package ru.netology.delivery.test;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import ru.netology.delivery.data.DataGenerator;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DeliveryTests {
    private WebDriver driver;
    private WebDriverWait wait;

    @BeforeAll
    static void setUpAll() {
        WebDriverManager.chromedriver().setup();
    }

    @BeforeEach
    void setUp() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--no-sandbox");
        options.addArguments("--headless");
        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(15));

        driver.get("http://localhost:9999");
    }

    @AfterEach
    void tearDown() {
        if (driver != null) {
            driver.quit();
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

        // Заполняем форму для первой встречи
        fillForm(validUser, firstMeetingDate);

        // Проверяем данные перед отправкой
        checkFormDataBeforeSubmission(validUser, firstMeetingDate);

        // Отправляем форму
        submitForm();

        // Проверяем успешное планирование первой встречи
        wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.cssSelector("[data-test-id='success-notification']")
                )
        );

        // Проверяем текст уведомления
        checkSuccessNotification(firstMeetingDate);

        // Перепланирование на вторую дату
        clearDateField();
        setNewDate(secondMeetingDate);

        // Проверяем данные перед повторной отправкой
        checkFormDataBeforeSubmission(validUser, secondMeetingDate);

        submitForm();

        // Проверяем уведомление о перепланировании
        wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.cssSelector("[data-test-id=replan-notification]")
                )
        );

        // Подтверждаем перепланирование
        confirmReplan();

        // Проверяем успешное перепланирование
        wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.cssSelector("[data-test-id='success-notification']")
                )
        );

        checkSuccessNotification(secondMeetingDate);
    }

    // Метод для проверки данных формы перед отправкой
    private void checkFormDataBeforeSubmission(DataGenerator.Registration.UserInfo user, String meetingDate) {
        System.out.println("=== FORM DATA VERIFICATION BEFORE SUBMISSION ===");

        // Получаем текущие значения из полей формы
        String actualCity = driver.findElement(By.cssSelector("[data-test-id=city] input")).getAttribute("value");
        String actualDate = driver.findElement(By.cssSelector("[data-test-id=date] input")).getAttribute("value");
        String actualName = driver.findElement(By.cssSelector("[data-test-id=name] input")).getAttribute("value");
        String actualPhone = driver.findElement(By.cssSelector("[data-test-id=phone] input")).getAttribute("value");
        boolean isAgreementChecked = driver.findElement(By.cssSelector("[data-test-id=agreement] input"))
                .isSelected();

        // Выводим фактические значения для отладки
        System.out.println("Actual city: '" + actualCity + "'");
        System.out.println("Expected city: '" + user.getCity() + "'");
        System.out.println("Actual date: '" + actualDate + "'");
        System.out.println("Expected date: '" + meetingDate + "'");
        System.out.println("Actual name: '" + actualName + "'");
        System.out.println("Expected name: '" + user.getName() + "'");
        System.out.println("Actual phone: '" + actualPhone + "'");
        System.out.println("Expected phone: '" + user.getPhone() + "'");
        System.out.println("Agreement checked: " + isAgreementChecked);

        // Проверяем соответствие данных (без проверки форматов)
        assertEquals(user.getCity(), actualCity, "Город заполнен некорректно");
        assertTrue(!actualDate.isEmpty(), "Дата не заполнена");
        assertEquals(user.getName(), actualName, "Имя заполнено некорректно");
        assertTrue(!actualPhone.isEmpty(), "Телефон не заполнен");
        assertTrue(isAgreementChecked, "Чекбокс соглашения не отмечен");

        System.out.println("=== FORM DATA VERIFICATION PASSED ===");
    }

    // Метод заполнение формы
    private void fillForm(DataGenerator.Registration.UserInfo user, String meetingDate) {
        // Заполняем город
        WebElement cityInput = driver.findElement(By.cssSelector("[data-test-id=city] input"));
        cityInput.sendKeys(user.getCity());

        // Заполняем дату в формате дд.мм.гггг
        WebElement dateInput = driver.findElement(By.cssSelector("[data-test-id=date] input"));

        // Очищаем поле
        dateInput.sendKeys(Keys.chord(Keys.CONTROL, "a"));
        dateInput.sendKeys(Keys.DELETE);

        // Вводим дату
        dateInput.sendKeys(meetingDate);

        // Заполняем имя
        WebElement nameInput = driver.findElement(By.cssSelector("[data-test-id=name] input"));
        nameInput.sendKeys(user.getName());

        // Заполняем телефон
        WebElement phoneInput = driver.findElement(By.cssSelector("[data-test-id=phone] input"));
        phoneInput.sendKeys(user.getPhone());

        // Отмечаем чекбокс
        WebElement agreementCheckbox = driver.findElement(By.cssSelector("[data-test-id=agreement]"));
        if (!agreementCheckbox.isSelected()) {
            agreementCheckbox.click();
        }
    }

    // метод отправки формы
    private void submitForm() {
        WebElement submitButton = driver.findElement(By.cssSelector("button.button"));
        submitButton.click();
    }

    // метод очистки формы
    private void clearDateField() {
        WebElement dateInput = driver.findElement(By.cssSelector("[data-test-id=date] input"));
        dateInput.sendKeys(Keys.chord(Keys.CONTROL, "a"));
        dateInput.sendKeys(Keys.DELETE);
    }

    // метод для новой даты (не помогло)
    private void setNewDate(String newDate) {
        WebElement dateInput = driver.findElement(By.cssSelector("[data-test-id=date] input"));
        dateInput.sendKeys(newDate);
    }

    // метод для проверки уведомления
    private void checkSuccessNotification(String expectedDate) {
        WebElement notificationContent = driver.findElement(
                By.cssSelector("[data-test-id='success-notification'] .notification__content")
        );
        String expectedText = "Встреча успешно запланирована на " + expectedDate;
        assertEquals(expectedText, notificationContent.getText().trim());
    }

    // метод подтверждение перепланирования
    private void confirmReplan() {
        WebElement replanButton = driver.findElement(
                By.cssSelector("[data-test-id=replan-notification] button")
        );
        replanButton.click();
    }
}