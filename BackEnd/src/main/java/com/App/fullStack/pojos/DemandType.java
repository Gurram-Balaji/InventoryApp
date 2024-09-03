package com.App.fullStack.pojos;

import java.util.HashMap;
import java.util.Map;

public enum DemandType {
    HARD_PROMISED,
    PLANNED;

    private static final Map<String, DemandType> lookup = new HashMap<>();

    static {
        for (DemandType type : DemandType.values()) {
            lookup.put(type.name().toUpperCase(), type);
        }
    }

    public static boolean isValid(String value) {
        return !lookup.containsKey(value.toUpperCase());
    }

}
