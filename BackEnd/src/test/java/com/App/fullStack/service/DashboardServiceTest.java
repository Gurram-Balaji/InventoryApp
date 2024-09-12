package com.App.fullStack.service;

import com.App.fullStack.repositories.DemandRepository;
import com.App.fullStack.repositories.ItemRepository;
import com.App.fullStack.repositories.LocationRepository;
import com.App.fullStack.repositories.SupplyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

class DashboardServiceTest {

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private LocationRepository locationRepository;

    @Mock
    private SupplyRepository supplyRepository;

    @Mock
    private DemandRepository demandRepository;

    @InjectMocks
    private DashboardService dashboardService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetDashboardData() {
        // Setup
        long totalItems = 100;
        long totalLocations = 50;
        long totalSupplies = 200;
        long totalDemands = 150;

        when(itemRepository.count()).thenReturn(totalItems);
        when(locationRepository.count()).thenReturn(totalLocations);
        when(supplyRepository.count()).thenReturn(totalSupplies);
        when(demandRepository.count()).thenReturn(totalDemands);

        // Expected result
        Map<String, Object> expectedDashboardData = new HashMap<>();
        expectedDashboardData.put("totalItems", totalItems);
        expectedDashboardData.put("totalLocations", totalLocations);
        expectedDashboardData.put("totalSupplies", totalSupplies);
        expectedDashboardData.put("totalDemands", totalDemands);

        // Test
        Map<String, Object> actualDashboardData = dashboardService.getDashboardData();

        // Verify
        assertEquals(expectedDashboardData, actualDashboardData);
    }
}
