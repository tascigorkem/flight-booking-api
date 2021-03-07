package com.tascigorkem.flightbookingservice.faker;

import java.security.SecureRandom;

public class EnumRandomizeUtil {
    private static final SecureRandom secureRandom = new SecureRandom();

    private EnumRandomizeUtil() {
        throw new IllegalStateException("Utility class");
    }

    // get random enum value
    public static <T extends Enum<?>> T randomEnum(Class<T> clazz) {
        int x = secureRandom.nextInt(clazz.getEnumConstants().length);
        return clazz.getEnumConstants()[x];
    }
}
