package com.App.fullStack.pojos;

import java.util.HashMap;
import java.util.Map;

public enum SupplyType {
    ONHAND,
    INTRANSIT,
    DAMAGED;

    private static final Map<String, SupplyType> lookup = new HashMap<>();

    static {
        for (SupplyType type : SupplyType.values()) {
            lookup.put(type.name().toUpperCase(), type);
        }
    }

    public static boolean isValid(String value) {
        return !lookup.containsKey(value.toUpperCase());
    }
}
