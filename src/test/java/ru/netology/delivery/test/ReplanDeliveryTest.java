package ru.netology.delivery.test;

import com.codeborne.selenide.Configuration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.netology.delivery.data.DataGenerator;
import ru.netology.delivery.data.UserInfo;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.*;

public class ReplanDeliveryTest {

    @BeforeEach
    void setup() {
        Configuration.holdBrowserOpen = false;
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
        $("[data-test-id=date] input").doubleClick().sendKeys(firstDate);
        $("[data-test-id=agreement]").click();
        $$("button").find(text("Запланировать")).click();

        $("[data-test-id=success-notification]")
                .shouldHave(text("Встреча успешно запланирована на " + firstDate));

        $("[data-test-id=date] input").doubleClick().sendKeys(secondDate);
        $$("button").find(text("Запланировать")).click();

        $("[data-test-id=replan-notification]")
                .shouldHave(text("У вас уже запланирована встреча"));

        $$("button").find(text("Перепланировать")).click();

        $("[data-test-id=success-notification]")
                .shouldHave(text("Встреча успешно запланирована на " + secondDate));
    }
}
