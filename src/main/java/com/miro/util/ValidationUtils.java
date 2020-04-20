package com.miro.util;

import java.util.Objects;
import java.util.stream.Stream;

public class ValidationUtils {

    public static boolean allAreNull(Object... fields) {
        return Stream.of(fields).allMatch(Objects::isNull);
    }

    public static boolean allAreNotNull(Object... fields) {
        return Stream.of(fields).allMatch(Objects::nonNull);
    }
}
