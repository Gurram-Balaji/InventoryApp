package com.App.fullStack.service;

import com.App.fullStack.config.AvailabilityConfig;
import com.App.fullStack.pojos.Supply;
import com.App.fullStack.dto.AvailabilityResponseV2V3;
import com.App.fullStack.exception.FoundException;
import com.App.fullStack.pojos.AtpThreshold;
import com.App.fullStack.pojos.Demand;
import com.App.fullStack.repositories.SupplyRepository;
import com.App.fullStack.utility.ItemAndLocationIDChecker;
import com.App.fullStack.repositories.AtpThresholdRepository;
import com.App.fullStack.repositories.DemandRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class AvailabilityService {

    @Autowired
    private SupplyRepository supplyRepository;

    @Autowired
    private AtpThresholdRepository atpThresholdRepository;

    @Autowired
    private ItemAndLocationIDChecker itemAndLocationIDChecker;

    @Autowired
    private DemandRepository demandRepository;

    @Autowired
    private AvailabilityConfig availabilityConfig;

    // v1 and v2 methods

    public int calculateAvailabilityByLocation(String itemId, String locationId) {
        itemAndLocationIDChecker.validateItemAndLocationID(itemId, locationId);
        int totalSupply = supplyRepository.findByItemIdAndLocationIdAndSupplyType(itemId, locationId, "ONHAND")
                .stream()
                .mapToInt(Supply::getQuantity)
                .sum();
        int totalDemand = demandRepository.findByItemIdAndLocationIdAndDemandType(itemId, locationId, "HARD_PROMISED")
                .stream()
                .mapToInt(Demand::getQuantity)
                .sum();
        if (totalSupply == 0 && totalDemand == 0)
            throw new FoundException(
                    "Records with ItemId: " + itemId + " And LocationId " + locationId + " not found.");
        return totalSupply - totalDemand;
    }

    public int calculateAvailabilityByItem(String itemId) {
        itemAndLocationIDChecker.validateItemId(itemId);
        int totalSupply = supplyRepository.findByItemIdAndSupplyType(itemId, "ONHAND")
                .stream()
                .mapToInt(Supply::getQuantity)
                .sum();
        int totalDemand = demandRepository.findByItemIdAndDemandType(itemId, "HARD_PROMISED")
                .stream()
                .mapToInt(Demand::getQuantity)
                .sum();
        if (totalSupply == 0 && totalDemand == 0)
            throw new FoundException("Records with ItemId: " + itemId + " not found.");
        return totalSupply - totalDemand;
    }

    public AvailabilityResponseV2V3 calculateV2AvailabilityByLocation(String itemId, String locationId) {
        Optional<AtpThreshold> threshold = atpThresholdRepository.findByItemIdAndLocationId(itemId, locationId);
        int availableQty = calculateAvailabilityByLocation(itemId, locationId);
        String stockLevel = calculateStockLevel(availableQty, threshold);

        return new AvailabilityResponseV2V3(itemId, locationId, availableQty, stockLevel);
    }

    //  v3 logic
    public AvailabilityResponseV2V3 calculateV3AvailabilityByLocation(String itemId, String locationId) {
        itemAndLocationIDChecker.validateItemAndLocationID(itemId, locationId);

        // Get valid supply and demand types from the configuration
        List<String> validSupplyTypes = Arrays.asList(availabilityConfig.getSupplies());
        List<String> validDemandTypes = Arrays.asList(availabilityConfig.getDemands());
        
        // Check if location is excluded
        if (isExcludedLocation(locationId)) {
            throw new FoundException("LocationId " + locationId + " is excluded from availability checks.");
        }

        // Calculate total supply and demand
        int totalSupply = supplyRepository.findByItemIdAndLocationIdAndSupplyTypeIn(itemId, locationId, validSupplyTypes)
                .stream()
                .mapToInt(Supply::getQuantity)
                .sum();

        int totalDemand = demandRepository.findByItemIdAndLocationIdAndDemandTypeIn(itemId, locationId, validDemandTypes)
                .stream()
                .mapToInt(Demand::getQuantity)
                .sum();

        if (totalSupply == 0 && totalDemand == 0) {
            throw new FoundException("Records with ItemId: " + itemId + " and LocationId: " + locationId + " not found.");
        }

        // Get thresholds and calculate stock level
        Optional<AtpThreshold> threshold = atpThresholdRepository.findByItemIdAndLocationId(itemId, locationId);
        String stockLevel = calculateStockLevel(totalSupply - totalDemand, threshold);

        return new AvailabilityResponseV2V3(itemId, locationId, totalSupply - totalDemand, stockLevel);
    }

    private boolean isExcludedLocation(String locationId) {
        return Arrays.asList(availabilityConfig.getExcludedLocations()).contains(locationId);
    }

    private String calculateStockLevel(int availableQty, Optional<AtpThreshold> thresholdOpt) {
        if (thresholdOpt.isPresent()) {
            AtpThreshold threshold = thresholdOpt.get();
            if (availableQty < threshold.getMinThreshold()) {
                return "Red";
            } else if (availableQty > threshold.getMaxThreshold()) {
                return "Green";
            } else {
                return "Yellow";
            }
        }
        return "Unknown";
    }
}
