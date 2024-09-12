package com.App.fullStack.controler;

import com.App.fullStack.controller.LocationController;
import com.App.fullStack.pojos.Location;
import com.App.fullStack.responseHandler.ApiResponse;
import com.App.fullStack.service.LocationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Objects;

class LocationControllerTest {

    @Mock
    private LocationService locationService;

    @InjectMocks
    private LocationController locationController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getAllLocations_Success() {
        Page<Location> mockPage = mock(Page.class);
        ApiResponse<Page<Location>> response = new ApiResponse<>(true, "Locations Found", mockPage);
        when(locationService.getAllLocations(0, 8, null)).thenReturn(mockPage);

        ResponseEntity<ApiResponse<Page<Location>>> result = locationController.getAllLocations(0, 8, null);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(response.isSuccess(), Objects.requireNonNull(result.getBody()).isSuccess());
    }

    @Test
    void getLocationById_Success() {
        Location location = new Location();
        ApiResponse<Location> response = new ApiResponse<>(true, "Location Found", location);
        when(locationService.getLocationById("locationId")).thenReturn(location);

        ResponseEntity<ApiResponse<Location>> result = locationController.getLocationById("locationId");

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(response.getPayload(), Objects.requireNonNull(result.getBody()).getPayload());
    }

    @Test
    void addLocation_Success() {
        Location location = new Location();
        ApiResponse<Location> response = new ApiResponse<>(true, "Location Added", location);
        when(locationService.addLocation(location)).thenReturn(location);

        ResponseEntity<ApiResponse<Location>> result = locationController.addLocation(location);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(response.getPayload(), Objects.requireNonNull(result.getBody()).getPayload());
    }

    @Test
    void deleteLocation_Success() {
        ApiResponse<String> response = new ApiResponse<>(true, "Location Delete Operation.", "locationId");
        when(locationService.deleteLocation("locationId")).thenReturn("locationId");

        ResponseEntity<ApiResponse<String>> result = locationController.deleteLocation("locationId");

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(response.getPayload(), Objects.requireNonNull(result.getBody()).getPayload());
    }

    @Test
    void getAllLocationIds_Success() {
        List<String> locationIds = List.of("id1", "id2");
        ApiResponse<List<String>> response = new ApiResponse<>(true, "Location ids found.", locationIds);
        when(locationService.getAllLocationIds()).thenReturn(locationIds);

        ResponseEntity<ApiResponse<List<String>>> result = locationController.getAllLocationIds();

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(response.getPayload(), Objects.requireNonNull(result.getBody()).getPayload());
    }
}
