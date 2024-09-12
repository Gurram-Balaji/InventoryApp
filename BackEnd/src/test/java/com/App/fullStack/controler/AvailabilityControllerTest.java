package com.App.fullStack.controler;

import com.App.fullStack.controller.AvailabilityController;
import com.App.fullStack.dto.AvailabilityResponse;
import com.App.fullStack.dto.AvailabilityResponseV2V3;
import com.App.fullStack.responseHandler.ApiResponse;
import com.App.fullStack.service.AvailabilityService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AvailabilityControllerTest {

    @Mock
    private AvailabilityService availabilityService;

    @InjectMocks
    private AvailabilityController availabilityController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getV1AvailabilityByLocation_Success() {
        String itemId = "item123";
        String locationId = "location456";
        int availableQuantity = 100;

        when(availabilityService.calculateAvailabilityByLocation(itemId, locationId)).thenReturn(availableQuantity);

        ResponseEntity<ApiResponse<AvailabilityResponse>> response = availabilityController.getV1AvailabilityByLocation(itemId, locationId);

        assertEquals(200, response.getStatusCode().value());
        assertTrue(Objects.requireNonNull(response.getBody()).isSuccess());
        assertEquals("Available Quantity", response.getBody().getMessage());
        assertEquals(itemId, response.getBody().getPayload().getItemId());
        assertEquals(locationId, response.getBody().getPayload().getLocationId());
        assertEquals(availableQuantity, response.getBody().getPayload().getAvailableQty());

        verify(availabilityService, times(1)).calculateAvailabilityByLocation(itemId, locationId);
    }

    @Test
    void getV1AvailabilityByItem_Success() {
        String itemId = "item123";
        int availableQuantity = 500;

        when(availabilityService.calculateAvailabilityByItem(itemId)).thenReturn(availableQuantity);

        ResponseEntity<ApiResponse<AvailabilityResponse>> response = availabilityController.getV1AvailabilityByItem(itemId);

        assertEquals(200, response.getStatusCode().value());
        assertTrue(Objects.requireNonNull(response.getBody()).isSuccess());
        assertEquals("Available Quantity", response.getBody().getMessage());
        assertEquals(itemId, response.getBody().getPayload().getItemId());
        assertEquals("NETWORK", response.getBody().getPayload().getLocationId());
        assertEquals(availableQuantity, response.getBody().getPayload().getAvailableQty());

        verify(availabilityService, times(1)).calculateAvailabilityByItem(itemId);
    }

    @Test
    void getV2AvailabilityByLocation_Success() {
        String itemId = "item123";
        String locationId = "location456";
        AvailabilityResponseV2V3 mockResponse = new AvailabilityResponseV2V3(itemId, locationId, 150, "extra info");

        when(availabilityService.calculateV2AvailabilityByLocation(itemId, locationId)).thenReturn(mockResponse);

        ResponseEntity<ApiResponse<AvailabilityResponseV2V3>> response = availabilityController.getV2AvailabilityByLocation(itemId, locationId);

        assertEquals(200, response.getStatusCode().value());
        assertTrue(Objects.requireNonNull(response.getBody()).isSuccess());
        assertEquals("Available Quantity", response.getBody().getMessage());
        assertEquals(mockResponse, response.getBody().getPayload());

        verify(availabilityService, times(1)).calculateV2AvailabilityByLocation(itemId, locationId);
    }

    @Test
    void getV2AvailabilityInAllLocation_Success() {
        String itemId = "item123";
        AvailabilityResponseV2V3 mockResponse = new AvailabilityResponseV2V3(itemId, "ALL_LOCATIONS", 1000, "extra info");

        when(availabilityService.calculateV2AvailabilityInAllLocation(itemId)).thenReturn(mockResponse);

        ResponseEntity<ApiResponse<AvailabilityResponseV2V3>> response = availabilityController.getV2AvailabilityInAllLocation(itemId);

        assertEquals(200, response.getStatusCode().value());
        assertTrue(Objects.requireNonNull(response.getBody()).isSuccess());
        assertEquals("Available Quantity", response.getBody().getMessage());
        assertEquals(mockResponse, response.getBody().getPayload());

        verify(availabilityService, times(1)).calculateV2AvailabilityInAllLocation(itemId);
    }

    @Test
    void getV3AvailabilityByLocation_Success() {
        String itemId = "item123";
        String locationId = "location456";
        AvailabilityResponseV2V3 mockResponse = new AvailabilityResponseV2V3(itemId, locationId, 200, "extra info");

        when(availabilityService.calculateV3AvailabilityByLocation(itemId, locationId)).thenReturn(mockResponse);

        ResponseEntity<ApiResponse<AvailabilityResponseV2V3>> response = availabilityController.getV3AvailabilityByLocation(itemId, locationId);

        assertEquals(200, response.getStatusCode().value());
        assertTrue(Objects.requireNonNull(response.getBody()).isSuccess());
        assertEquals("Available Quantity", response.getBody().getMessage());
        assertEquals(mockResponse, response.getBody().getPayload());

        verify(availabilityService, times(1)).calculateV3AvailabilityByLocation(itemId, locationId);
    }
}
