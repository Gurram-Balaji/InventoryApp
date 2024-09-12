package com.App.fullStack.controler;

import com.App.fullStack.controller.DemandController;
import com.App.fullStack.dto.DemandDTO;
import com.App.fullStack.dto.DemandDetailsResponse;
import com.App.fullStack.dto.DemandSummaryResponse;
import com.App.fullStack.pojos.Demand;
import com.App.fullStack.pojos.DemandType;
import com.App.fullStack.responseHandler.ApiResponse;
import com.App.fullStack.service.DemandService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.ResponseEntity;

import java.util.Map;
import java.util.Collections;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DemandControllerTest {

    @Mock
    private DemandService demandService;

    @InjectMocks
    private DemandController demandController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getAllDemands_Success() {
        Page<Demand> mockDemands = new PageImpl<>(Collections.singletonList(new Demand()));

        when(demandService.getAllDemands(0, 8)).thenReturn(mockDemands);

        ResponseEntity<ApiResponse<Page<Demand>>> response = demandController.getAllDemands(0, 8);

        assertEquals(200, response.getStatusCode().value());
        assertTrue(Objects.requireNonNull(response.getBody()).isSuccess());
        assertEquals("Demands Found.", response.getBody().getMessage());
        assertEquals(mockDemands, response.getBody().getPayload());

        verify(demandService, times(1)).getAllDemands(0, 8);
    }

    @Test
    void getAllDemandWithDetails_Success() {
        Page<DemandDTO> mockDemandDetails = new PageImpl<>(Collections.singletonList(new DemandDTO()));

        when(demandService.getAllDemandWithDetails(0, 8, null)).thenReturn(mockDemandDetails);

        ResponseEntity<ApiResponse<Page<DemandDTO>>> response = demandController.getAllDemandWithDetails(0, 8, null);

        assertEquals(200, response.getStatusCode().value());
        assertTrue(Objects.requireNonNull(response.getBody()).isSuccess());
        assertEquals("Demands Found.", response.getBody().getMessage());
        assertEquals(mockDemandDetails, response.getBody().getPayload());

        verify(demandService, times(1)).getAllDemandWithDetails(0, 8, null);
    }

    @Test
    void getDemandById_Success() {
        Demand mockDemand = new Demand();

        when(demandService.getDemandById("demand123")).thenReturn(mockDemand);

        ResponseEntity<ApiResponse<Demand>> response = demandController.getDemandById("demand123");

        assertEquals(200, response.getStatusCode().value());
        assertTrue(Objects.requireNonNull(response.getBody()).isSuccess());
        assertEquals("Demands Found.", response.getBody().getMessage());
        assertEquals(mockDemand, response.getBody().getPayload());

        verify(demandService, times(1)).getDemandById("demand123");
    }

    @Test
    void getDemandsByItemAndLocation_Success() {
        DemandDetailsResponse mockResponse = new DemandDetailsResponse();

        when(demandService.getDemandsByItemIdAndLocationId("item123", "location456")).thenReturn(mockResponse);

        ResponseEntity<ApiResponse<DemandDetailsResponse>> response = demandController.getDemandsByItemAndLocation("item123", "location456");

        assertEquals(200, response.getStatusCode().value());
        assertTrue(Objects.requireNonNull(response.getBody()).isSuccess());
        assertEquals("Demands Found.", response.getBody().getMessage());
        assertEquals(mockResponse, response.getBody().getPayload());

        verify(demandService, times(1)).getDemandsByItemIdAndLocationId("item123", "location456");
    }

    @Test
    void getDemandsByTypeAndLocation_Success() {

       Map<DemandType, Integer> demandDetails = Collections.singletonMap(DemandType.HARD_PROMISED, 12);
        
        DemandSummaryResponse mockResponse = new DemandSummaryResponse("location456", demandDetails);

        when(demandService.getDemandsByTypeAndLocationId(DemandType.HARD_PROMISED, "location456")).thenReturn(mockResponse);

        ResponseEntity<ApiResponse<DemandSummaryResponse>> response = demandController.getDemandsByTypeAndLocation(DemandType.HARD_PROMISED, "location456");

        assertEquals(200, response.getStatusCode().value());
        assertTrue(Objects.requireNonNull(response.getBody()).isSuccess());
        assertEquals("Demands Found.", response.getBody().getMessage());
        assertEquals(mockResponse, response.getBody().getPayload());

        verify(demandService, times(1)).getDemandsByTypeAndLocationId(DemandType.HARD_PROMISED, "location456");
    }

    @Test
    void addDemand_Success() {
        Demand mockDemand = new Demand();

        when(demandService.addDemand(mockDemand)).thenReturn(mockDemand);

        ResponseEntity<ApiResponse<Demand>> response = demandController.addDemand(mockDemand);

        assertEquals(200, response.getStatusCode().value());
        assertTrue(Objects.requireNonNull(response.getBody()).isSuccess());
        assertEquals("Demand Added.", response.getBody().getMessage());
        assertEquals(mockDemand, response.getBody().getPayload());

        verify(demandService, times(1)).addDemand(mockDemand);
    }

    @Test
    void updateDemand_Success() {
        Demand mockDemand = new Demand();

        when(demandService.updateDemand("demand123", mockDemand)).thenReturn(mockDemand);

        ResponseEntity<ApiResponse<Demand>> response = demandController.updateDemand("demand123", mockDemand);

        assertEquals(200, response.getStatusCode().value());
        assertTrue(Objects.requireNonNull(response.getBody()).isSuccess());
        assertEquals("Demand Updated.", response.getBody().getMessage());
        assertEquals(mockDemand, response.getBody().getPayload());

        verify(demandService, times(1)).updateDemand("demand123", mockDemand);
    }

    @Test
    void deleteDemand_Success() {
        when(demandService.deleteDemand("demand123")).thenReturn("Demand deleted successfully.");

        ResponseEntity<ApiResponse<String>> response = demandController.deleteDemand("demand123");

        assertEquals(200, response.getStatusCode().value());
        assertTrue(Objects.requireNonNull(response.getBody()).isSuccess());
        assertEquals("Demand Delete Operation.", response.getBody().getMessage());
        assertEquals("Demand deleted successfully.", response.getBody().getPayload());

        verify(demandService, times(1)).deleteDemand("demand123");
    }
}
