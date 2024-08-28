package com.App.fullStack.service;

import com.App.fullStack.dto.SupplyDetailsResponse;
import com.App.fullStack.dto.SupplySummaryResponse;
import com.App.fullStack.exception.FoundException;
import com.App.fullStack.pojos.Supply;
import com.App.fullStack.pojos.SupplyType;
import com.App.fullStack.repositories.SupplyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SupplyService {

    @Autowired
    private SupplyRepository supplyRepository;

    public List<Supply> getAllSupplies() {
        try {
            return supplyRepository.findAll();
        } catch (Exception e) {
            throw new FoundException("Supplies not found.");
        }

        // TODO: Advanced: Implement pagination
    }

    public Supply getSupplyById(String supplyId) {
        Optional<Supply> existingSupply = supplyRepository.findBySupplyId(supplyId);
        if (existingSupply.isPresent())
            return existingSupply.get();
        else
            throw new FoundException("Supply with supplyId " + supplyId + " not found.");
    }

    public SupplyDetailsResponse getSuppliesByItemIdAndLocationId(String itemId, String locationId) {

        List<Supply> supplies = supplyRepository.findByItemIdAndLocationId(itemId, locationId);

        // Check if the list is empty
        if (supplies.isEmpty()) {
            throw new FoundException(
                    "Supplies with ItemId: " + itemId + " and locationId " + locationId + " not found.");
        }

        Map<SupplyType, Integer> supplyDetails = supplies.stream()
                .collect(Collectors.toMap(Supply::getSupplyType, Supply::getQuantity));

        return new SupplyDetailsResponse(itemId, locationId, supplyDetails);

    }

    public SupplySummaryResponse getSuppliesByTypeAndLocationId(SupplyType supplyType, String locationId) {
        List<Supply> supplies = supplyRepository.findBySupplyTypeAndLocationId(supplyType, locationId);
        // Check if the list is empty
        if (supplies.isEmpty()) {
            throw new FoundException(
                    "Supplies with supplyType " + supplyType + " and locationId " + locationId + " not found.");
        }

        // Aggregate counts by supply type
        Map<SupplyType, Integer> supplyCounts = supplies.stream()
                .collect(Collectors.groupingBy(Supply::getSupplyType, Collectors.summingInt(Supply::getQuantity)));

        return new SupplySummaryResponse(locationId, supplyCounts);

    }

    public Supply addSupply(Supply supply) {
        
        // Optional validation logic if necessary
        if (!SupplyType.isValid(supply.getSupplyType().toString()))
            throw new FoundException("Supplies with supplyType: " + supply.getSupplyType() + " not found.");
        if (supplyRepository.existsByItemIdAndLocationIdAndSupplyType(supply.getItemId(), supply.getLocationId(),
                supply.getSupplyType()))
            throw new FoundException("Supplies with itemId: " + supply.getItemId() + ", locationId: "
                    + supply.getLocationId() + " and supplyType: " + supply.getSupplyType() + " already exists.");
        return supplyRepository.save(supply);
    }

    public Supply updateSupply(String supplyId, Supply supplyDetails) {
        Optional<Supply> existingSupply = supplyRepository.findBySupplyId(supplyId);
        if (existingSupply.isPresent()) {
            Supply supply = existingSupply.get();
            supply.setQuantity(supplyDetails.getQuantity());
            return supplyRepository.save(supply);
        } else {
            throw new FoundException("Supply with supplyId " + supplyId + " not found.");
        }
    }

    public String deleteSupply(String supplyId) {
        Optional<Supply> supply = supplyRepository.findBySupplyId(supplyId);
        if (supply.isPresent()) {
            supplyRepository.delete(supply.get());
            return "Supply deleted successfully";
        } else {
            throw new FoundException("Supply with supplyId " + supplyId + " not found.");
        }
    }
}
