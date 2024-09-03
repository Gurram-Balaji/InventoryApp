package com.App.fullStack.pojos;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;


@Document(collection = "atpThresholds")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AtpThreshold {

    @Id
    private String thresholdId; // MongoDB will auto-generate this field
    private String itemId;
    private String locationId;
    private int minThreshold;
    private int maxThreshold;
}
