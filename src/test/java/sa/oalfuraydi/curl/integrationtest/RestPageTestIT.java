package sa.oalfuraydi.curl.integrationtest;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.regex.Pattern;

import org.eclipse.microprofile.config.ConfigProvider;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.utility.DockerImageName;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.options.AriaRole;

import io.quarkus.test.junit.QuarkusIntegrationTest;

@TestInstance(Lifecycle.PER_CLASS)
@QuarkusIntegrationTest
public class RestPageTestIT {

  @Container
  static GenericContainer jsonContainer = new GenericContainer(DockerImageName.parse("svenwal/jsonplaceholder"))
      .withExposedPorts(3000);

  // @ConfigProperty(name = "quarkus.http.test-port",defaultValue = "8083")
  static Integer port;

  String restPage;
  String testcasePage;
  // Shared between all tests in this class.
  static Playwright playwright;
  static Browser browser;
  static Integer mappedPort;

  @BeforeEach
  void setup() {
    String testDomain = "http://localhost:" + port;
    restPage = testDomain + "/rest";
    testcasePage = testDomain + "/testcases";
  }

  @BeforeAll
  static void launchBrowser() {
    port = ConfigProvider.getConfig().getValue("quarkus.http.test-port", Integer.class);

    jsonContainer.start();
    playwright = Playwright.create();
    browser = playwright.chromium().launch();
    mappedPort = jsonContainer.getMappedPort(3000);

  }

  @AfterAll
  void AfterAll() {
    jsonContainer.stop();
    playwright.close();
  }

  @Test
  @DisplayName("Heading")
  public void Heading() {
    Page page = browser.newPage();
    page.navigate(restPage);
    String actualHeading = page.locator("h1").allInnerTexts().get(0);
    assertThat(page).hasTitle(Pattern.compile("REST API Client"));
    assertEquals("REST API Client", actualHeading);
    assertThat(page.locator("#mainform")).isVisible();

  }

  @Test
  @DisplayName("GetRequest")
  public void GetRequest() {
    String testName = "GetRequest";
    Page page = browser.newPage();

    String requestUrl = "http://localhost:" + mappedPort + "/todos/1";
    page.navigate(restPage);
    page.getByLabel(Pattern.compile("Collection")).fill(testName);
    page.getByLabel("URL:").fill(requestUrl);
    page.getByLabel("HTTP Method:").selectOption("GET");
    page.getByLabel("Request Headers:").fill("Content-Type: application/json");
    page.locator("#sendButton").click();
    Locator statusLocator = page.locator("#status");
    Locator responseHeaderLocator = page.locator("#responseHeader");
    Locator responseLocator = page.locator("#response");
    assertThat(statusLocator).hasValue("200 OK");
    String status = statusLocator.textContent();

    assertThat(responseHeaderLocator).containsText("date");
    assertThat(responseLocator).containsText("userId");

    page.locator("#savebutton").click();
    Locator saveresponseLocator = page.locator("#saveresponse");
    assertThat(saveresponseLocator).containsText("TestCase Saved:");

    String[] testCaseid = saveresponseLocator.textContent().split("TestCase Saved: id=");

    page.navigate(testcasePage);

    Locator actualrow = page.locator("#tc" + testCaseid[1]);
    var cells = actualrow.getByRole(AriaRole.CELL).all();

    assertEquals(testCaseid[1], cells.get(0).textContent(), "id assert");

    assertEquals(testName, cells.get(1).textContent(), "Collection assert");
    assertEquals("GET", cells.get(2).textContent(), "Verb assert");
    assertEquals(requestUrl, cells.get(3).textContent(), "URI assert");
    assertEquals(status, cells.get(4).textContent(), "Status assert");
  }

