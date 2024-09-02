package com.App.fullStack.controller;

import com.App.fullStack.pojos.Location;
import com.App.fullStack.responseHandler.ApiResponse;
import com.App.fullStack.service.LocationService;
import com.App.fullStack.utility.APIResponseForFoundOrNot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/locations")
public class LocationController {

    @Autowired
    public LocationService locationService;

    @GetMapping
    public ResponseEntity<ApiResponse<Page<Location>>> getAllLocations(@RequestParam(defaultValue = "0") int page,
                                                                       @RequestParam(defaultValue = "10") int size) {
        return APIResponseForFoundOrNot.generateResponse(locationService.getAllLocations(page, size), "Locations Found",
                "Locations Not Found");
    }

    @GetMapping("/{locationId}")
    public ResponseEntity<ApiResponse<Location>> getLocationById(@PathVariable String locationId) {
        return APIResponseForFoundOrNot.generateResponse(locationService.getLocationById(locationId), "Location Found",
                "Location Not Found");
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Location>> addLocation(@RequestBody Location location) {
        return APIResponseForFoundOrNot.generateResponse(locationService.addLocation(location), "Location Added",
                "Location Not Added");
    }

    @PatchMapping("/{locationId}")
    public ResponseEntity<ApiResponse<Location>> updateLocation(@PathVariable String locationId,
                                                                @RequestBody Location locationDetails) {
        return APIResponseForFoundOrNot.generateResponse(locationService.updateLocation(locationId, locationDetails),
                "Location Updated", "Location Not Updated");
    }

    @DeleteMapping("/{locationId}")
    public ResponseEntity<ApiResponse<String>> deleteLocation(@PathVariable String locationId) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(new ApiResponse<>(true, "Location Delete Operation.",
                        locationService.deleteLocation(locationId)));
    }
}
