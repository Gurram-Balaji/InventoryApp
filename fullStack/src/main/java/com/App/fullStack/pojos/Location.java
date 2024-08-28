package com.App.fullStack.pojos;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "locations")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Location {

    @Id
    private String id; // MongoDB will auto-generate this ID if you don't set it
    private String locationId; // Mandatory field from the input
    private String locationDesc;
    private String locationType;
    private boolean pickupAllowed;
    private boolean shippingAllowed;
    private boolean deliveryAllowed;
    private String addressLine1;
    private String addressLine2;
    private String addressLine3;
    private String city;
    private String state;
    private String country;
    private String pinCode;
}
