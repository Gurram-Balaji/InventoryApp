package com.App.fullStack.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.group;

@Service
public class DashboardService {

    @Autowired
    private MongoTemplate mongoTemplate;

    public Map<String, Object> getDashboardData() {
        Map<String, Object> dashboardData = new HashMap<>();

        // Fetch counts for each collection using aggregation
        long totalItems = getCount("items");
        long totalLocations = getCount("locations");
        long totalSupplies = getCount("supplies");
        long totalDemands = getCount("demands");

        // Add counts to the map
        dashboardData.put("totalItems", totalItems);
        dashboardData.put("totalLocations", totalLocations);
        dashboardData.put("totalSupplies", totalSupplies);
        dashboardData.put("totalDemands", totalDemands);

        return dashboardData;
    }

    long getCount(String collectionName) {
        // Aggregation pipeline to count documents in the collection
        Aggregation aggregation = Aggregation.newAggregation(
                group().count().as("count")
        );

        // Execute the aggregation and return a typed result
        AggregationResults<Map> results = mongoTemplate.aggregate(aggregation, collectionName, Map.class);

        // Extract the count from the results
        Map resultMap = results.getUniqueMappedResult();

        // Return the count or 0 if no result is found
        return resultMap != null ? ((Number) resultMap.get("count")).longValue() : 0;
    }
}
