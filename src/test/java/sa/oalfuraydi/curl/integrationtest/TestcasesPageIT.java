package sa.oalfuraydi.curl.integrationtest;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.regex.Pattern;

import org.eclipse.microprofile.config.ConfigProvider;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.utility.DockerImageName;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.options.AriaRole;

import io.quarkus.logging.Log;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.h2.H2DatabaseTestResource;
import io.quarkus.test.junit.QuarkusIntegrationTest;
import io.quarkus.test.junit.TestProfile;

@QuarkusTestResource(H2DatabaseTestResource.class)
@QuarkusIntegrationTest
@TestProfile(InMemoryTestProfile.class)
public class TestcasesPageIT {

    // @ConfigProperty(name = "quarkus.http.test-port")
    static Integer port;
    String restPage;
    String testcasePage;

    // Shared between all tests in this class.
    static Playwright playwright;
    static Browser browser;
    String requestUrl;
    String body = """
                 {
                 "title": "Test",
                "body": "rest client test",
            "userId": "2"
             }
               """;
    String response;
    String status;
    String responseHeader;
    String testNumber;
    static Integer mappedPort;
    @Container
    static GenericContainer jsonContainer = new GenericContainer(DockerImageName.parse("svenwal/jsonplaceholder"))
            .withExposedPorts(3000);

    @BeforeAll
    static void launchBrowser() {
        port = ConfigProvider.getConfig().getValue("quarkus.http.test-port", Integer.class);
        jsonContainer.start();
        playwright = Playwright.create();
        browser = playwright.chromium().launch();
        mappedPort = jsonContainer.getMappedPort(3000);

    }

    @AfterAll
    static void closeBrowser() {
        playwright.close();
        jsonContainer.stop();
    }

    @BeforeEach
    public void init() {

        Page page = browser.newPage();
        String testDomain = "http://localhost:" + port;
        restPage = testDomain + "/rest";
        testcasePage = testDomain + "/";
        String testName = "PostRequestTestcasesPage";

        requestUrl = "http://localhost:" + mappedPort + "/posts";
        page.navigate(restPage);
        page.getByLabel(Pattern.compile("Collection")).fill(testName);
        page.getByLabel("URL:").fill(requestUrl);
        page.getByLabel("HTTP Method:").selectOption("POST");
        page.getByLabel("Request Headers:").fill("Content-Type: application/json");
        page.locator("#body").fill(body);
        page.locator("#sendButton").click();
        Locator statusLocator = page.locator("#status");

        Locator responseHeaderLocator = page.locator("#responseHeader");
        Locator responseLocator = page.locator("#response");
        assertThat(statusLocator).hasValue("201 Created");
        status = statusLocator.textContent();
        responseHeader = responseHeaderLocator.textContent();
        response = responseLocator.textContent();
        assertThat(responseHeaderLocator).containsText("date");
        assertThat(responseLocator).containsText("rest client test");

        page.locator("#savebutton").click();
        Locator saveresponseLocator = page.locator("#saveresponse");
        assertThat(saveresponseLocator).containsText("TestCase Saved:");

        String[] testCaseid = saveresponseLocator.textContent().split("TestCase Saved: id=");
        testNumber = testCaseid[1];

    }

    @Test
    @DisplayName("Heading")
    public void Heading() {
        Page page = browser.newPage();
        page.navigate("http://localhost:" + port + "/");
        // page.screenshot(new
        // Page.ScreenshotOptions().setPath(Paths.get("screenshot.png")));
        String actualHeading = page.locator("h1").allInnerTexts().get(0);
        assertThat(page).hasTitle(Pattern.compile("TestCases"));
        assertEquals("REST API Client - TestCases", actualHeading);
        assertThat(page.locator("#testcasesTable")).isVisible();
    }

    @Test
    @DisplayName("DeleteTestCase")
    public void DeleteTestCase() {
        Page page = browser.newPage();
        page.navigate(testcasePage);
        page.waitForSelector("#testcasesTable");

        Locator actualrow = page.locator("#tc" + testNumber);
        page.onDialog(dialog -> {
            dialog.accept();
        });
   
        Locator deleteButton = actualrow.getByRole(AriaRole.BUTTON, new Locator.GetByRoleOptions().setName("Delete"));
        deleteButton.click();

        assertThat(actualrow).not().isVisible();
    }

    @Test
    @DisplayName("LoadTestCase")
    public void LoadTestCase() {
        Page page = browser.newPage();
        page.navigate(testcasePage);
        page.waitForSelector("#testcasesTable");

        Locator actualrow = page.locator("#tc" + testNumber);

        page.onDialog(dialog -> {
            dialog.accept();
        });

        actualrow.getByRole(AriaRole.BUTTON, new Locator.GetByRoleOptions().setName("Load")).click();
        // assertThat(actualrow).not().isVisible();
        assertThat(page.getByLabel("URL:")).hasValue(requestUrl);
        assertThat(page.getByLabel("HTTP Method:")).hasValue("POST");
        assertThat(page.getByLabel("Request Headers:")).hasValue("Content-Type: application/json");
        assertThat(page.locator("#body")).hasValue(body);
        assertThat(page.getByLabel("Collection")).hasValue("PostRequestTestcasesPage");
        assertThat(page.locator("#status")).hasValue(status);
        assertThat(page.locator("#responseHeader")).hasValue(responseHeader);
        assertThat(page.locator("#response")).hasValue(response);

    }
}
