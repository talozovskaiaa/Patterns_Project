package ru.netology.delivery.page;

import static com.codeborne.selenide.Selenide.open;

public class MainPageObject {

    public void openURL() {

        open("http://localhost:9999");
    }
}
