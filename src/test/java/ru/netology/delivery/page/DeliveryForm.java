package ru.netology.delivery.page;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import org.junit.jupiter.api.Assertions;
import ru.netology.delivery.data.DataGenerator;

public class DeliveryForm {

    private final Page page;

    private final Locator cityInputField;
    private final Locator dateInputField;
    private final Locator nameInputField;
    private final Locator phoneInputField;
    private final Locator agreementCheckbox;
    private final Locator submitButton;
    private final Locator notifications;
    private final Locator replanNotifications;
    private final Locator replanButton;


    public DeliveryForm(Page page) {
        this.page = page;

        this.cityInputField = page.locator("[data-test-id=city] input");
        this.dateInputField = page.locator("[data-test-id=date] input");
        this.nameInputField = page.locator("[data-test-id=name] input");
        this.phoneInputField = page.locator("[data-test-id=phone] input");
        this.agreementCheckbox = page.locator("[data-test-id=agreement]");
        this.submitButton = page.locator("button:has-text('Запланировать')");
        this.notifications = page.locator("[data-test-id='success-notification'] .notification__content");
        this.replanNotifications = page.locator("[data-test-id='replan-notification'] .notification__content");
        this.replanButton = page.locator("[data-test-id='replan-notification'] button:has-text('Перепланировать')");



    }

    public void fillingAndSendingForm(DataGenerator.Registration.UserInfo user, String meetingDate) {
        cityInputField.fill(user.getCity());
        clearDataField();
        dateInputField.fill(meetingDate);
        nameInputField.fill(user.getName());
        phoneInputField.fill(user.getPhone());
        agreementCheckbox.click();
        submitForm();
    }

    public void clearDataField() {
        dateInputField.fill("");
    }

    public void submitForm() {
        submitButton.click();
    }

    public void checkSuccessNotification(String expectedMessage) {
        notifications.waitFor();
        String actualText = notifications.innerText();
        Assertions.assertTrue(actualText.contains(expectedMessage));
    }

    public void setDate(String data) {
        dateInputField.fill(data);
    }

    public void checkReplanNotification(String expectedMessage) {
        replanNotifications.waitFor();
        String actualText = replanNotifications.innerText();
        Assertions.assertTrue(actualText.contains(expectedMessage));
    }

    public void clickReplanButton() {
        replanButton.click();
    }
}