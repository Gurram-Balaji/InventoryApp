package com.App.fullStack.dto;

import com.App.fullStack.pojos.DemandType;
import lombok.*;

import java.util.Map;

@Setter
@Getter
@AllArgsConstructor
public class DemandDetailsResponse {
    private String itemId;
    private String locationId;
    private Map<DemandType, Integer> demandDetails;

}
