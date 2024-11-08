package com.App.fullStack.dto;

import com.App.fullStack.pojos.SupplyType;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class SupplyDetailsResponse {
    private String itemId;
    private String locationId;
    private Map<SupplyType, Integer> supplyDetails;
}
