package com.App.fullStack.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AvailabilityResponseV2V3 {
    private String itemId;
    private String locationId;
    private int availableQty;
    private String stockLevel; // Possible values: Red, Yellow, Green
}