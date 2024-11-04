package com.App.fullStack.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class DashboardServiceTest {

    @InjectMocks
    private DashboardService dashboardService;

    @Mock
    private MongoTemplate mongoTemplate;

    @Mock
    private AggregationResults<Map> aggregationResults;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getDashboardData_ShouldReturnCorrectCounts() {
        // Arrange
        when(mongoTemplate.aggregate(any(Aggregation.class), eq("items"), eq(Map.class)))
                .thenReturn(aggregationResults);
        when(mongoTemplate.aggregate(any(Aggregation.class), eq("locations"), eq(Map.class)))
                .thenReturn(aggregationResults);
        when(mongoTemplate.aggregate(any(Aggregation.class), eq("supplies"), eq(Map.class)))
                .thenReturn(aggregationResults);
        when(mongoTemplate.aggregate(any(Aggregation.class), eq("demands"), eq(Map.class)))
                .thenReturn(aggregationResults);

        // Mocking count results
        when(aggregationResults.getUniqueMappedResult()).thenReturn(Map.of("count", 10));

        // Act
        Map<String, Object> result = dashboardService.getDashboardData();

        // Assert
        assertEquals(10L, result.get("totalItems"));
        assertEquals(10L, result.get("totalLocations"));
        assertEquals(10L, result.get("totalSupplies"));
        assertEquals(10L, result.get("totalDemands"));
    }

    @Test
    void getCount_ShouldReturnCount_WhenDocumentsExist() {
        // Arrange
        when(mongoTemplate.aggregate(any(Aggregation.class), eq("items"), eq(Map.class)))
                .thenReturn(aggregationResults);
        when(aggregationResults.getUniqueMappedResult()).thenReturn(Map.of("count", 5));

        // Act
        long count = dashboardService.getCount("items");

        // Assert
        assertEquals(5, count);
    }

    @Test
    void getCount_ShouldReturnZero_WhenNoDocumentsExist() {
        // Arrange
        when(mongoTemplate.aggregate(any(Aggregation.class), eq("items"), eq(Map.class)))
                .thenReturn(aggregationResults);
        when(aggregationResults.getUniqueMappedResult()).thenReturn(null);

        // Act
        long count = dashboardService.getCount("items");

        // Assert
        assertEquals(0, count);
    }
}
