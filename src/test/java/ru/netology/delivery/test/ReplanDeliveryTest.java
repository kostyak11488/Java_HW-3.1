package ru.netology.delivery.test;

import com.codeborne.selenide.Configuration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Keys;
import ru.netology.delivery.data.DataGenerator;
import ru.netology.delivery.data.UserInfo;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.*;

public class ReplanDeliveryTest {

    @BeforeEach
    void setUp() {
        // Убрать WebDriverManager - Selenide управляет драйвером сам
        Configuration.browser = "chrome";
        Configuration.headless = true;
        Configuration.browserSize = "1920x1080";
        Configuration.timeout = 10000;

        open("http://localhost:9999");
    }

    @Test
    void shouldReplanDeliveryDate() {
        UserInfo user = DataGenerator.generateUser();
        String firstDate = DataGenerator.generateDate(3);
        String secondDate = DataGenerator.generateDate(7);

        $("[data-test-id=city] input").setValue(user.getCity());
        $("[data-test-id=name] input").setValue(user.getName());
        $("[data-test-id=phone] input").setValue(user.getPhone());

        // Более надежный способ очистки поля даты
        $("[data-test-id=date] input").sendKeys(Keys.chord(Keys.CONTROL, "a"), Keys.DELETE);
        $("[data-test-id=date] input").setValue(firstDate);

        $("[data-test-id=agreement]").click();
        $$("button").find(text("Запланировать")).click();

        $("[data-test-id=success-notification]")
                .shouldHave(text("Встреча успешно запланирована на " + firstDate));

        // Для перепланирования
        $("[data-test-id=date] input").sendKeys(Keys.chord(Keys.CONTROL, "a"), Keys.DELETE);
        $("[data-test-id=date] input").setValue(secondDate);
        $$("button").find(text("Запланировать")).click();

        $("[data-test-id=replan-notification]")
                .shouldHave(text("У вас уже запланирована встреча на другую дату"));

        $$("button").find(text("Перепланировать")).click();

        $("[data-test-id=success-notification]")
                .shouldHave(text("Встреча успешно запланирована на " + secondDate));
    }
}

