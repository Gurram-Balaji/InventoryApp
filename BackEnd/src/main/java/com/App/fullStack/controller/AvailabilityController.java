package com.App.fullStack.controller;

import com.App.fullStack.service.AvailabilityService;
import com.App.fullStack.dto.AvailabilityResponse;
import com.App.fullStack.dto.AvailabilityResponseV2V3;
import com.App.fullStack.responseHandler.ApiResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class AvailabilityController {

    @Autowired
    public AvailabilityService availabilityService;

    @GetMapping("/v1/availability/{itemId}/{locationId}")
    public ResponseEntity<ApiResponse<AvailabilityResponse>> getAvailabilityByLocation(
            @PathVariable String itemId, @PathVariable String locationId) {
        return ResponseEntity.ok(new ApiResponse<>(true, "Available Quantity",
                new AvailabilityResponse(itemId, locationId,
                        availabilityService.calculateAvailabilityByLocation(itemId,
                                locationId))));
    }

    @GetMapping("/v1/availability/{itemId}")
    public ResponseEntity<ApiResponse<AvailabilityResponse>> getAvailabilityByItem(
            @PathVariable String itemId) {
        return ResponseEntity.ok(
                new ApiResponse<>(true, "Available Quantity",
                        new AvailabilityResponse(itemId, "NETWORK",
                                availabilityService
                                        .calculateAvailabilityByItem(itemId))));
    }

    @GetMapping("/v2/availability/{itemId}/{locationId}")
    public ResponseEntity<ApiResponse<AvailabilityResponseV2V3>> getV2AvailabilityByLocation(
            @PathVariable String itemId, @PathVariable String locationId) {
        return ResponseEntity.ok(
                new ApiResponse<>(true, "Available Quantity",
                        availabilityService.calculateV2AvailabilityByLocation(itemId,
                                locationId)));
    }

    @GetMapping("/v3/availability/{itemId}/{locationId}")
    public ResponseEntity<ApiResponse<AvailabilityResponseV2V3>> getV3AvailabilityByLocation(
            @PathVariable String itemId, @PathVariable String locationId) {
        return ResponseEntity.ok(
                new ApiResponse<>(true, "Available Quantity",
                        availabilityService.calculateV3AvailabilityByLocation(itemId,
                                locationId)));
    }
}