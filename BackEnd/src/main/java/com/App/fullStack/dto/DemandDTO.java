package com.App.fullStack.dto;

import com.App.fullStack.pojos.DemandType;

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
public class DemandDTO {
    private String demandId;
    private String itemId;
    private String itemDescription;
    private String locationId;
    private String locationDescription;
    private DemandType demandType;
    private int quantity;
  
}
