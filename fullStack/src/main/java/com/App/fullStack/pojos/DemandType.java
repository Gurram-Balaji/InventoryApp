package com.App.fullStack.pojos;

public enum DemandType {
    HARD_PROMISED,
    PLANNED;

    public static boolean isValid(String value) {
        for (DemandType type : DemandType.values()) {
            if (type.name().equalsIgnoreCase(value)) {
                return true;
            }
        }
        return false;
    }
    
}
