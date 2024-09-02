package com.App.fullStack.service;

import com.App.fullStack.dto.SupplyDetailsResponse;
import com.App.fullStack.dto.SupplySummaryResponse;
import com.App.fullStack.exception.FoundException;
import com.App.fullStack.pojos.Supply;
import com.App.fullStack.pojos.SupplyType;
import com.App.fullStack.repositories.SupplyRepository;
import com.App.fullStack.utility.ItemAndLocationIDChecker;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SupplyService {

    @Autowired
    public SupplyRepository supplyRepository;

    @Autowired
    public ItemAndLocationIDChecker itemAndLocationIDChecker;

    public Page<Supply> getAllSupplies(int page, int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<Supply> supply = supplyRepository.findAll(pageable);

        if (supply.isEmpty())
            throw new FoundException("Supplies not found.");

        return supply;
    }

    public Supply getSupplyById(String supplyId) {

        Optional<Supply> existingSupply = supplyRepository.findBySupplyId(supplyId);

        if (existingSupply.isPresent())
            return existingSupply.get();

        throw new FoundException("Supply with supplyId " + supplyId + " not found.");

    }

    public SupplyDetailsResponse getSuppliesByItemIdAndLocationId(String itemId, String locationId) {

        List<Supply> supplies = supplyRepository.findByItemIdAndLocationId(itemId, locationId);

        // Check if the list is empty
        if (supplies.isEmpty())
            throw new FoundException(
                    "Supplies with ItemId: " + itemId + " and locationId " + locationId + " not found.");

        Map<SupplyType, Integer> supplyDetails = supplies.stream()
                .collect(Collectors.toMap(Supply::getSupplyType, Supply::getQuantity));

        return new SupplyDetailsResponse(itemId, locationId, supplyDetails);

    }

    public SupplySummaryResponse getSuppliesByTypeAndLocationId(SupplyType supplyType, String locationId) {

        List<Supply> supplies = supplyRepository.findBySupplyTypeAndLocationId(supplyType, locationId);

        // Check if the list is empty
        if (supplies.isEmpty())
            throw new FoundException(
                    "Supplies with supplyType " + supplyType + " and locationId " + locationId + " not found.");

        // Aggregate counts by supply type
        Map<SupplyType, Integer> supplyCounts = supplies.stream()
                .collect(Collectors.groupingBy(Supply::getSupplyType, Collectors.summingInt(Supply::getQuantity)));

        return new SupplySummaryResponse(locationId, supplyCounts);

    }

    public Supply addSupply(Supply supply) {

        if (supplyRepository.existsByItemIdAndLocationIdAndSupplyType(supply.getItemId(), supply.getLocationId(),
                supply.getSupplyType()))
            throw new FoundException("Supplies with itemId: " + supply.getItemId() + ", locationId: "
                    + supply.getLocationId() + " and supplyType: " + supply.getSupplyType() + " already exists.");

        itemAndLocationIDChecker.validateItemAndLocationID(supply.getItemId(), supply.getLocationId());

        return supplyRepository.save(supply);

    }

    public Supply updateSupply(String supplyId, Supply supplyDetails) {

        Optional<Supply> existingSupply = supplyRepository.findBySupplyId(supplyId);

        if (existingSupply.isPresent()) {
            Supply supply = existingSupply.get();
            supply.setQuantity(supplyDetails.getQuantity());
            return supplyRepository.save(supply);
        }

        throw new FoundException("Supply with supplyId " + supplyId + " not found.");
    }

    public String deleteSupply(String supplyId) {

        Optional<Supply> supply = supplyRepository.findBySupplyId(supplyId);

        if (supply.isPresent()) {
            supplyRepository.delete(supply.get());
            return "Supply deleted successfully";
        }

        throw new FoundException("Supply with supplyId " + supplyId + " not found.");

    }
}
