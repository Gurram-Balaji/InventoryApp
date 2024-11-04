package com.App.fullStack.controller;

import com.App.fullStack.dto.LocationData;
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

import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class LocationControllerTest {

    @Mock
    private LocationService locationService;

    @InjectMocks
    private LocationController locationController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // Test case for getting all locations successfully
    @Test
    void getAllLocations_Success() {
        Page<Location> mockPage = mock(Page.class);
        when(locationService.getAllLocations(0, 8, null)).thenReturn(mockPage);

        ResponseEntity<ApiResponse<Page<Location>>> result = locationController.getAllLocations(0, 8, null);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertTrue(Objects.requireNonNull(result.getBody()).isSuccess());
        assertEquals(mockPage, result.getBody().getPayload());

        verify(locationService, times(1)).getAllLocations(0, 8, null);
    }

    // Test case when no locations are found
    @Test
    void getAllLocations_NotFound() {
        when(locationService.getAllLocations(0, 8, null)).thenReturn(null);

        ResponseEntity<ApiResponse<Page<Location>>> result = locationController.getAllLocations(0, 8, null);

        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
        assertFalse(Objects.requireNonNull(result.getBody()).isSuccess());
        assertNull(result.getBody().getPayload());

        verify(locationService, times(1)).getAllLocations(0, 8, null);
    }

    // Test case for getting a location by its ID successfully
    @Test
    void getLocationById_Success() {
        Location mockLocation = new Location();
        when(locationService.getLocationById("location123")).thenReturn(mockLocation);

        ResponseEntity<ApiResponse<Location>> result = locationController.getLocationById("location123");

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertTrue(Objects.requireNonNull(result.getBody()).isSuccess());
        assertEquals(mockLocation, result.getBody().getPayload());

        verify(locationService, times(1)).getLocationById("location123");
    }

    // Test case when a location is not found by its ID
    @Test
    void getLocationById_NotFound() {
        when(locationService.getLocationById("location123")).thenReturn(null);

        ResponseEntity<ApiResponse<Location>> result = locationController.getLocationById("location123");

        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
        assertFalse(Objects.requireNonNull(result.getBody()).isSuccess());
        assertNull(result.getBody().getPayload());

        verify(locationService, times(1)).getLocationById("location123");
    }

    // Test case for adding a new location successfully
    @Test
    void addLocation_Success() {
        Location mockLocation = new Location();
        when(locationService.addLocation(mockLocation)).thenReturn(mockLocation);

        ResponseEntity<ApiResponse<Location>> result = locationController.addLocation(mockLocation);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertTrue(Objects.requireNonNull(result.getBody()).isSuccess());
        assertEquals(mockLocation, result.getBody().getPayload());

        verify(locationService, times(1)).addLocation(mockLocation);
    }

    // Test case for adding a new location failure (e.g., invalid data)
    @Test
    void addLocation_Failure() {
        Location mockLocation = new Location();
        when(locationService.addLocation(mockLocation)).thenReturn(null);

        ResponseEntity<ApiResponse<Location>> result = locationController.addLocation(mockLocation);

        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
        assertFalse(Objects.requireNonNull(result.getBody()).isSuccess());
        assertNull(result.getBody().getPayload());

        verify(locationService, times(1)).addLocation(mockLocation);
    }

    // Test case for updating an existing location successfully
    @Test
    void updateLocation_Success() {
        Location mockLocation = new Location();
        when(locationService.updateLocation("location123", mockLocation)).thenReturn(mockLocation);

        ResponseEntity<ApiResponse<Location>> result = locationController.updateLocation("location123", mockLocation);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertTrue(Objects.requireNonNull(result.getBody()).isSuccess());
        assertEquals(mockLocation, result.getBody().getPayload());

        verify(locationService, times(1)).updateLocation("location123", mockLocation);
    }

    // Test case for updating a location when it is not found
    @Test
    void updateLocation_NotFound() {
        Location mockLocation = new Location();
        when(locationService.updateLocation("location123", mockLocation)).thenReturn(null);

        ResponseEntity<ApiResponse<Location>> result = locationController.updateLocation("location123", mockLocation);

        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
        assertFalse(Objects.requireNonNull(result.getBody()).isSuccess());
        assertNull(result.getBody().getPayload());

        verify(locationService, times(1)).updateLocation("location123", mockLocation);
    }

    // Test case for deleting a location successfully
    @Test
    void deleteLocation_Success() {
        when(locationService.deleteLocation("location123")).thenReturn("location123");

        ResponseEntity<ApiResponse<String>> result = locationController.deleteLocation("location123");

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertTrue(Objects.requireNonNull(result.getBody()).isSuccess());
        assertEquals("location123", result.getBody().getPayload());

        verify(locationService, times(1)).deleteLocation("location123");
    }

    // Test case for deleting a location when it is not found
    @Test
    void deleteLocation_NotFound() {
        when(locationService.deleteLocation("location123")).thenReturn(null);

        ResponseEntity<ApiResponse<String>> result = locationController.deleteLocation("location123");

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertTrue(Objects.requireNonNull(result.getBody()).isSuccess());
        assertNull(result.getBody().getPayload());

        verify(locationService, times(1)).deleteLocation("location123");
    }

    // Test case for fetching all location IDs successfully
    @Test
    void getAllLocationIds_Success() {
        Page<String> mockPage = mock(Page.class);
        when(locationService.getAllLocationIds(0, 8, null)).thenReturn(mockPage);

        ResponseEntity<ApiResponse<Page<String>>> result = locationController.getAllLocationIds(0, 8, null);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertTrue(Objects.requireNonNull(result.getBody()).isSuccess());
        assertEquals(mockPage, result.getBody().getPayload());

        verify(locationService, times(1)).getAllLocationIds(0, 8, null);
    }

    // Test case for fetching location IDs when none are found
    @Test
    void getAllLocationIds_NotFound() {
        when(locationService.getAllLocationIds(0, 8, null)).thenReturn(null);

        ResponseEntity<ApiResponse<Page<String>>> result = locationController.getAllLocationIds(0, 8, null);

        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
        assertFalse(Objects.requireNonNull(result.getBody()).isSuccess());
        assertNull(result.getBody().getPayload());

        verify(locationService, times(1)).getAllLocationIds(0, 8, null);
    }

    // Test case for fetching stacked bar data successfully
    @Test
    void stackedBarData_Success() {
        List<LocationData> mockData = mock(List.class);
        when(locationService.getStackedBarData()).thenReturn(mockData);

        ResponseEntity<ApiResponse<List<LocationData>>> result = locationController.stackedBarData();

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertTrue(Objects.requireNonNull(result.getBody()).isSuccess());
        assertEquals(mockData, result.getBody().getPayload());

        verify(locationService, times(1)).getStackedBarData();
    }

    // Test case for fetching stacked bar data when no data is found
    @Test
    void stackedBarData_NotFound() {
        when(locationService.getStackedBarData()).thenReturn(null);

        ResponseEntity<ApiResponse<List<LocationData>>> result = locationController.stackedBarData();

        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
        assertFalse(Objects.requireNonNull(result.getBody()).isSuccess());
        assertNull(result.getBody().getPayload());

        verify(locationService, times(1)).getStackedBarData();
    }
}
