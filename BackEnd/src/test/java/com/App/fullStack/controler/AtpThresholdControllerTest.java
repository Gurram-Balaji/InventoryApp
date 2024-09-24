package com.App.fullStack.controler;

import com.App.fullStack.controller.AtpThresholdController;
import com.App.fullStack.dto.ThresholdDTO;
import com.App.fullStack.pojos.AtpThreshold;
import com.App.fullStack.responseHandler.ApiResponse;
import com.App.fullStack.service.AtpThresholdService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AtpThresholdControllerTest {

    @Mock
    private AtpThresholdService atpThresholdService;

    @InjectMocks
    private AtpThresholdController atpThresholdController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getAllAtpThresholds_Success() {
        Page mockPage = mock(Page.class);
        when(atpThresholdService.getAllAtpThresholds(0, 8)).thenReturn(mockPage);

        ResponseEntity<ApiResponse<Page<AtpThreshold>>> response = atpThresholdController.getAllAtpThresholds(0, 8);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(Objects.requireNonNull(response.getBody()).isSuccess());
        verify(atpThresholdService, times(1)).getAllAtpThresholds(0, 8);
    }

    @Test
    void getAllThresholdWithDetails_Success() {
        Page<ThresholdDTO> mockPage = mock(Page.class);
        when(atpThresholdService.getAllDemandWithDetails(0, 8, null,null)).thenReturn(mockPage);

        ResponseEntity<ApiResponse<Page<ThresholdDTO>>> response = atpThresholdController.getAllThresholdWithDetails(0, 8, null,null);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(Objects.requireNonNull(response.getBody()).isSuccess());
        verify(atpThresholdService, times(1)).getAllDemandWithDetails(0, 8, null,null);
    }

    @Test
    void getAtpThresholdById_Success() {
        AtpThreshold threshold = new AtpThreshold();
        when(atpThresholdService.getAtpThresholdById("thresholdId")).thenReturn(threshold);

        ResponseEntity<ApiResponse<AtpThreshold>> response = atpThresholdController.getAtpThresholdById("thresholdId");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(Objects.requireNonNull(response.getBody()).isSuccess());
        verify(atpThresholdService, times(1)).getAtpThresholdById("thresholdId");
    }

    @Test
    void getAtpThresholdByItemAndLocation_Success() {
        AtpThreshold threshold = new AtpThreshold();
        when(atpThresholdService.getAtpThresholdByItemAndLocation("itemId", "locationId")).thenReturn(threshold);

        ResponseEntity<ApiResponse<AtpThreshold>> response = atpThresholdController.getAtpThresholdByItemAndLocation("itemId", "locationId");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(Objects.requireNonNull(response.getBody()).isSuccess());
        verify(atpThresholdService, times(1)).getAtpThresholdByItemAndLocation("itemId", "locationId");
    }

    @Test
    void createAtpThreshold_Success() {
        AtpThreshold threshold = new AtpThreshold();
        when(atpThresholdService.AddAtpThreshold(threshold)).thenReturn(threshold);

        ResponseEntity<ApiResponse<AtpThreshold>> response = atpThresholdController.createAtpThreshold(threshold);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertTrue(Objects.requireNonNull(response.getBody()).isSuccess());
        verify(atpThresholdService, times(1)).AddAtpThreshold(threshold);
    }

    @Test
    void updateAtpThresholdById_Success() {
        AtpThreshold updatedThreshold = new AtpThreshold();
        when(atpThresholdService.updateAtpThresholdById("thresholdId", updatedThreshold)).thenReturn(updatedThreshold);

        ResponseEntity<ApiResponse<AtpThreshold>> response = atpThresholdController.updateAtpThresholdById("thresholdId", updatedThreshold);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(Objects.requireNonNull(response.getBody()).isSuccess());
        verify(atpThresholdService, times(1)).updateAtpThresholdById("thresholdId", updatedThreshold);
    }

    @Test
    void updateAtpThresholdByItemAndLocation_Success() {
        AtpThreshold updatedThreshold = new AtpThreshold();
        when(atpThresholdService.updateAtpThresholdByItemAndLocation("itemId", "locationId", updatedThreshold)).thenReturn(updatedThreshold);

        ResponseEntity<ApiResponse<AtpThreshold>> response = atpThresholdController.updateAtpThresholdByItemAndLocation("itemId", "locationId", updatedThreshold);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(Objects.requireNonNull(response.getBody()).isSuccess());
        verify(atpThresholdService, times(1)).updateAtpThresholdByItemAndLocation("itemId", "locationId", updatedThreshold);
    }

    @Test
    void deleteAtpThresholdById_Success() {
        when(atpThresholdService.deleteAtpThresholdById("thresholdId")).thenReturn("Threshold Deleted");

        ResponseEntity<ApiResponse<String>> response = atpThresholdController.deleteAtpThresholdById("thresholdId");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(Objects.requireNonNull(response.getBody()).isSuccess());
        assertEquals("Threshold Delete Operation.", response.getBody().getMessage());
        verify(atpThresholdService, times(1)).deleteAtpThresholdById("thresholdId");
    }
}
