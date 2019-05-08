package jmri.web;

import cucumber.api.java8.En;
import java.io.File;
import jmri.InstanceManager;
import jmri.ConfigureManager;
import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.events.EventFiringWebDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * Cucumber step definitions for Web Server Acceptance tests.
 *
 * @author Paul Bender Copyright (C) 2017
 */
public class WebServerAcceptanceSteps implements En {

    private EventFiringWebDriver webDriver;

    String[] firefoxtags = {"@webtest", "@firefox"};
    String[] chrometags = {"@webtest", "@chrome"};
    String[] tags = {"@webtest"};

    public WebServerAcceptanceSteps(jmri.InstanceManager instance) {

        Given("^I am using firefox$", () -> {
            webDriver = jmri.util.web.BrowserFactory.getBrowser("Firefox");
        });

        Given("^I am using chrome$", () -> {
            webDriver = jmri.util.web.BrowserFactory.getBrowser("Chrome");
        });

        When("^I ask for the url (.*)$", (String url) -> {
            webDriver.get(url);
        });

        Given("^panel (.*) is loaded$", (String path) -> {
            InstanceManager.getDefault(ConfigureManager.class)
                .load(new File(path));
        });

        Then("^a page with title (.*) is returned$", (String pageTitle) -> {
            WebDriverWait wait = new WebDriverWait(webDriver, 10);
            wait.until(new ExpectedCondition<Boolean>() {
                // this ExpectedCondition code is derived from code posted by user 
                // Jeff Vincent to
                // https://stackoverflow.com/questions/12858972/how-can-i-ask-the-selenium-webdriver-to-wait-for-few-seconds-in-java
                @Override
                public Boolean apply(WebDriver driver) {
                    String script =
                            "if (typeof window != 'undefined' && window.document) { return window.document.readyState; } else { return 'notready'; }";
                    Boolean result = Boolean.FALSE;
                    if (driver != null) {
                        try {
                            result = ((JavascriptExecutor) driver).executeScript(script).equals("complete");
                        } catch (Exception ex) {
                            // nothing to do, but silence the error
                        }
                    }
                    return result;
                }
            });
            Assert.assertEquals("Page Title", pageTitle, webDriver.getTitle());
        });


        After(tags, () -> {
           // navigate back home to prevent the webpage from reloading.
           webDriver.get("http://localhost:12080/");
           jmri.util.JUnitUtil.closeAllPanels();
        });

    }
}
