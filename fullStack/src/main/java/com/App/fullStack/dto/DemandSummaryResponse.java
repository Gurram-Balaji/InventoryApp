package com.App.fullStack.dto;

import com.App.fullStack.pojos.DemandType;
import lombok.*;

import java.util.Map;

@AllArgsConstructor
@Getter
@Setter
public class DemandSummaryResponse {

    private String locationId;
    private Map<DemandType, Integer> demandDetails;

}
