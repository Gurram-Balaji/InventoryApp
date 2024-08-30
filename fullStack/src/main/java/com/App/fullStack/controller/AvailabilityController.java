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
        private AvailabilityService availabilityService;

        @GetMapping("/v1/availability/{itemid}/{locationid}")
        public ResponseEntity<ApiResponse<AvailabilityResponse>> getAvailabilityByLocation(
                        @PathVariable String itemid, @PathVariable String locationid) {
                return ResponseEntity.ok(new ApiResponse<AvailabilityResponse>(true, "Available Quantity",
                                new AvailabilityResponse(itemid, locationid,
                                                availabilityService.calculateAvailabilityByLocation(itemid,
                                                                locationid))));
        }

        @GetMapping("/v1/availability/{itemid}")
        public ResponseEntity<ApiResponse<AvailabilityResponse>> getAvailabilityByItem(
                        @PathVariable String itemid) {
                return ResponseEntity.ok(
                                new ApiResponse<AvailabilityResponse>(true, "Available Quantity",
                                                new AvailabilityResponse(itemid, "NETWORK",
                                                                availabilityService
                                                                                .calculateAvailabilityByItem(itemid))));
        }

        @GetMapping("/v2/availability/{itemid}/{locationid}")
        public ResponseEntity<ApiResponse<AvailabilityResponseV2V3>> getV2AvailabilityByLocation(
                        @PathVariable String itemid, @PathVariable String locationid) {
                return ResponseEntity.ok(
                                new ApiResponse<>(true, "Available Quantity",
                                                availabilityService.calculateV2AvailabilityByLocation(itemid,
                                                                locationid)));
        }

        @GetMapping("/v3/availability/{itemid}/{locationid}")
        public ResponseEntity<ApiResponse<AvailabilityResponseV2V3>> getV3AvailabilityByLocation(
                        @PathVariable String itemid, @PathVariable String locationid) {
                return ResponseEntity.ok(
                                new ApiResponse<>(true, "Available Quantity",
                                                availabilityService.calculateV3AvailabilityByLocation(itemid,
                                                                locationid)));
        }
}