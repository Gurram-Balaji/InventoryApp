package com.App.fullStack.controller;

import com.App.fullStack.dto.SupplyDTO;
import com.App.fullStack.dto.SupplyDetailsResponse;
import com.App.fullStack.dto.SupplySummaryResponse;
import com.App.fullStack.pojos.Supply;
import com.App.fullStack.pojos.SupplyType;
import com.App.fullStack.responseHandler.ApiResponse;
import com.App.fullStack.service.SupplyService;
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

class SupplyControllerTest {

    @Mock
    private SupplyService supplyService;

    @InjectMocks
    private SupplyController supplyController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // Test case for getting all supplies successfully
    @Test
    void getAllSupplies_Success() {
        Page<Supply> mockPage = mock(Page.class);
        when(supplyService.getAllSupplies(0, 8)).thenReturn(mockPage);

        ResponseEntity<ApiResponse<Page<Supply>>> result = supplyController.getAllSupplies(0, 8);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertTrue(Objects.requireNonNull(result.getBody()).isSuccess());
        assertEquals(mockPage, result.getBody().getPayload());

        verify(supplyService, times(1)).getAllSupplies(0, 8);
    }

    // Test case when no supplies are found
    @Test
    void getAllSupplies_NotFound() {
        when(supplyService.getAllSupplies(0, 8)).thenReturn(null);

        ResponseEntity<ApiResponse<Page<Supply>>> result = supplyController.getAllSupplies(0, 8);

        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
        assertFalse(Objects.requireNonNull(result.getBody()).isSuccess());
        assertNull(result.getBody().getPayload());

        verify(supplyService, times(1)).getAllSupplies(0, 8);
    }

    // Test case for getting all supplies with details successfully
    @Test
    void getAllSuppliesWithDetails_Success() {
        Page<SupplyDTO> mockPage = mock(Page.class);
        when(supplyService.getAllSuppliesWithDetails(0, 8, null, null)).thenReturn(mockPage);

        ResponseEntity<ApiResponse<Page<SupplyDTO>>> result = supplyController.getAllSuppliesWithDetails(0, 8, null, null);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertTrue(Objects.requireNonNull(result.getBody()).isSuccess());
        assertEquals(mockPage, result.getBody().getPayload());

        verify(supplyService, times(1)).getAllSuppliesWithDetails(0, 8, null, null);
    }

    // Test case for getting a supply by ID successfully
    @Test
    void getSupplyById_Success() {
        Supply mockSupply = new Supply();
        when(supplyService.getSupplyById("supply123")).thenReturn(mockSupply);

        ResponseEntity<ApiResponse<Supply>> result = supplyController.getSupplyById("supply123");

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertTrue(Objects.requireNonNull(result.getBody()).isSuccess());
        assertEquals(mockSupply, result.getBody().getPayload());

        verify(supplyService, times(1)).getSupplyById("supply123");
    }

    // Test case for getting a supply by ID when not found
    @Test
    void getSupplyById_NotFound() {
        when(supplyService.getSupplyById("supply123")).thenReturn(null);

        ResponseEntity<ApiResponse<Supply>> result = supplyController.getSupplyById("supply123");

        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
        assertFalse(Objects.requireNonNull(result.getBody()).isSuccess());
        assertNull(result.getBody().getPayload());

        verify(supplyService, times(1)).getSupplyById("supply123");
    }

    // Test case for getting supplies by item and location successfully
    @Test
    void getSuppliesByItemAndLocation_Success() {
        SupplyDetailsResponse mockResponse = new SupplyDetailsResponse();
        when(supplyService.getSuppliesByItemIdAndLocationId("item123", "location123")).thenReturn(mockResponse);

        ResponseEntity<ApiResponse<SupplyDetailsResponse>> result = supplyController.getSuppliesByItemAndLocation("item123", "location123");

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertTrue(Objects.requireNonNull(result.getBody()).isSuccess());
        assertEquals(mockResponse, result.getBody().getPayload());

        verify(supplyService, times(1)).getSuppliesByItemIdAndLocationId("item123", "location123");
    }

    // Test case for getting supplies by item and location when not found
    @Test
    void getSuppliesByItemAndLocation_NotFound() {
        when(supplyService.getSuppliesByItemIdAndLocationId("item123", "location123")).thenReturn(null);

        ResponseEntity<ApiResponse<SupplyDetailsResponse>> result = supplyController.getSuppliesByItemAndLocation("item123", "location123");

        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
        assertFalse(Objects.requireNonNull(result.getBody()).isSuccess());
        assertNull(result.getBody().getPayload());

        verify(supplyService, times(1)).getSuppliesByItemIdAndLocationId("item123", "location123");
    }

