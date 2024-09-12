package com.App.fullStack.dto;

import com.App.fullStack.pojos.SupplyType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SupplyDTO {
    private String supplyId;
    private String itemId;
    private String itemDescription;
    private String locationId;
    private String locationDescription;
    private SupplyType supplyType;
    private int quantity;
  
}