  @Test
  @DisplayName("PostRequest")
  public void PostRequest() {
    String testName = "PostRequest";
    String body = """
             {
             "title": "Test",
            "body": "rest client test",
        "userId": "2"
         }
           """;
    Page page = browser.newPage();

    String url = "http://localhost:" + mappedPort + "/posts";
    page.navigate(restPage);
    page.getByLabel(Pattern.compile("Collection")).fill(testName);
    page.getByLabel("URL:").fill(url);
    page.getByLabel("HTTP Method:").selectOption("POST");
    page.getByLabel("Request Headers:").fill("Content-Type: application/json");
    page.locator("#body").fill(body);
    page.locator("#sendButton").click();
    Locator statusLocator = page.locator("#status");
    Locator responseHeaderLocator = page.locator("#responseHeader");
    Locator responseLocator = page.locator("#response");
    assertThat(statusLocator).hasValue("201 Created");
    String status = statusLocator.textContent();

    assertThat(responseHeaderLocator).containsText("date");
    assertThat(responseLocator).containsText("rest client test");

    page.locator("#savebutton").click();
    Locator saveresponseLocator = page.locator("#saveresponse");
    assertThat(saveresponseLocator).containsText("TestCase Saved:");

    String[] testCaseid = saveresponseLocator.textContent().split("TestCase Saved: id=");

    page.navigate(testcasePage);

    Locator table = page.locator("#testcasesTable");
    Locator actualrow = table.getByRole(AriaRole.ROW).filter(new Locator.FilterOptions().setHasText(testName));

    var cells = actualrow.getByRole(AriaRole.CELL).all();

    assertEquals(testCaseid[1], cells.get(0).textContent(), "id assert");
    assertEquals(testName, cells.get(1).textContent(), "Collection assert");
    assertEquals("POST", cells.get(2).textContent(), "Verb assert");
    assertEquals(url, cells.get(3).textContent(), "URI assert");
    assertEquals(status, cells.get(4).textContent(), "Status assert");
  }

  @Test
  @DisplayName("PatchRequest")
  public void PatchRequest() {
    String testName = "PatchRequest";
    String body = """
        {                 "userId": 1,
                           "title": "Test",
                          "body": "rest client test",
                      "userId": "2"
                       }
                                       """;

    Page page = browser.newPage();

    String url = "http://localhost:" + mappedPort + "/posts/2";
    page.navigate(restPage);
    page.getByLabel(Pattern.compile("Collection")).fill(testName);
    page.getByLabel("URL:").fill(url);
    page.getByLabel("HTTP Method:").selectOption("PATCH");
    page.getByLabel("Request Headers:").fill("Content-Type: application/json");
    page.locator("#body").fill(body);
    page.locator("#sendButton").click();
    Locator statusLocator = page.locator("#status");
    Locator responseHeaderLocator = page.locator("#responseHeader");
    Locator responseLocator = page.locator("#response");
    assertThat(statusLocator).hasValue("200 OK");
    String status = statusLocator.textContent();

    assertThat(responseHeaderLocator).containsText("date");
    assertThat(responseLocator).containsText("rest client test");

    page.locator("#savebutton").click();
    Locator saveresponseLocator = page.locator("#saveresponse");
    assertThat(saveresponseLocator).containsText("TestCase Saved:");

    String[] testCaseid = saveresponseLocator.textContent().split("TestCase Saved: id=");

    page.navigate(testcasePage);

    Locator actualrow = page.locator("#tc" + testCaseid[1]);

    var cells = actualrow.getByRole(AriaRole.CELL).all();

    assertEquals(testCaseid[1], cells.get(0).textContent(), "id assert");
    assertEquals(testName, cells.get(1).textContent(), "Collection assert");
    assertEquals("PATCH", cells.get(2).textContent(), "Verb assert");
    assertEquals(url, cells.get(3).textContent(), "URI assert");
    assertEquals(status, cells.get(4).textContent(), "Status assert");
  }

  @Test
  @DisplayName("DeleteRequest")
  public void DeleteRequest() {
    String testName = "DeleteRequest";
    Page page = browser.newPage();

    String url = "http://localhost:" + mappedPort + "/posts/2";
    page.navigate(restPage);
    page.getByLabel(Pattern.compile("Collection")).fill(testName);
    page.getByLabel("URL:").fill(url);
    page.getByLabel("HTTP Method:").selectOption("DELETE");
    page.getByLabel("Request Headers:").fill("Content-Type: application/json");
    page.locator("#sendButton").click();
    Locator statusLocator = page.locator("#status");
    Locator responseHeaderLocator = page.locator("#responseHeader");

    assertThat(statusLocator).hasValue("200 OK");
    String status = statusLocator.textContent();

    assertThat(responseHeaderLocator).containsText("date");

    page.locator("#savebutton").click();
    Locator saveresponseLocator = page.locator("#saveresponse");
    assertThat(saveresponseLocator).containsText("TestCase Saved:");

    String[] testCaseid = saveresponseLocator.textContent().split("TestCase Saved: id=");

    page.navigate(testcasePage);

    Locator actualrow = page.locator("#tc" + testCaseid[1]);
    var cells = actualrow.getByRole(AriaRole.CELL).all();

    assertEquals(testCaseid[1], cells.get(0).textContent(), "id assert");

    assertEquals(testName, cells.get(1).textContent(), "Collection assert");
    assertEquals("DELETE", cells.get(2).textContent(), "Verb assert");
    assertEquals(url, cells.get(3).textContent(), "URI assert");
    assertEquals(status, cells.get(4).textContent(), "Status assert");
  }

}
