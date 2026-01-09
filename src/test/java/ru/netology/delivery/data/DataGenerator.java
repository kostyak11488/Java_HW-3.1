package ru.netology.delivery.data;

import com.github.javafaker.Faker;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class DataGenerator {

    private static final Faker faker = new Faker(new Locale("ru"));

    private DataGenerator() {
    }

    public static UserInfo generateUser() {
        return new UserInfo(
                generateCity(),
                generateName(),
                generatePhone()
        );
    }

    public static String generateDate(int days) {
        return LocalDate.now().plusDays(days)
                .format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
    }

    private static String generateCity() {
        String[] cities = {
                "Москва", "Санкт-Петербург", "Казань",
                "Екатеринбург", "Новосибирск", "Самара",
                "Уфа", "Краснодар", "Пермь", "Воронеж"
        };
        return cities[faker.random().nextInt(cities.length)];
    }

    private static String generateName() {
        return faker.name().firstName() + " " + faker.name().lastName();
    }

    private static String generatePhone() {
        return "+7" + faker.numerify("##########");
    }
}

