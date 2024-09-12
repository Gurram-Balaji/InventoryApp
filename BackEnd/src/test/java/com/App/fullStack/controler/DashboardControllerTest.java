package com.App.fullStack.controler;

import com.App.fullStack.controller.DashboardController;
import com.App.fullStack.responseHandler.ApiResponse;
import com.App.fullStack.service.DashboardService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DashboardControllerTest {

    @Mock
    private DashboardService dashboardService;

    @InjectMocks
    private DashboardController dashboardController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getDashboardData_Success() {
        // Prepare mock data
        Map<String, Object> mockDashboardData = new HashMap<>();
        mockDashboardData.put("totalItems", 20);
        mockDashboardData.put("totalLocations", 10);
        mockDashboardData.put("totalSupplies", 15);
        mockDashboardData.put("totalDemands", 12);

        // Mock the service method call
        when(dashboardService.getDashboardData()).thenReturn(mockDashboardData);

        // Call the controller method
        ResponseEntity<ApiResponse<Map<String, Object>>> response = dashboardController.getDashboardData();

        // Verify the response
        assertEquals(200, response.getStatusCode().value());
        assertTrue(Objects.requireNonNull(response.getBody()).isSuccess());
        assertEquals("Dashboard Data Retrieved", response.getBody().getMessage());
        assertEquals(mockDashboardData, response.getBody().getPayload());

        // Verify that the service method was called once
        verify(dashboardService, times(1)).getDashboardData();
    }

    @Test
    void getDashboardData_EmptyData() {
        // Prepare mock empty data
        Map<String, Object> mockDashboardData = new HashMap<>();

        // Mock the service method call
        when(dashboardService.getDashboardData()).thenReturn(mockDashboardData);

        // Call the controller method
        ResponseEntity<ApiResponse<Map<String, Object>>> response = dashboardController.getDashboardData();

        // Verify the response
        assertEquals(200, response.getStatusCode().value());
        assertTrue(Objects.requireNonNull(response.getBody()).isSuccess());
        assertEquals("Dashboard Data Retrieved", response.getBody().getMessage());
        assertTrue(response.getBody().getPayload().isEmpty());

        // Verify that the service method was called once
        verify(dashboardService, times(1)).getDashboardData();
    }
}
