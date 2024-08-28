package com.App.fullStack.controller;

import com.App.fullStack.pojos.Location;
import com.App.fullStack.responseHandler.ApiResponse;
import com.App.fullStack.service.LocationService;
import com.App.fullStack.utility.APIResponseForFoundOrNot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/locations")
public class LocationController {

    @Autowired
    private LocationService locationService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<Location>>> getAllLocations() {
        return APIResponseForFoundOrNot.generateResponse(locationService.getAllLocations(), "Locations Found",
                "Locations Not Found");
    }

    @GetMapping("/{locationid}")
    public ResponseEntity<ApiResponse<Location>> getLocationById(@PathVariable String locationid) {
        return APIResponseForFoundOrNot.generateResponse(locationService.getLocationById(locationid), "Location Found",
                "Location Not Found");
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Location>> addLocation(@RequestBody Location location) {
        return APIResponseForFoundOrNot.generateResponse(locationService.addLocation(location), "Location Added",
                "Location Not Added");
    }

    @PatchMapping("/{locationid}")
    public ResponseEntity<ApiResponse<Location>> updateLocation(@PathVariable String locationid,
            @RequestBody Location locationDetails) {
        return APIResponseForFoundOrNot.generateResponse(locationService.updateLocation(locationid, locationDetails),
                "Location Updated", "Location Not Updated");
    }

    @DeleteMapping("/{locationid}")
    public ResponseEntity<ApiResponse<String>> deleteLocation(@PathVariable String locationid) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(new ApiResponse<>(true, "Location Delete Operation.",
                        locationService.deleteLocation(locationid)));
    }
}
