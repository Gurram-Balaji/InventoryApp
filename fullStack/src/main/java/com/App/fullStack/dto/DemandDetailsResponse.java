package com.App.fullStack.dto;

import com.App.fullStack.pojos.DemandType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DemandDetailsResponse {

    private String itemId;
    private String locationId;
    private Map<DemandType, Integer> demandDetails;

}
