package com.App.fullStack.dto;

import java.util.Map;

import com.App.fullStack.pojos.SupplyType;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class SupplySummaryResponse {
    private String locationId;
    private Map<SupplyType, Integer> supplyDetails;

    // Constructors, getters, and setters
    public SupplySummaryResponse(String locationId, Map<SupplyType, Integer> supplyDetails) {
        this.locationId = locationId;
        this.supplyDetails = supplyDetails;
    }

   
}
