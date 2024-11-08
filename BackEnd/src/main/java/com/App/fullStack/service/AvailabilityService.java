package com.App.fullStack.service;

import com.App.fullStack.dto.ScatterDataDTO;
import com.App.fullStack.dto.ScatterLocationDataDTO;
import com.App.fullStack.pojos.*;
import com.App.fullStack.dto.AvailabilityConfig;
import com.App.fullStack.dto.AvailabilityResponseV2V3;
import com.App.fullStack.exception.FoundException;
import com.App.fullStack.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class AvailabilityService {

    @Autowired
    public SupplyRepository supplyRepository;

    @Autowired
    public AtpThresholdRepository atpThresholdRepository;

    @Autowired
    public ItemRepository itemRepository;

    @Autowired
    public DemandRepository demandRepository;

    @Autowired
    public AvailabilityConfig availabilityConfig;

    @Autowired
    public LocationRepository locationRepository;


    // v1 methods
    // There is dependence of this method in v2 method
    public int calculateAvailabilityByLocation(String itemId, String locationId) {
        List<Supply> supplies = supplyRepository.findByItemIdAndLocationIdAndSupplyType(itemId, locationId, "ONHAND");
        List<Demand> demands = demandRepository.findByItemIdAndLocationIdAndDemandType(itemId, locationId,
                "HARD_PROMISED");

        if (supplies.isEmpty() && demands.isEmpty())
            throw new FoundException("Records with ItemId: " + itemId + " And LocationId " + locationId + " not found.");

        // Calculate total supply and demand
        return SupplyQTYSum(supplies) - DemandsQTYSum(demands);
    }

    public int calculateAvailabilityByItem(String itemId) {
        List<Supply> supplies = supplyRepository.findByItemIdAndSupplyType(itemId, "ONHAND");
        List<Demand> demands = demandRepository.findByItemIdAndDemandType(itemId, "HARD_PROMISED");

        if (supplies.isEmpty() && demands.isEmpty())
            throw new FoundException("Records with ItemId: " + itemId + " not found.");

        // Calculate total supply and demand
        return SupplyQTYSum(supplies) - DemandsQTYSum(demands);
    }

    // v2 methods
    public AvailabilityResponseV2V3 calculateV2AvailabilityByLocation(String itemId, String locationId) {
        int availableQty = calculateAvailabilityByLocation(itemId, locationId);
        Optional<AtpThreshold> threshold = atpThresholdRepository.findByItemIdAndLocationId(itemId, locationId);
        String stockLevel = calculateStockLevel(threshold, availableQty);

        return new AvailabilityResponseV2V3(itemId, locationId, availableQty, stockLevel);
    }


    public AvailabilityResponseV2V3 calculateV2AvailabilityInAllLocation(String itemId) {
        int availableQty = calculateAvailabilityByItem(itemId);
        Optional<AtpThreshold> threshold = atpThresholdRepository.findById(itemId);
        String stockLevel = calculateStockLevel(threshold, availableQty);

        return new AvailabilityResponseV2V3(itemId, "NETWORK", availableQty, stockLevel);
    }


    // v3 methods
    public AvailabilityResponseV2V3 calculateV3AvailabilityByLocation(String itemId, String locationId) {

        // Get valid supply and demand types from the configuration
        List<String> validSupplyTypes = Arrays.asList(availabilityConfig.getSupplies() != null ? availabilityConfig.getSupplies() : new String[]{});
        List<String> validDemandTypes = Arrays.asList(availabilityConfig.getDemands() != null ? availabilityConfig.getDemands() : new String[]{});

        // Check if location is excluded
        if (availabilityConfig.getExcludedLocations() != null && Arrays.asList(availabilityConfig.getExcludedLocations()).contains(locationId)) {
            throw new FoundException("LocationId " + locationId + " is excluded from availability checks.");
        }

        // Fetch supplies and demands based on the criteria
        List<Supply> supplies = supplyRepository.findByItemIdAndLocationIdAndSupplyTypeIn(itemId, locationId, validSupplyTypes);
        List<Demand> demands = demandRepository.findByItemIdAndLocationIdAndDemandTypeIn(itemId, locationId, validDemandTypes);

        // Check if no records are found for supply and demand
        if (supplies.isEmpty() && demands.isEmpty()) {
            throw new FoundException("Records with ItemId: " + itemId + " and LocationId: " + locationId + " not found.");
        }

        // Calculate total supply and demand
        int totalQTY = SupplyQTYSum(supplies) - DemandsQTYSum(demands);

        // Get thresholds and calculate stock level
        Optional<AtpThreshold> threshold = atpThresholdRepository.findByItemIdAndLocationId(itemId, locationId);
        String stockLevel = calculateStockLevel(threshold, totalQTY);

        return new AvailabilityResponseV2V3(itemId, locationId, totalQTY, stockLevel);
    }

    String calculateStockLevel(Optional<AtpThreshold> thresholdOpt, int availableQty) {
        if (thresholdOpt.isPresent()) {
            AtpThreshold threshold = thresholdOpt.get();
            if (availableQty < threshold.getMinThreshold())
                return "Red";
            else if (availableQty > threshold.getMaxThreshold())
                return "Green";

            return "Yellow";
        }
        return "Unknown";
    }

    private int SupplyQTYSum(List<Supply> supplies) {
        return supplies.stream()
                .mapToInt(Supply::getQuantity)
                .sum();
    }

    private int DemandsQTYSum(List<Demand> demands) {
        return demands.stream()
                .mapToInt(Demand::getQuantity)
                .sum();
    }


    public ScatterLocationDataDTO getAvailabilityScatterData(String locationId) {
        List<Item> items = itemRepository.findAll();
        // Initialize a list to store the scatter data
        List<ScatterDataDTO> scatterData = new ArrayList<>();

        String locationName;
        Optional<Location> location = locationRepository.findByLocationId(locationId);
        if (location.isPresent())
            locationName = location.get().getLocationDesc();
        else
            locationName = "NETWORK";
        // Calculate availability for each item
        for (Item item : items) {
            String itemId = item.getItemId();
            int supplyQuantity, demandQuantity;
            List<Supply> supplies;
            List<Demand> demands;
            if (Objects.equals(locationName, "NETWORK")) {

                supplies = supplyRepository.findByItemIdAndSupplyType(itemId, "ONHAND");
                demands = demandRepository.findByItemIdAndDemandType(itemId, "HARD_PROMISED");
            } else {
                supplies = supplyRepository.findByItemIdAndLocationIdAndSupplyType(itemId, locationId, "ONHAND");
                demands = demandRepository.findByItemIdAndLocationIdAndDemandType(itemId, locationId, "HARD_PROMISED");
            }
            supplyQuantity = SupplyQTYSum(supplies);
            demandQuantity = DemandsQTYSum(demands);

            scatterData.add(new ScatterDataDTO(
                    item.getPrice(), // Assuming price is stored as String
                    supplyQuantity,
                    demandQuantity,
                    item.getItemDescription()
            ));
        }
        return new ScatterLocationDataDTO(scatterData, locationName);
    }
}
