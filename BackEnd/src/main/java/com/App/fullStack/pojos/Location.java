package com.App.fullStack.pojos;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "locations")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Location {

    @Id
    private String id;
    private String locationId; // Mandatory field from the input
    private String locationDesc;
    private LocationType locationType;
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
