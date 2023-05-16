package com.mindhub.homebanking.utils;

import java.util.Random;

public class AccountUtils {

    public static String generateAccountNumber() {
        Random random = new Random();
        int num = random.nextInt(100000000);
        return String.format("VIN-%08d", num);
    }
}
