package com.App.fullStack.service;

import com.App.fullStack.exception.FoundException;
import com.App.fullStack.pojos.AtpThreshold;
import com.App.fullStack.repositories.AtpThresholdRepository;
import com.App.fullStack.utility.ItemAndLocationIDChecker;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AtpThresholdService {

    @Autowired
    public AtpThresholdRepository atpThresholdRepository;

    @Autowired
    public ItemAndLocationIDChecker itemAndLocationIDChecker;

    public Page<AtpThreshold> getAllAtpThresholds(int page,int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<AtpThreshold> thresholds = atpThresholdRepository.findAll(pageable);
        if (thresholds.isEmpty())
            throw new FoundException("No ATP Thresholds found.");

        return thresholds;
    }

    public AtpThreshold getAtpThresholdById(String thresholdId) {
        if(thresholdId==null)
            throw new FoundException("ATP Threshold with ID null not found.");
        Optional<AtpThreshold> existingAtpThreshold = atpThresholdRepository.findByThresholdId(thresholdId);
        if (existingAtpThreshold.isPresent())
            return existingAtpThreshold.get();
        throw new FoundException("ATP Threshold with ID " + thresholdId + " not found.");
    }

    public AtpThreshold getAtpThresholdByItemAndLocation(String itemId, String locationId) {
        if(itemId==null || locationId==null)
            throw new FoundException("ATP Threshold with Item ID null and Location ID null not found.");
        Optional<AtpThreshold> AtpThreshold = atpThresholdRepository.findByItemIdAndLocationId(itemId,
                locationId);
        if (AtpThreshold.isPresent())
            return AtpThreshold.get();
        throw new FoundException(
                "ATP Threshold with Item ID " + itemId + " and Location ID " + locationId + " not found.");
    }

    public AtpThreshold AddAtpThreshold(AtpThreshold atpThreshold) {
        if(atpThreshold==null)
            throw new FoundException("Cannot add null ATP Threshold.");
        if(atpThreshold.getMaxThreshold()==0 && atpThreshold.getItemId()==null && atpThreshold.getLocationId()==null)
            throw new FoundException("Cannot add empty ATP Threshold.");
        if(atpThreshold.getMinThreshold()>atpThreshold.getMaxThreshold())
            throw new FoundException("ATP Threshold Min is greater then Max Threshold.");

        if (atpThresholdRepository.existsByItemIdAndLocationId(atpThreshold.getItemId(),
                atpThreshold.getLocationId())) {
            throw new FoundException("ATP Threshold for Item ID " + atpThreshold.getItemId() +
                    " and Location ID " + atpThreshold.getLocationId() + " already exists.");
        }
        itemAndLocationIDChecker.validateItemAndLocationID(atpThreshold.getItemId(), atpThreshold.getLocationId());

        return atpThresholdRepository.save(atpThreshold);
    }

    public AtpThreshold updateAtpThresholdById(String thresholdId, AtpThreshold atpThresholdDetails) {
        if(atpThresholdDetails==null || thresholdId==null)
            throw new FoundException("Cannot update with null details.");
        return updateAndSaveThreshold(getAtpThresholdById(thresholdId), atpThresholdDetails);
    }

    public AtpThreshold updateAtpThresholdByItemAndLocation(String itemId, String locationId,
                                                            AtpThreshold atpThresholdDetails) {

        if(atpThresholdDetails==null)
            throw new FoundException("Cannot update with null details.");
        return updateAndSaveThreshold(getAtpThresholdByItemAndLocation(itemId, locationId), atpThresholdDetails);

    }

    public String deleteAtpThresholdById(String thresholdId) {
        Optional<AtpThreshold> existingThreshold = atpThresholdRepository.findByThresholdId(thresholdId);
        if (existingThreshold.isPresent()) {
            atpThresholdRepository.delete(existingThreshold.get());
            return "Threshold deleted successfully.";
        }
        throw new FoundException("Demand with demandId " + thresholdId + " not found.");

    }

    private AtpThreshold updateAndSaveThreshold(AtpThreshold existingThreshold, AtpThreshold atpThresholdDetails) {
        existingThreshold.setMinThreshold(atpThresholdDetails.getMinThreshold());
        existingThreshold.setMaxThreshold(atpThresholdDetails.getMaxThreshold());
        return atpThresholdRepository.save(existingThreshold);
    }

}
