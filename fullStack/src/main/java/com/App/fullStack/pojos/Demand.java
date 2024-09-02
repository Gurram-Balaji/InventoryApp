package com.App.fullStack.pojos;

import lombok.*;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "demands")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Demand {
    @Id
    private String demandId;
    private DemandType demandType; // Restricted to HARD_PROMISED or PLANNED
    private int quantity;
    private String itemId;
    private String locationId;
}
