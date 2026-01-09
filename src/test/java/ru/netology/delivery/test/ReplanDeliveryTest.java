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

        // Retry логика для открытия приложения
        int maxRetries = 3;
        Exception lastException = null;

        for (int i = 0; i < maxRetries; i++) {
            try {
                open("/");
                $("[data-test-id=city]").shouldBe(visible);
                System.out.println("✅ Application opened successfully on attempt " + (i + 1));
                return;
            } catch (Exception e) {
                lastException = e;
                System.out.println("⚠️ Attempt " + (i + 1) + " failed: " + e.getMessage());

                if (i < maxRetries - 1) {
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                    }
                }
            }
        }

        throw new RuntimeException("❌ Failed to open application after " + maxRetries + " attempts", lastException);
    }

    @Test
    @DisplayName("Should successfully replan delivery date")
    void shouldReplanDeliveryDate() {
        // Генерация тестовых данных
        UserInfo user = DataGenerator.generateUser();
        String firstDate = DataGenerator.generateDate(3);
        String secondDate = DataGenerator.generateDate(7);

        System.out.println("\n=== Test Data ===");
        System.out.println("City: " + user.getCity());
        System.out.println("Name: " + user.getName());
        System.out.println("Phone: " + user.getPhone());
        System.out.println("First date: " + firstDate);
        System.out.println("Second date: " + secondDate);
        System.out.println("================\n");

        // Шаг 1: Заполнение формы и запланирование первую дату
        System.out.println("📋 Step 1: Filling form with first date");
        $("[data-test-id=city] input").setValue(user.getCity());
        $("[data-test-id=name] input").setValue(user.getName());
        $("[data-test-id=phone] input").setValue(user.getPhone());

        // Очистка и установка даты
        $("[data-test-id=date] input").click();
        $("[data-test-id=date] input").sendKeys(Keys.chord(Keys.CONTROL, "a"));
        $("[data-test-id=date] input").sendKeys(Keys.DELETE);
        $("[data-test-id=date] input").setValue(firstDate);

        // Согласие и отправка
        $("[data-test-id=agreement]").click();
        $$("button").find(exactText("Запланировать")).click();

        // Проверка успешного планирования
        $("[data-test-id=success-notification]")
                .shouldBe(visible)
                .shouldHave(text("Встреча успешно запланирована на " + firstDate));
        System.out.println("✅ First date scheduled successfully: " + firstDate);

        // Шаг 2: Перепланирование на вторую дату
        System.out.println("\n📅 Step 2: Replanning to second date");
        $("[data-test-id=date] input").click();
        $("[data-test-id=date] input").sendKeys(Keys.chord(Keys.CONTROL, "a"));
        $("[data-test-id=date] input").sendKeys(Keys.DELETE);
        $("[data-test-id=date] input").setValue(secondDate);
        $$("button").find(exactText("Запланировать")).click();

        // Проверка уведомления о перепланировании
        $("[data-test-id=replan-notification]")
                .shouldBe(visible)
                .shouldHave(text("У вас уже запланирована встреча на другую дату"));
        System.out.println("✅ Replan notification appeared");

        // Шаг 3: Подтверждение перепланирования
        System.out.println("\n✔️ Step 3: Confirming replan");
        $$("button").find(exactText("Перепланировать")).click();

        // Проверка успешного перепланирования
        $("[data-test-id=success-notification]")
                .shouldBe(visible)
                .shouldHave(text("Встреча успешно запланирована на " + secondDate));
        System.out.println("✅ Successfully replanned to: " + secondDate);
        System.out.println("\n🎉 Test completed successfully!\n");
    }

    @AfterEach
    void tearDown() {
        closeWebDriver();
    }
}
