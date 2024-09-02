package com.App.fullStack.dto;

import lombok.*;

@Setter
@Getter
@AllArgsConstructor
public class AvailabilityResponse {
    private String itemId;
    private String locationId;
    private int availableQty;

}