    // Test case for getting supplies by type and location successfully
    @Test
    void getSuppliesByTypeAndLocation_Success() {
        SupplySummaryResponse mockResponse = new SupplySummaryResponse();
        when(supplyService.getSuppliesByTypeAndLocationId(SupplyType.ONHAND, "location123")).thenReturn(mockResponse);

        ResponseEntity<ApiResponse<SupplySummaryResponse>> result = supplyController.getSuppliesByTypeAndLocation("ONHAND", "location123");

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertTrue(Objects.requireNonNull(result.getBody()).isSuccess());
        assertEquals(mockResponse, result.getBody().getPayload());

        verify(supplyService, times(1)).getSuppliesByTypeAndLocationId(SupplyType.ONHAND, "location123");
    }

    // Test case for getting supplies by type and location when not found
    @Test
    void getSuppliesByTypeAndLocation_NotFound() {
        when(supplyService.getSuppliesByTypeAndLocationId(SupplyType.ONHAND, "location123")).thenReturn(null);

        ResponseEntity<ApiResponse<SupplySummaryResponse>> result = supplyController.getSuppliesByTypeAndLocation("ONHAND", "location123");

        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
        assertFalse(Objects.requireNonNull(result.getBody()).isSuccess());
        assertNull(result.getBody().getPayload());

        verify(supplyService, times(1)).getSuppliesByTypeAndLocationId(SupplyType.ONHAND, "location123");
    }

    // Test case for adding a new supply successfully
    @Test
    void addSupply_Success() {
        Supply mockSupply = new Supply();
        when(supplyService.addSupply(mockSupply)).thenReturn(mockSupply);

        ResponseEntity<ApiResponse<Supply>> result = supplyController.addSupply(mockSupply);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertTrue(Objects.requireNonNull(result.getBody()).isSuccess());
        assertEquals(mockSupply, result.getBody().getPayload());

        verify(supplyService, times(1)).addSupply(mockSupply);
    }

    // Test case for adding a new supply when failed
    @Test
    void addSupply_Failure() {
        Supply mockSupply = new Supply();
        when(supplyService.addSupply(mockSupply)).thenReturn(null);

        ResponseEntity<ApiResponse<Supply>> result = supplyController.addSupply(mockSupply);

        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
        assertFalse(Objects.requireNonNull(result.getBody()).isSuccess());
        assertNull(result.getBody().getPayload());

        verify(supplyService, times(1)).addSupply(mockSupply);
    }

    // Test case for updating an existing supply successfully
    @Test
    void updateSupply_Success() {
        Supply mockSupply = new Supply();
        when(supplyService.updateSupply("supply123", mockSupply)).thenReturn(mockSupply);

        ResponseEntity<ApiResponse<Supply>> result = supplyController.updateSupply("supply123", mockSupply);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertTrue(Objects.requireNonNull(result.getBody()).isSuccess());
        assertEquals(mockSupply, result.getBody().getPayload());

        verify(supplyService, times(1)).updateSupply("supply123", mockSupply);
    }

    // Test case for updating a supply when not found
    @Test
    void updateSupply_NotFound() {
        Supply mockSupply = new Supply();
        when(supplyService.updateSupply("supply123", mockSupply)).thenReturn(null);

        ResponseEntity<ApiResponse<Supply>> result = supplyController.updateSupply("supply123", mockSupply);

        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
        assertFalse(Objects.requireNonNull(result.getBody()).isSuccess());
        assertNull(result.getBody().getPayload());

        verify(supplyService, times(1)).updateSupply("supply123", mockSupply);
    }

    // Test case for deleting a supply successfully
    @Test
    void deleteSupply_Success() {
        when(supplyService.deleteSupply("supply123")).thenReturn("Deleted");

        ResponseEntity<ApiResponse<String>> result = supplyController.deleteSupply("supply123");

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertTrue(Objects.requireNonNull(result.getBody()).isSuccess());
        assertEquals("Deleted", result.getBody().getPayload());

        verify(supplyService, times(1)).deleteSupply("supply123");
    }

    // Test case for deleting a supply when not found
    @Test
    void deleteSupply_NotFound() {
        when(supplyService.deleteSupply("supply123")).thenReturn(null);

        ResponseEntity<ApiResponse<String>> result = supplyController.deleteSupply("supply123");

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertTrue(Objects.requireNonNull(result.getBody()).isSuccess());
        assertNull(result.getBody().getPayload());

        verify(supplyService, times(1)).deleteSupply("supply123");
    }
}
