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
        Configuration.browser = "chrome";
        Configuration.headless = true;
        Configuration.browserSize = "1920x1080";
        Configuration.timeout = 15000;
        Configuration.pageLoadTimeout = 15000;
        Configuration.baseUrl = "http://localhost:9999";

        int maxRetries = 3;
        Exception lastException = null;

        for (int i = 0; i < maxRetries; i++) {
            try {
                open("/");
                $("[data-test-id=city]").shouldBe(visible);
                System.out.println("✅ Application opened successfully");
                return;
            } catch (Exception e) {
                lastException = e;
                System.out.println("⚠️ Attempt " + (i + 1) + " failed");

                if (i < maxRetries - 1) {
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                    }
                }
            }
        }

        throw new RuntimeException("❌ Failed to open application", lastException);
    }

    @Test
    @DisplayName("Should successfully replan delivery date")
    void shouldReplanDeliveryDate() {
        UserInfo user = DataGenerator.generateUser();
        String firstDate = DataGenerator.generateDate(3);
        String secondDate = DataGenerator.generateDate(7);

        // Шаг 1: Заполнение формы и запланирование первую дату
        $("[data-test-id=city] input").setValue(user.getCity());
        $("[data-test-id=name] input").setValue(user.getName());
        $("[data-test-id=phone] input").setValue(user.getPhone());

        // Установка даты через поле ввода
        $("[data-test-id=date] input").sendKeys(Keys.chord(Keys.CONTROL, "a"));
        $("[data-test-id=date] input").sendKeys(Keys.DELETE);
        $("[data-test-id=date] input").setValue(firstDate);

        // Клик на согласие
        $("[data-test-id=agreement]").click();

        // Отправка формы
        $$("button").find(exactText("Запланировать")).click();

        // Проверка успешного планирования
        $("[data-test-id=success-notification]")
                .shouldBe(visible)
                .shouldHave(text("Встреча успешно запланирована на " + firstDate));

        // Шаг 2: Перепланирование на вторую дату
        $("[data-test-id=date] input").sendKeys(Keys.chord(Keys.CONTROL, "a"));
        $("[data-test-id=date] input").sendKeys(Keys.DELETE);
        $("[data-test-id=date] input").setValue(secondDate);

        $$("button").find(exactText("Запланировать")).click();

        // Проверка уведомления о перепланировании
        $("[data-test-id=replan-notification]")
                .shouldBe(visible)
                .shouldHave(text("У вас уже запланирована встреча на другую дату"));

        // Шаг 3: Подтверждение перепланирования
        $$("button").find(exactText("Перепланировать")).click();

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