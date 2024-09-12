package com.App.fullStack.dto;

import java.util.Map;

import com.App.fullStack.pojos.SupplyType;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class SupplySummaryResponse {
    private String locationId;
    private Map<SupplyType, Integer> supplyDetails;
}
