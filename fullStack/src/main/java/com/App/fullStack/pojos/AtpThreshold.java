package com.App.fullStack.pojos;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "atpThresholds")
public class AtpThreshold {

    @Id
    private String thresholdId; // MongoDB will auto-generate this field

    private String itemId;
    private String locationId;
    private int minThreshold;
    private int maxThreshold;
}
