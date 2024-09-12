package com.App.fullStack.service;

import com.App.fullStack.dto.DemandDetailsResponse;
import com.App.fullStack.dto.DemandSummaryResponse;
import com.App.fullStack.dto.DemandDTO;
import com.App.fullStack.exception.FoundException;
import com.App.fullStack.pojos.Demand;
import com.App.fullStack.pojos.DemandType;
import com.App.fullStack.pojos.Item;
import com.App.fullStack.pojos.Location;
import com.App.fullStack.repositories.DemandRepository;
import com.App.fullStack.utility.ItemAndLocationIDChecker;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class DemandService {

    @Autowired
    public DemandRepository demandRepository;
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

    public Page<DemandDTO> getAllDemandWithDetails(int page, int size, String search) {
        Pageable pageable = PageRequest.of(page, size);

        // Fetch all demands (or add a better way of filtering directly from DB using
        // pageable)
        List<Demand> demands = demandRepository.findAll();

        if (demands.isEmpty()) {
            throw new FoundException("Demands not found.");
        }

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
                    demand.getQuantity());

            demandDTOs.add(demandDTO);
        }

        // Perform the search only if the search string is not null and not empty
        if (search != null && !search.trim().isEmpty()) {
            demandDTOs = demandDTOs.stream()
                    .filter(dto -> (dto.getDemandId() != null
                            && dto.getDemandId().toLowerCase().contains(search.toLowerCase())) ||
                            (dto.getItemId() != null && dto.getItemId().toLowerCase().contains(search.toLowerCase())) ||
                            (dto.getItemDescription() != null
                                    && dto.getItemDescription().toLowerCase().contains(search.toLowerCase()))
                            ||
                            (dto.getLocationId() != null
                                    && dto.getLocationId().toLowerCase().contains(search.toLowerCase()))
                            ||
                            (dto.getLocationDescription() != null
                                    && dto.getLocationDescription().toLowerCase().contains(search.toLowerCase()))
                            ||
                            (dto.getDemandType() != null
                                    && dto.getDemandType().toString().toLowerCase().contains(search.toLowerCase()))
                            ||
                            String.valueOf(dto.getQuantity()).contains(search))
                    .collect(Collectors.toList());
        }

        // Manually handle pagination
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), demandDTOs.size());
        List<DemandDTO> paginatedList = demandDTOs.subList(start, end);

        // Return the PageImpl with the paginated results and total count of the
        // original list
        return new PageImpl<>(paginatedList, pageable, demandDTOs.size());
    }

}
