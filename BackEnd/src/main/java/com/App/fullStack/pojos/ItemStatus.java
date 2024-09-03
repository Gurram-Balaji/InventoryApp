package com.App.fullStack.pojos;

import java.util.HashMap;
import java.util.Map;

public enum ItemStatus {
    //Available: The item is in stock and ready for purchase or use.
    AVAILABLE,
    //Out of Stock: The item is not currently available in inventory.
    OUT_OF_STOCK,
    //Back ordered: The item is out of stock but can be ordered, with delivery expected once it becomes available.
    BACKORDERED,
    //Discontinued: The item is no longer being produced or sold.
    DISCONTINUED,
    //On Hold: The item is temporarily not available for sale due to administrative reasons.
    ON_HOLD,
    //Pre-Order: The item is not yet released but can be pre-ordered.
    PRE_ORDER;

    private static final Map<String, ItemStatus> lookup = new HashMap<>();

    static {
        for (ItemStatus type : ItemStatus.values()) {
            lookup.put(type.name().toUpperCase(), type);
        }
    }

    public static boolean isValid(String value) {
        return !lookup.containsKey(value.toUpperCase());
    }

}


