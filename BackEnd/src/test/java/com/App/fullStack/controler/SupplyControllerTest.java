package com.App.fullStack.controler;

import com.App.fullStack.controller.SupplyController;
import com.App.fullStack.pojos.Supply;
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

    @Test
    void getAllSupplies_Success() {
        Page<Supply> mockPage = mock(Page.class);
        ApiResponse<Page<Supply>> response = new ApiResponse<>(true, "Supplies Found", mockPage);
        when(supplyService.getAllSupplies(0, 8)).thenReturn(mockPage);

        ResponseEntity<ApiResponse<Page<Supply>>> result = supplyController.getAllSupplies(0, 8);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(response.isSuccess(), Objects.requireNonNull(result.getBody()).isSuccess());
    }

    @Test
    void getSupplyById_Success() {
        Supply supply = new Supply();
        ApiResponse<Supply> response = new ApiResponse<>(true, "Supply Found", supply);
        when(supplyService.getSupplyById("supplyId")).thenReturn(supply);

        ResponseEntity<ApiResponse<Supply>> result = supplyController.getSupplyById("supplyId");

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(response.getPayload(), Objects.requireNonNull(result.getBody()).getPayload());
    }

    @Test
    void addSupply_Success() {
        Supply supply = new Supply();
        ApiResponse<Supply> response = new ApiResponse<>(true, "Supply Added", supply);
        when(supplyService.addSupply(supply)).thenReturn(supply);

        ResponseEntity<ApiResponse<Supply>> result = supplyController.addSupply(supply);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(response.getPayload(), Objects.requireNonNull(result.getBody()).getPayload());
    }

    @Test
    void deleteSupply_Success() {
        ApiResponse<String> response = new ApiResponse<>(true, "Supply Delete Operation.", "supplyId");
        when(supplyService.deleteSupply("supplyId")).thenReturn("supplyId");

        ResponseEntity<ApiResponse<String>> result = supplyController.deleteSupply("supplyId");

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(response.getPayload(), Objects.requireNonNull(result.getBody()).getPayload());
    }
}
