package ru.netology.delivery.test;

import com.microsoft.playwright.*;
import org.junit.jupiter.api.*;
import ru.netology.delivery.data.DataGenerator;

class DeliveryTests {

    Playwright playwright;
    Browser browser;
    Page page;

    @BeforeEach
    public void setup() {
        playwright = Playwright.create();
        browser = playwright.chromium().launch(new BrowserType.LaunchOptions()
                .setHeadless(false));
        page = browser.newPage();
        page.navigate("http://localhost:9999");
        page.setDefaultTimeout(10000);
    }

    @AfterEach
    public void tearDown() {
        if (browser != null) {
            browser.close();
        }
        if (playwright != null) {
            playwright.close();
        }
    }

    @Test
    @DisplayName("Should successful plan and replan meeting")
    void shouldSuccessfulPlanAndReplanMeeting() {
        DataGenerator.Registration.UserInfo validUser = DataGenerator.Registration.generateUser("ru");
        var daysToAddForFirstMeeting = 4;
        var firstMeetingDate = DataGenerator.generateDate(daysToAddForFirstMeeting);
        var daysToAddForSecondMeeting = 7;
        var secondMeetingDate = DataGenerator.generateDate(daysToAddForSecondMeeting);

        page.locator("[data-test-id=city] input").fill(validUser.getCity());
        page.locator("[data-test-id=date] input").fill("");
        page.locator("[data-test-id=date] input").fill(firstMeetingDate);
        page.locator("[data-test-id=name] input").fill(validUser.getName());
        page.locator("[data-test-id=phone] input").fill(validUser.getPhone());
        page.locator("[data-test-id=agreement]").click();
        page.locator("button:has-text('Запланировать')").click();

        // Проверка первого уведомления
        page.locator("[data-test-id='success-notification']").waitFor();
        Assertions.assertTrue(page.innerText("[data-test-id='success-notification'] .notification__content").contains("Встреча успешно запланирована на"));

        page.locator("[data-test-id=date] input").fill("");
        page.locator("[data-test-id=date] input").fill(secondMeetingDate);
        page.locator("button:has-text('Запланировать')").click();

        // Проверка второго уведомления
        page.locator("[data-test-id='replan-notification']").waitFor();
        Assertions.assertTrue(page.innerText("[data-test-id='replan-notification'] .notification__content").contains("У вас уже запланирована встреча на другую дату. Перепланировать?"));

        page.locator("button:has-text('Перепланировать')").click();

        //Проверка третьего уведомления
        page.locator("[data-test-id='success-notification']").waitFor();
        Assertions.assertTrue(page.innerText("[data-test-id='success-notification'] .notification__content").contains("Встреча успешно запланирована на"));




    }
}