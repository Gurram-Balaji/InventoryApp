package com.App.fullStack.dto;

import com.App.fullStack.pojos.SupplyType;

import lombok.Getter;
import lombok.Setter;
import java.util.Map;

@Setter
@Getter
public class SupplyDetailsResponse {
    private String itemId;
    private String locationId;
    private Map<SupplyType, Integer> supplyDetails;

    // Constructors, Getters, and Setters

    public SupplyDetailsResponse(String itemId, String locationId, Map<SupplyType, Integer> supplyDetails) {
        this.itemId = itemId;
        this.locationId = locationId;
        this.supplyDetails = supplyDetails;
    }

   }
