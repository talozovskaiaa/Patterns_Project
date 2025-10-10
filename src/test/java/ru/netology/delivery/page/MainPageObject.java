package ru.netology.delivery.page;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;

public class MainPageObject {

    Playwright playwright;
    Browser browser;
    Page page;

    public Page setUP() {

        playwright = Playwright.create();
        browser = playwright.chromium().launch(new BrowserType.LaunchOptions()
                .setHeadless(false));
        page = browser.newPage();
        page.navigate("http://localhost:9999");
        page.setDefaultTimeout(5000);
        return page;
    }

    public Page getPage() {
        return page;
    }

    public Browser getBrowser() {
        return browser;
    }

    public Playwright getPlaywright() {
        return playwright;
    }

    public void tearDown() {
        if (browser != null) {
            browser.close();
        }
        if (playwright != null) {
            playwright.close();
        }
    }
}
