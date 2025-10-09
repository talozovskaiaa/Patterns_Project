package ru.netology.delivery.page;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.Keys;
import ru.netology.delivery.data.DataGenerator;

import java.time.Duration;

import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;

public class DeliveryForm {

    private final SelenideElement cityInputField =  $("[data-test-id=city] input");
    private final SelenideElement dateInputField =  $("[data-test-id=date] input");
    private final SelenideElement nameInputField =  $("[data-test-id=name] input");
    private final SelenideElement phoneInputField = $("[data-test-id=phone] input");
    private final SelenideElement agreementCheckbox =  $("[data-test-id=agreement]");
    private final SelenideElement notifications = $(".notification__content");
    private final SelenideElement replanNotifications = $("[data-test-id='replan-notification'] .notification__content");
    private final SelenideElement replanButton = $("[data-test-id='replan-notification'] button");


    public void fillingAndSendingForm(DataGenerator.Registration.UserInfo user, String meetingDate) {
        cityInputField.setValue(user.getCity());
        clearDateField();
        dateInputField.setValue(meetingDate);
        nameInputField.setValue(user.getName());
        phoneInputField.setValue(user.getPhone());
        agreementCheckbox.click();
        submitForm();
    }

    public void clearDateField() {
        dateInputField.sendKeys(Keys.chord(Keys.SHIFT, Keys.HOME), Keys.BACK_SPACE);
    }

    public void submitForm() {
        $(byText("Запланировать")).click();
    }

    public void checkSuccessNotification(String expectedMessage) {
        notifications.shouldHave(Condition.partialText(expectedMessage)).shouldBe(Condition.visible, Duration.ofSeconds(5));
    }

    public void checkSuccessReplanNotifacation(String expectedMessage) {
        replanNotifications.shouldHave(Condition.partialText(expectedMessage)).shouldBe(Condition.visible, Duration.ofSeconds(5));
    }

    public void setDateInputField(String data) {
        dateInputField.setValue(data);
    }

    public void replanButton() {
        replanButton.click();
    }
}
