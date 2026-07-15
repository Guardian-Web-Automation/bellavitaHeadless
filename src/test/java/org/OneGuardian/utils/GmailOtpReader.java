package org.OneGuardian.utils;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.ListMessagesResponse;
import com.google.api.services.gmail.model.Message;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Reads the GoKwik login-OTP mail delivered to a Gmail inbox during checkout automation.
 * Authenticates with a pre-issued Google OAuth refresh token (GOOGLE_CLIENT_ID / GOOGLE_CLIENT_SECRET /
 * GOOGLE_REFRESH_TOKEN, read from a project-root .env or environment variables) rather than an
 * interactive consent flow, so it also runs headlessly in CI.
 */
public class GmailOtpReader {

    private static final Logger log = LogManager.getLogger(GmailOtpReader.class);
    private static final String APPLICATION_NAME = "BellaVita QA OTP Reader";
    private static final GsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private static final Pattern OTP_PATTERN = Pattern.compile("(\\d{4,6})\\s+is your login OTP");

    private final Gmail gmailService;

    public GmailOtpReader() {
        try {
            this.gmailService = buildGmailService();
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize Gmail API client: " + e.getMessage(), e);
        }
    }

    private Gmail buildGmailService() throws Exception {
        Map<String, String> env = loadEnvFile(System.getProperty("gmail.env.path", ".env"));

        String clientId = env.getOrDefault("GOOGLE_CLIENT_ID", System.getenv("GOOGLE_CLIENT_ID"));
        String clientSecret = env.getOrDefault("GOOGLE_CLIENT_SECRET", System.getenv("GOOGLE_CLIENT_SECRET"));
        String refreshToken = env.getOrDefault("GOOGLE_REFRESH_TOKEN", System.getenv("GOOGLE_REFRESH_TOKEN"));

        StringBuilder missing = new StringBuilder();
        if (clientId == null || clientId.isBlank()) missing.append("GOOGLE_CLIENT_ID ");
        if (clientSecret == null || clientSecret.isBlank()) missing.append("GOOGLE_CLIENT_SECRET ");
        if (refreshToken == null || refreshToken.isBlank()) missing.append("GOOGLE_REFRESH_TOKEN ");
        if (missing.length() > 0) {
            throw new IllegalStateException(missing.toString().trim()
                    + " not set (or blank) via .env or environment variables");
        }

        NetHttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
        GoogleCredential credential = new GoogleCredential.Builder()
                .setTransport(httpTransport)
                .setJsonFactory(JSON_FACTORY)
                .setClientSecrets(clientId, clientSecret)
                .build()
                .setRefreshToken(refreshToken);
        credential.refreshToken();

        return new Gmail.Builder(httpTransport, JSON_FACTORY, credential)
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

    private Map<String, String> loadEnvFile(String path) {
        Map<String, String> values = new HashMap<>();
        File file = new File(path);
        if (!file.exists()) {
            return values;
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }
                int eq = line.indexOf('=');
                if (eq > 0) {
                    values.put(line.substring(0, eq).trim(), line.substring(eq + 1).trim());
                }
            }
        } catch (Exception e) {
            log.warn("Failed reading env file " + path + ": " + e.getMessage());
        }
        return values;
    }

    /**
     * Polls Gmail for the latest OTP mail from senderEmail received after searchAfterEpochSeconds.
     * GoKwik reuses the same subject line for OTP and order-confirmation mails, so messages are
     * told apart by matching the OTP wording in the body rather than the subject.
     */
    public String waitForOtp(String senderEmail, long searchAfterEpochSeconds, Duration timeout) {
        Instant deadline = Instant.now().plus(timeout);
        String query = "from:" + senderEmail + " after:" + searchAfterEpochSeconds;

        while (Instant.now().isBefore(deadline)) {
            try {
                ListMessagesResponse response = gmailService.users().messages()
                        .list("me")
                        .setQ(query)
                        .setMaxResults(5L)
                        .execute();

                List<Message> messages = response.getMessages();
                if (messages != null) {
                    for (Message summary : messages) {
                        Message full = gmailService.users().messages().get("me", summary.getId()).execute();
                        String snippet = full.getSnippet();
                        if (snippet != null) {
                            Matcher matcher = OTP_PATTERN.matcher(snippet);
                            if (matcher.find()) {
                                String otp = matcher.group(1);
                                log.info("Found OTP in email from {}", senderEmail);
                                return otp;
                            }
                        }
                    }
                }
            } catch (Exception e) {
                log.warn("Error polling Gmail for OTP: " + e.getMessage());
            }

            try {
                Thread.sleep(3000);
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
                break;
            }
        }

        throw new RuntimeException("OTP email from " + senderEmail + " not received within " + timeout.getSeconds() + "s");
    }
}
