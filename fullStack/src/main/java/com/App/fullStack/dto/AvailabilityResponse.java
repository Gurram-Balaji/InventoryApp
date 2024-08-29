package com.App.fullStack.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class AvailabilityResponse {
    private String itemId;
    private String locationId;
    private int availableQty;
}
