package org.OneGuardian.utils;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.gmail.GmailScopes;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * One-off utility to mint a fresh Google OAuth refresh token for {@link GmailOtpReader}.
 *
 * <p>The refresh token stored in the {@code GOOGLE_REFRESH_TOKEN} GitHub secret / local {@code .env}
 * periodically stops working — Google returns {@code invalid_grant} ("Token has been expired or
 * revoked"). The usual cause is an OAuth consent screen left in <b>Testing</b> publishing status,
 * for which Google expires refresh tokens after 7 days. Publishing the app to <b>In production</b>
 * stops the expiry; until then (and any time the token is revoked) run this to generate a new one.
 *
 * <h3>How to run</h3>
 * <ol>
 *   <li>Ensure {@code GOOGLE_CLIENT_ID} and {@code GOOGLE_CLIENT_SECRET} are set in the project-root
 *       {@code .env} (they already are for this suite) or as environment variables. The OAuth client
 *       must be a <b>Desktop app</b> type so the loopback redirect below is accepted.</li>
 *   <li>Run this class from the project root:
 *       <pre>mvn test-compile
 *mvn exec:java -Dexec.mainClass=org.OneGuardian.utils.GoogleRefreshTokenGenerator -Dexec.classpathScope=test</pre>
 *       or run its {@code main} from your IDE.</li>
 *   <li>A browser opens for Google consent — sign in as the inbox that receives the OTP mail and
 *       approve. On approval the new refresh token is printed to the console.</li>
 *   <li>Copy it into the GitHub secret {@code GOOGLE_REFRESH_TOKEN} (Settings → Secrets and
 *       variables → Actions) and into your local {@code .env}.</li>
 * </ol>
 *
 * <p>{@code access_type=offline} + {@code approval_prompt=force} guarantee Google returns a refresh
 * token (it otherwise omits it on repeat consent). Read-only Gmail scope is requested.
 */
public class GoogleRefreshTokenGenerator {

    private static final GsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();

    public static void main(String[] args) throws Exception {
        Map<String, String> env = loadEnvFile(System.getProperty("gmail.env.path", ".env"));
        String clientId = env.getOrDefault("GOOGLE_CLIENT_ID", System.getenv("GOOGLE_CLIENT_ID"));
        String clientSecret = env.getOrDefault("GOOGLE_CLIENT_SECRET", System.getenv("GOOGLE_CLIENT_SECRET"));

        if (clientId == null || clientId.isBlank() || clientSecret == null || clientSecret.isBlank()) {
            System.err.println("GOOGLE_CLIENT_ID / GOOGLE_CLIENT_SECRET not found in .env or environment. "
                    + "Set them (from your OAuth Desktop client in Google Cloud Console) and run again.");
            System.exit(1);
        }

        NetHttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                httpTransport, JSON_FACTORY, clientId, clientSecret,
                Collections.singletonList(GmailScopes.GMAIL_READONLY))
                .setAccessType("offline")
                .setApprovalPrompt("force")
                .build();

        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
        Credential credential = new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");

        String refreshToken = credential.getRefreshToken();
        System.out.println("\n============================================================");
        if (refreshToken == null || refreshToken.isBlank()) {
            System.out.println("No refresh token returned. Revoke the app's access at "
                    + "https://myaccount.google.com/permissions and run this again so Google re-prompts consent.");
        } else {
            System.out.println("New GOOGLE_REFRESH_TOKEN:\n" + refreshToken);
            System.out.println("\nUpdate the GitHub secret GOOGLE_REFRESH_TOKEN and your local .env with the value above.");
        }
        System.out.println("============================================================");
        System.exit(0);
    }

    private static Map<String, String> loadEnvFile(String path) {
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
            System.err.println("Failed reading env file " + path + ": " + e.getMessage());
        }
        return values;
    }
}
