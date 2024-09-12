package com.App.fullStack.controller;

import com.App.fullStack.service.AvailabilityService;
import com.App.fullStack.dto.AvailabilityResponse;
import com.App.fullStack.dto.AvailabilityResponseV2V3;
import com.App.fullStack.responseHandler.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/availability")
public class AvailabilityController {

    @Autowired
    private AvailabilityService availabilityService;

    // Common messages and path versions
    private static final String SUCCESS_MESSAGE = "Available Quantity";
    
    // API Version 1: Availability by Location
    @GetMapping("/v1/{itemId}/{locationId}")
    public ResponseEntity<ApiResponse<AvailabilityResponse>> getV1AvailabilityByLocation(
            @PathVariable String itemId, @PathVariable String locationId) {
        
        // Calculate availability and return response
        int availableQuantity = availabilityService.calculateAvailabilityByLocation(itemId, locationId);
        AvailabilityResponse response = new AvailabilityResponse(itemId, locationId, availableQuantity);
        
        return ResponseEntity.ok(new ApiResponse<>(true, SUCCESS_MESSAGE, response));
    }

    // API Version 1: Availability by Item (All Locations)
    @GetMapping("/v1/{itemId}")
    public ResponseEntity<ApiResponse<AvailabilityResponse>> getV1AvailabilityByItem(
            @PathVariable String itemId) {
        
        int availableQuantity = availabilityService.calculateAvailabilityByItem(itemId);
        AvailabilityResponse response = new AvailabilityResponse(itemId, "NETWORK", availableQuantity);
        
        return ResponseEntity.ok(new ApiResponse<>(true, SUCCESS_MESSAGE, response));
    }

    // API Version 2: Availability by Location
    @GetMapping("/v2/{itemId}/{locationId}")
    public ResponseEntity<ApiResponse<AvailabilityResponseV2V3>> getV2AvailabilityByLocation(
            @PathVariable String itemId, @PathVariable String locationId) {
        
        AvailabilityResponseV2V3 response = availabilityService.calculateV2AvailabilityByLocation(itemId, locationId);
        
        return ResponseEntity.ok(new ApiResponse<>(true, SUCCESS_MESSAGE, response));
    }

    // API Version 2: Availability by Item (All Locations)
    @GetMapping("/v2/{itemId}")
    public ResponseEntity<ApiResponse<AvailabilityResponseV2V3>> getV2AvailabilityInAllLocation(
            @PathVariable String itemId) {
        
        AvailabilityResponseV2V3 response = availabilityService.calculateV2AvailabilityInAllLocation(itemId);
        
        return ResponseEntity.ok(new ApiResponse<>(true, SUCCESS_MESSAGE, response));
    }

    // API Version 3: Availability by Location
    @GetMapping("/v3/{itemId}/{locationId}")
    public ResponseEntity<ApiResponse<AvailabilityResponseV2V3>> getV3AvailabilityByLocation(
            @PathVariable String itemId, @PathVariable String locationId) {
        
        AvailabilityResponseV2V3 response = availabilityService.calculateV3AvailabilityByLocation(itemId, locationId);
        
        return ResponseEntity.ok(new ApiResponse<>(true, SUCCESS_MESSAGE, response));
    }
}
