package com.mindhub.homebanking.utils;

import java.util.Random;

public class CardUtils {
    public static String generateCardNumber() {
        Random random = new Random();
        return String.format("%04d-%04d-%04d-%04d",
                random.nextInt(10000),
                random.nextInt(10000),
                random.nextInt(10000),
                random.nextInt(10000));

    }

    public static int generateCvv() {
        Random random = new Random();
        return 100 + random.nextInt(900);
    }
}
