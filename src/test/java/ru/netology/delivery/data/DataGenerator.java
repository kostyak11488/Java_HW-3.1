package ru.netology.delivery.data;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Random;

public class DataGenerator {

    private static final Random random = new Random();

    private DataGenerator() {
    }

    public static UserInfo generateUser() {
        return new UserInfo(
                generateCity(),
                "Иванов Иван",
                "+79990000000"
        );
    }

    public static String generateDate(int days) {
        return LocalDate.now()
                .plusDays(days)
                .format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
    }

    private static String generateCity() {
        String[] cities = {
                "Москва",
                "Санкт-Петербург",
                "Казань",
                "Екатеринбург",
                "Новосибирск",
                "Самара"
        };
        return cities[random.nextInt(cities.length)];
    }
}


