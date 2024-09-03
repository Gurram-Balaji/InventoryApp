package com.App.fullStack.service;

import com.App.fullStack.dto.DemandDetailsResponse;
import com.App.fullStack.dto.DemandSummaryResponse;
import com.App.fullStack.exception.FoundException;
import com.App.fullStack.pojos.Demand;
import com.App.fullStack.pojos.DemandType;
import com.App.fullStack.repositories.DemandRepository;
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
public class DemandService {

    @Autowired
    public DemandRepository demandRepository;
    @Autowired
    public ItemAndLocationIDChecker itemAndLocationIDChecker;

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
}
