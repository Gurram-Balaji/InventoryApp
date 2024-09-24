package com.App.fullStack.service;

import com.App.fullStack.dto.DemandDetailsResponse;
import com.App.fullStack.dto.DemandSummaryResponse;
import com.App.fullStack.dto.DemandDTO;
import com.App.fullStack.dto.SupplyDTO;
import com.App.fullStack.exception.FoundException;
import com.App.fullStack.pojos.*;
import com.App.fullStack.repositories.DemandRepository;
import com.App.fullStack.repositories.ItemRepository;
import com.App.fullStack.repositories.LocationRepository;
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
public class DemandService {

    @Autowired
    public DemandRepository demandRepository;
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

    public Page<Demand> getAllDemands(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        Page<Demand> demand = demandRepository.findAll(pageable);

        if (demand.isEmpty())
            throw new FoundException("Demand records not found.");

        return demand;
    }

    public Demand getDemandById(String demandId) {
        Optional<Demand> existingDemand = demandRepository.findByDemandId(demandId);

        if (existingDemand.isPresent())
            return existingDemand.get();

        throw new FoundException("Demand with demandId " + demandId + " not found.");
    }

    public DemandDetailsResponse getDemandsByItemIdAndLocationId(String itemId, String locationId) {
        List<Demand> demands = demandRepository.findByItemIdAndLocationId(itemId, locationId);

        if (demands.isEmpty())
            throw new FoundException(
                    "Demands with ItemId: " + itemId + " and locationId " + locationId + " not found.");
        Map<DemandType, Integer> demandDetails = demands.stream()
                .collect(Collectors.toMap(Demand::getDemandType, Demand::getQuantity));

        return new DemandDetailsResponse(itemId, locationId, demandDetails);
    }

    public DemandSummaryResponse getDemandsByTypeAndLocationId(DemandType demandType, String locationId) {
        List<Demand> demands = demandRepository.findByDemandTypeAndLocationId(demandType, locationId);

        if (demands.isEmpty())
            throw new FoundException(
                    "Demands with demandType " + demandType + " and locationId " + locationId + " not found.");

        Map<DemandType, Integer> demandCounts = demands.stream()
                .collect(Collectors.groupingBy(Demand::getDemandType, Collectors.summingInt(Demand::getQuantity)));

        return new DemandSummaryResponse(locationId, demandCounts);
    }

    public Demand addDemand(Demand demand) {

        if (demandRepository.existsByItemIdAndLocationIdAndDemandType(demand.getItemId(), demand.getLocationId(),
                demand.getDemandType()))
            throw new FoundException("Demands with itemId: " + demand.getItemId() + ", locationId: "
                    + demand.getLocationId() + " and demandType: " + demand.getDemandType() + " already exists.");

        itemAndLocationIDChecker.validateItemAndLocationID(demand.getItemId(), demand.getLocationId());

        return demandRepository.save(demand);
    }

    public Demand updateDemand(String demandId, Demand demandDetails) {
        Optional<Demand> existingDemand = demandRepository.findByDemandId(demandId);
        if (existingDemand.isPresent()) {
            Demand demand = existingDemand.get();
            demand.setQuantity(demandDetails.getQuantity());
            return demandRepository.save(demand);
        }
        throw new FoundException("Demand with demandId " + demandId + " not found.");
    }

    public String deleteDemand(String demandId) {

        Optional<Demand> demand = demandRepository.findByDemandId(demandId);

        if (demand.isPresent()) {
            demandRepository.delete(demand.get());
            return "Demand deleted successfully";
        }
        throw new FoundException("Demand with demandId " + demandId + " not found.");
    }

    public Page<DemandDTO> getAllDemandWithDetails(int page, int size, String search, String searchBy) {
        Pageable pageable = PageRequest.of(page, size);

        if (search != null && !search.trim().isEmpty()) {
            Page<Demand> Demands = null;
            if(Objects.equals(searchBy, "item")) {
                List<Item> itemIdList = itemRepository.searchItemIdsByKeywordGetIds(search);
                if (itemIdList.isEmpty())
                    throw new FoundException("Demands not found.");
                List<String> itemIds = itemIdList.stream()
                        .map(Item::getItemId)
                        .collect(Collectors.toList());

                Demands = demandRepository.findByItemIdIn(itemIds, pageable);

                if (Demands.getContent().isEmpty())
                    throw new FoundException("Demands not found..");
            }

            if(Objects.equals(searchBy, "location")) {
                List<Location> locationsIdList = locationRepository.searchLocationIdsByKeywordGetIds(search);
                if (locationsIdList.isEmpty())
                    throw new FoundException("Demands not found.");
                List<String> locationsIds = locationsIdList.stream()
                        .map(Location::getLocationId)
                        .collect(Collectors.toList());

                Demands = demandRepository.findByLocationIdIn(locationsIds, pageable);

                if (Demands.getContent().isEmpty())
                    throw new FoundException("Demands not found..");
            }


            if(Objects.equals(searchBy, "demandType")) {

                Demands = demandRepository.findByDemandType(search, pageable);

                if (Demands.getContent().isEmpty())
                    throw new FoundException("Demands not found..");
            }

            assert Demands != null;
            List<DemandDTO> demandDTO = addAlldemandDetails(Demands.getContent());
            // Return the PageImpl with the paginated results and total count of the original list
            return new PageImpl<>(demandDTO, pageable, Demands.getTotalElements());
        } else {
            Page<Demand> demands = demandRepository.findAll(pageable);
            if (demands.isEmpty())
                throw new FoundException("Demands not found.");

            // Convert Demand to DemandDTO
           List<DemandDTO> demandDTOs =addAlldemandDetails(demands.getContent());

            return new PageImpl<>(demandDTOs, pageable, demands.getTotalElements());
        }
    }

    public List<DemandDTO> addAlldemandDetails(List<Demand> demands) {
        // Convert Demand to DemandDTO
        List<DemandDTO> demandDTOs = new ArrayList<>();
        for (Demand demand : demands) {
            Item item = itemService.getItemByItemIdWithOutException(demand.getItemId());
            Location location = locationService.getLocationByIdWithoutException(demand.getLocationId());

            DemandDTO demandDTO = new DemandDTO(
                    demand.getDemandId(),
                    demand.getItemId(),
                    item != null ? item.getItemDescription() : null,
                    demand.getLocationId(),
                    location != null ? location.getLocationDesc() : null,
                    demand.getDemandType(),
                    demand.getQuantity()
            );
            demandDTOs.add(demandDTO);
        }
        return demandDTOs;
    }
}

