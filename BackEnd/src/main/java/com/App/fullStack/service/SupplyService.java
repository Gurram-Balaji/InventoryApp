package com.App.fullStack.service;

import com.App.fullStack.dto.SupplyDetailsResponse;
import com.App.fullStack.dto.SupplySummaryResponse;
import com.App.fullStack.exception.FoundException;
import com.App.fullStack.pojos.Item;
import com.App.fullStack.pojos.Location;
import com.App.fullStack.pojos.Supply;
import com.App.fullStack.pojos.SupplyType;
import com.App.fullStack.repositories.ItemRepository;
import com.App.fullStack.repositories.LocationRepository;
import com.App.fullStack.repositories.SupplyRepository;
import com.App.fullStack.dto.SupplyDTO;

import com.App.fullStack.utility.ItemAndLocationIDChecker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class SupplyService {

    @Autowired
    public SupplyRepository supplyRepository;

    @Autowired
    public ItemRepository itemRepository;

    @Autowired
    public LocationRepository locationRepository;

    @Autowired
    public ItemAndLocationIDChecker itemAndLocationIDChecker;
    @Autowired
    private ItemService itemService;

    @Autowired
    private LocationService locationService;

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

        if(supplyDetails.getQuantity()<=0)
            throw new FoundException("Invalid quantity");
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

    public Page<SupplyDTO> getAllSuppliesWithDetails(int page, int size, String search, String searchBy) {
        Pageable pageable = PageRequest.of(page, size);
        if (search != null && !search.trim().isEmpty()) {
            Page<Supply> supplies = null;
            if (Objects.equals(searchBy, "item")) {
                List<Item> itemIdList = itemRepository.searchItemIdsByKeywordGetIds(search);
                if (itemIdList.isEmpty())
                    throw new FoundException("Supplies not found.");
                List<String> itemIds = itemIdList.stream()
                        .map(Item::getItemId)
                        .collect(Collectors.toList());

                supplies = supplyRepository.findByItemIdIn(itemIds, pageable);

                if (supplies.getContent().isEmpty())
                    throw new FoundException("Supplies not found.");
            }

            if (Objects.equals(searchBy, "location")) {
                List<Location> locationsIdList = locationRepository.searchLocationIdsByKeywordGetIds(search);
                if (locationsIdList.isEmpty())
                    throw new FoundException("Supplies not found.");
                List<String> locationsIds = locationsIdList.stream()
                        .map(Location::getLocationId)
                        .collect(Collectors.toList());

                supplies = supplyRepository.findByLocationIdIn(locationsIds, pageable);

                if (supplies.getContent().isEmpty())
                    throw new FoundException("Supplies not found..");
            }


            if (Objects.equals(searchBy, "supplyType")) {

                supplies = supplyRepository.findBySupplyType(search, pageable);

                if (supplies.getContent().isEmpty())
                    throw new FoundException("Supplies not found..");
            }

            assert supplies != null;
            List<SupplyDTO> supplyDTOs = addAllSuppliesWithDetails(supplies.getContent());
            // Return the PageImpl with the paginated results and total count of the original list
            return new PageImpl<>(supplyDTOs, pageable, supplies.getTotalElements());

        } else {
            // Fetch supplies with pagination directly from the DB
            Page<Supply> supplyPage = supplyRepository.findAll(pageable);

            if (supplyPage.isEmpty()) {
                throw new FoundException("Supplies not found.");
            }
            List<SupplyDTO> supplyDTOs = addAllSuppliesWithDetails(supplyPage.getContent());
            // Return a new PageImpl containing the SupplyDTOs, with the original pagination details
            return new PageImpl<>(supplyDTOs, pageable, supplyPage.getTotalElements());

        }
    }

    public List<SupplyDTO> addAllSuppliesWithDetails(List<Supply> supplies) {
        // Convert Supply to SupplyDTO
        List<SupplyDTO> supplyDTOs = new ArrayList<>();
        for (Supply supply : supplies) {
            Item item = itemService.getItemByItemIdWithOutException(supply.getItemId());
            Location location = locationService.getLocationByIdWithoutException(supply.getLocationId());

            SupplyDTO supplyDTO = new SupplyDTO(
                    supply.getSupplyId(),
                    supply.getItemId(),
                    item != null ? item.getItemDescription() : null,
                    supply.getLocationId(),
                    location != null ? location.getLocationDesc() : null,
                    supply.getSupplyType(),
                    supply.getQuantity()
            );
            supplyDTOs.add(supplyDTO);
        }
        return supplyDTOs;
    }


}
