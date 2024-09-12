package com.App.fullStack.service;

import com.App.fullStack.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class DashboardService {

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private LocationRepository locationRepository;

    @Autowired
    private SupplyRepository supplyRepository;

    @Autowired
    private DemandRepository demandRepository;

    public Map<String, Object> getDashboardData() {
        Map<String, Object> dashboardData = new HashMap<>();

        // Total counts
        long totalItems = itemRepository.count();
        long totalLocations = locationRepository.count();
        long totalSupplies = supplyRepository.count();
        long totalDemands = demandRepository.count();

        // Add total counts to response
        dashboardData.put("totalItems", totalItems);
        dashboardData.put("totalLocations", totalLocations);
        dashboardData.put("totalSupplies", totalSupplies);
        dashboardData.put("totalDemands", totalDemands);

        return dashboardData;
    }
}
