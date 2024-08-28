package com.App.fullStack.pojos;

import lombok.*;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "supplies")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Supply {
    @Id
    private String supplyId;
    private String itemId;
    private String locationId;
    private SupplyType supplyType; // Supply type can be ONHAND, INTRANSIT, or DAMAGED
    private int quantity;
}
