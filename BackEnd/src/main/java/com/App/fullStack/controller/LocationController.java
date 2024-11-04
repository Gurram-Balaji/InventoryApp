package com.App.fullStack.controller;

import com.App.fullStack.dto.LocationData;
import com.App.fullStack.pojos.Location;
import com.App.fullStack.responseHandler.ApiResponse;
import com.App.fullStack.service.LocationService;
import com.App.fullStack.utility.APIResponseForFoundOrNot;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/locations")
public class LocationController {

    @Autowired
    private LocationService locationService;

    // Constants for pagination and messages
    private static final String DEFAULT_PAGE = "0";
    private static final String DEFAULT_SIZE = "8";
    private static final String LOCATIONS_FOUND = "Locations Found";
    private static final String LOCATIONS_NOT_FOUND = "Locations Not Found";
    private static final String LOCATION_FOUND = "Location Found";
    private static final String LOCATION_NOT_FOUND = "Location Not Found";
    private static final String LOCATION_ADDED = "Location Added";
    private static final String LOCATION_NOT_ADDED = "Location Not Added";
    private static final String LOCATION_UPDATED = "Location Updated";
    private static final String LOCATION_NOT_UPDATED = "Location Not Updated";
    private static final String LOCATION_DELETE_OPERATION = "Location Delete Operation.";

    // Get all locations with optional search and pagination
    @GetMapping
    public ResponseEntity<ApiResponse<Page<Location>>> getAllLocations(
            @RequestParam(defaultValue = DEFAULT_PAGE) int page,
            @RequestParam(defaultValue = DEFAULT_SIZE) int size,
            @RequestParam(required = false) String search) {

        Page<Location> locations = locationService.getAllLocations(page, size, search);
        return APIResponseForFoundOrNot.generateResponse(locations, LOCATIONS_FOUND, LOCATIONS_NOT_FOUND);
    }

    // Get a location by ID
    @GetMapping("/{locationId}")
    public ResponseEntity<ApiResponse<Location>> getLocationById(@PathVariable String locationId) {
        Location location = locationService.getLocationById(locationId);
        return APIResponseForFoundOrNot.generateResponse(location, LOCATION_FOUND, LOCATION_NOT_FOUND);
    }

    // Add a new location
    @PostMapping
    public ResponseEntity<ApiResponse<Location>> addLocation(@RequestBody Location location) {
        Location addedLocation = locationService.addLocation(location);
        return APIResponseForFoundOrNot.generateResponse(addedLocation, LOCATION_ADDED, LOCATION_NOT_ADDED);
    }

    // Update an existing location
    @PatchMapping("/{locationId}")
    public ResponseEntity<ApiResponse<Location>> updateLocation(
            @PathVariable String locationId, @RequestBody Location locationDetails) {

        Location updatedLocation = locationService.updateLocation(locationId, locationDetails);
        return APIResponseForFoundOrNot.generateResponse(updatedLocation, LOCATION_UPDATED, LOCATION_NOT_UPDATED);
    }

    // Delete a location by ID
    @DeleteMapping("/{locationId}")
    public ResponseEntity<ApiResponse<String>> deleteLocation(@PathVariable String locationId) {
        String result = locationService.deleteLocation(locationId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new ApiResponse<>(true, LOCATION_DELETE_OPERATION, result));
    }

    // Get all location IDs
    @GetMapping("/ids")
    public ResponseEntity<ApiResponse<Page<String>>> getAllLocationIds(@RequestParam(defaultValue = DEFAULT_PAGE) int page,
                                                                       @RequestParam(defaultValue = DEFAULT_SIZE) int size,
                                                                       @RequestParam(required = false) String search) {
        Page<String> locationIds = locationService.getAllLocationIds(page, size, search);

        return APIResponseForFoundOrNot.generateResponse(locationIds, LOCATIONS_FOUND, LOCATIONS_NOT_FOUND);

    }


    @GetMapping("/stackedBarData")
    public ResponseEntity<ApiResponse<List<LocationData>>> stackedBarData() {
        return APIResponseForFoundOrNot.generateResponse(locationService.getStackedBarData(), LOCATIONS_FOUND, LOCATIONS_NOT_FOUND);
    }


}
