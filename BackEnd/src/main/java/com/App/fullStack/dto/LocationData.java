package com.App.fullStack.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;
@AllArgsConstructor
@Getter
@NoArgsConstructor
@Setter
public class LocationData {
    private String locationId;
    private String locationDesc;
    private Map<String, Integer> supplyDetails;
    private Map<String, Integer> demandDetails;
}
