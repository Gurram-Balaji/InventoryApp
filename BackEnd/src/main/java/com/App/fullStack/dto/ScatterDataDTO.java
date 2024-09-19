package com.App.fullStack.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter

@AllArgsConstructor
@NoArgsConstructor
public class ScatterDataDTO {
    private double itemPrice;
    private int supplyQuantity;
    private int demandQuantity;
    private String itemName;
}
