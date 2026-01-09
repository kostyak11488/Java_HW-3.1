package ru.netology.delivery.test;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.logevents.SelenideLogger;
import io.qameta.allure.selenide.AllureSelenide;
import org.junit.jupiter.api.*;
import org.openqa.selenium.Keys;
import ru.netology.delivery.data.DataGenerator;
import ru.netology.delivery.data.UserInfo;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.*;

public class ReplanDeliveryTest {

    @BeforeAll
    static void setupAll() {
        SelenideLogger.addListener("allure", new AllureSelenide());
    }

    @BeforeEach
    void setUp() {
        // Настройки для CI
        Configuration.browser = "chrome";
        Configuration.headless = true;
        Configuration.browserSize = "1920x1080";
        Configuration.timeout = 15000;
        Configuration.pageLoadTimeout = 15000;

        // Убедимся, что сервер доступен перед открытием
        open("http://localhost:9999");

        // Проверим, что страница загрузилась
        $("[data-test-id=city]").shouldBe(visible);
    }

    @Test
    @DisplayName("Should successfully replan delivery date")
    void shouldReplanDeliveryDate() {
        // Генерация данных
        UserInfo user = DataGenerator.generateUser();
        String firstDate = DataGenerator.generateDate(3);
        String secondDate = DataGenerator.generateDate(7);

        System.out.println("Test data:");
        System.out.println("City: " + user.getCity());
        System.out.println("Name: " + user.getName());
        System.out.println("Phone: " + user.getPhone());
        System.out.println("First date: " + firstDate);
        System.out.println("Second date: " + secondDate);

        // Шаг 1: Заполнение первой даты
        System.out.println("Step 1: Filling first date");
        $("[data-test-id=city] input").setValue(user.getCity());
        $("[data-test-id=name] input").setValue(user.getName());
        $("[data-test-id=phone] input").setValue(user.getPhone());

        // Очистка поля даты
        $("[data-test-id=date] input").sendKeys(Keys.chord(Keys.CONTROL, "a"), Keys.DELETE);
        $("[data-test-id=date] input").setValue(firstDate);

        $("[data-test-id=agreement]").click();
        $$("button").find(text("Запланировать")).click();

        // Проверка успешного уведомления
        $("[data-test-id=success-notification]")
                .shouldBe(visible)
                .shouldHave(text("Встреча успешно запланирована на " + firstDate));

        // Шаг 2: Перепланирование на другую дату
        System.out.println("Step 2: Replanning to second date");
        // Очистка поля даты
        $("[data-test-id=date] input").sendKeys(Keys.chord(Keys.CONTROL, "a"), Keys.DELETE);
        $("[data-test-id=date] input").setValue(secondDate);
        $$("button").find(text("Запланировать")).click();

        // Проверка уведомления о перепланировании
        $("[data-test-id=replan-notification]")
                .shouldBe(visible)
                .shouldHave(text("У вас уже запланирована встреча на другую дату"));

        // Шаг 3: Подтверждение перепланирования
        System.out.println("Step 3: Confirming replan");
        $$("button").find(text("Перепланировать")).click();

        // Проверка успешного перепланирования
        $("[data-test-id=success-notification]")
                .shouldBe(visible)
                .shouldHave(text("Встреча успешно запланирована на " + secondDate));
    }

    @AfterEach
    void tearDown() {
        closeWebDriver();
    }
}

