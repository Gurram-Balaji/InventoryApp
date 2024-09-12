package com.App.fullStack.dto;


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
public class ThresholdDTO {
    private String thresholdId;
    private String itemId;
    private String itemDescription;
    private String locationId;
    private String locationDescription;
    private int minThreshold;
    private int maxThreshold;
  
}
