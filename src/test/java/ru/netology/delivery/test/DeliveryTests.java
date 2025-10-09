package ru.netology.delivery.test;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Configuration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.*;

import org.openqa.selenium.Keys;
import ru.netology.delivery.data.DataGenerator;
import ru.netology.delivery.page.DeliveryForm;
import ru.netology.delivery.page.MainPageObject;

import java.time.Duration;

class DeliveryTests {

    @BeforeEach
    void setup() {
        Configuration.headless = false;

        MainPageObject mainPageObject = new MainPageObject();
        mainPageObject.openURL();
    }

    @Test
    @DisplayName("Selenide Tests with Page Object")
    void shouldSuccessfulPlanAndReplanMeetingWithPageObject () {
        DataGenerator.Registration.UserInfo validUser = DataGenerator.Registration.generateUser("ru");
        int daysToAddForFirstMeeting = 4;
        String firstMeetingDate = DataGenerator.generateDate(daysToAddForFirstMeeting);
        int daysToAddForSecondMeeting = 7;
        String secondMeetingDate = DataGenerator.generateDate(daysToAddForSecondMeeting);

        DeliveryForm deliveryForm = new DeliveryForm();
        deliveryForm.fillingAndSendingForm(validUser, firstMeetingDate);
        deliveryForm.checkSuccessNotification("Встреча успешно запланирована");
        deliveryForm.clearDateField();
        deliveryForm.setDateInputField(secondMeetingDate);
        deliveryForm.submitForm();
        deliveryForm.checkSuccessReplanNotifacation("У вас уже запланирована встреча на другую дату. Перепланировать?");
        deliveryForm.replanButton();
        deliveryForm.checkSuccessNotification("Встреча успешно запланирована");



    }

    @Test
    @DisplayName("Should successful plan and replan meeting")
    void shouldSuccessfulPlanAndReplanMeeting() {
        DataGenerator.Registration.UserInfo validUser = DataGenerator.Registration.generateUser("ru");
        int daysToAddForFirstMeeting = 4;
        String firstMeetingDate = DataGenerator.generateDate(daysToAddForFirstMeeting);
        int daysToAddForSecondMeeting = 7;
        String secondMeetingDate = DataGenerator.generateDate(daysToAddForSecondMeeting);
        $("[data-test-id=city] input").setValue(validUser.getCity());
        $("[data-test-id=date] input").sendKeys(Keys.chord(Keys.SHIFT, Keys.HOME), Keys.BACK_SPACE);
        $("[data-test-id=date] input").setValue(firstMeetingDate);
        $("[data-test-id=name] input").setValue(validUser.getName());
        $("[data-test-id=phone] input").setValue(validUser.getPhone());
        $("[data-test-id=agreement]").click();
        $(byText("Запланировать")).click();
        $(byText("Успешно!")).shouldBe(Condition.visible, Duration.ofSeconds(5));
        $("[data-test-id='success-notification'] .notification__content")
                .shouldHave(Condition.exactText("Встреча успешно запланирована на " + firstMeetingDate))
                .shouldBe(Condition.visible);
        $("[data-test-id=date] input").sendKeys(Keys.chord(Keys.SHIFT, Keys.HOME), Keys.BACK_SPACE);
        $("[data-test-id=date] input").setValue(secondMeetingDate);
        $(byText("Запланировать")).click();
        $("[data-test-id='replan-notification'] .notification__content")
                .shouldHave(Condition.partialText("У вас уже запланирована встреча на другую дату. Перепланировать?"))
                .shouldBe(Condition.visible);
        $("[data-test-id='replan-notification'] button").click();
        $("[data-test-id='success-notification'] .notification__content")
                .shouldHave(Condition.text("Встреча успешно запланирована на " + secondMeetingDate))
                .shouldBe(Condition.visible);
   }
}