# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project overview

Selenium + TestNG UI automation suite for the BellaVita Luxury Shopify storefront
(`https://bvl---live-59c56cb80d07a4341b5e.o2.myshopify.dev`). Java 17 / Maven project. There is no
application source here — this repo only contains end-to-end browser tests.

## Commands

```bash
# Run the full suite (headed, launches visible Chrome)
mvn test

# Run headless (used in CI / Linux runners)
mvn test -Dheadless=true

# Run a single test class (bypasses testng.xml's suite definition)
mvn test -Dtest=SearchTest

# Run a single test method
mvn test -Dtest=SearchTest#verifyAddToCartFromSearchResult
```

Tests are wired together via `testng.xml` (referenced from `pom.xml`'s Surefire config), not via
Maven module conventions — adding a new test class requires registering it in `testng.xml`'s
`<classes>` block or it will never run as part of `mvn test`.

Reports/logs/screenshots are gitignored and regenerated per run:
- `reports/TestReport.html` — ExtentReports HTML report (dark theme)
- `screenshots/` — captured only on test failure, named `<testName>_<timestamp>.png`
- `logs/test-execution.log` — Log4j2 file output

## Architecture

**Page Object Model**, split by Maven scope:
- `src/test/java/org/OneGuardian/base/` — `BaseTest` (WebDriver lifecycle: creates/quits ChromeDriver
  per test method via `@BeforeMethod`/`@AfterMethod`) and `BasePage` (shared Selenium helpers: safe
  click with JS-click fallback, scroll helpers, explicit waits, stale-element retry, etc. — all page
  objects extend this).
- `src/test/java/org/OneGuardian/pages/` — one class per storefront page/section (`HomePage`,
  `CollectionPage`, `ProductDetailPage`, `CartPage`, `CrazyDealsPage`). Locators are `@FindBy` fields
  wired by `PageFactory` in the `BasePage` constructor; actions are public methods that call the
  `BasePage` helpers rather than raw Selenium calls.
- `src/test/java/org/OneGuardian/tests/` — TestNG test classes, one per feature area, extending
  `BaseTest`. Tests instantiate page objects directly (`new HomePage(driver)`), chain page actions,
  and assert with `org.testng.Assert`.
- `src/test/java/org/OneGuardian/utils/` — cross-cutting infrastructure:
  - `TestListener` (`ITestListener`) — drives ExtentReports per test (start/pass/fail/skip), attaches
    a screenshot on failure via `ScreenshotUtility`.
  - `RetryListener` (`IAnnotationTransformer`) + `RetryAnalyzer` (`IRetryAnalyzer`) — auto-attaches a
    1-retry policy to *every* `@Test` without needing `retryAnalyzer = ...` on each method. Both are
    registered globally as `<listeners>` in `testng.xml`.
  - `ExtentReportManager` — singleton `ExtentReports` instance (spark reporter, dark theme).
  - `ExcelUtility` — reads `.xlsx` files (Apache POI) into `Object[][]` for TestNG `@DataProvider`s.
  - `ScreenshotUtility` — captures a PNG from the live `driver` on failure.

**Data-driven tests**: search keyword cases come from `testdata/SearchData(1).xlsx` via
`ExcelUtility.getTestData(path, sheetName)`, consumed by a `@DataProvider` in `SearchTest`. Filter/sort
combinations in `FilterSortTest` use inline `Object[][]` `@DataProvider`s instead.

**Headless mode** (`-Dheadless=true`) is read via `System.getProperty` in both `BaseTest` (Chrome
launch args) and `BasePage` (bumps the explicit-wait timeout from 10s to 20s) — the storefront is
slower to render without a GPU, so timing-sensitive locators may need the longer headless timeout to
be reliable in CI even if they pass locally in headed mode.

**Selenium click strategy**: prefer `safeClick()` (waits for clickability, falls back to a JS click on
failure) over calling `.click()` directly on a `WebElement`; several storefront elements sit behind
overlays or animate in, and a raw `.click()` can throw `ElementClickInterceptedException` in headless
runs where headed runs succeed.

## CI

`.github/workflows/test-automation.yml` runs the suite daily via cron plus manual
`workflow_dispatch`, always headless. It uploads the ExtentReport and any screenshots as build
artifacts (`continue-on-error: true` so a test failure doesn't block the Slack step), then parses
`target/surefire-reports/testng-results.xml` and posts a pass/fail summary to Slack via
`SLACK_WEBHOOK_URL`.
