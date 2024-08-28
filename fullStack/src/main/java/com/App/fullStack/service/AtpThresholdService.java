package com.App.fullStack.service;

import com.App.fullStack.exception.FoundException;
import com.App.fullStack.pojos.AtpThreshold;
import com.App.fullStack.repositories.AtpThresholdRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AtpThresholdService {

    @Autowired
    private AtpThresholdRepository atpThresholdRepository;

    public List<AtpThreshold> getAllAtpThresholds() {
        List<AtpThreshold> thresholds = atpThresholdRepository.findAll();
        if (thresholds.isEmpty()) {
            throw new FoundException("No ATP Thresholds found.");
        }
        return thresholds;
    }

    public AtpThreshold getAtpThresholdById(String thresholdId) {
        Optional<AtpThreshold> existingAtpThreshold = atpThresholdRepository.findByThresholdId(thresholdId);
        if (existingAtpThreshold.isPresent())
            return existingAtpThreshold.get();
        else
            throw new FoundException("ATP Threshold with ID " + thresholdId + " not found.");
    }

    public AtpThreshold getAtpThresholdByItemAndLocation(String itemId, String locationId) {
        Optional<AtpThreshold> AtpThreshold = atpThresholdRepository.findByItemIdAndLocationId(itemId,
                locationId);
        if (AtpThreshold.isPresent())
            return AtpThreshold.get();
        else
            throw new FoundException("ATP Threshold with Item ID " + itemId + " and Location ID " + locationId + " not found.");
    }


    public AtpThreshold createAtpThreshold(AtpThreshold atpThreshold) {
        if (atpThresholdRepository.existsByItemIdAndLocationId(atpThreshold.getItemId(),
                atpThreshold.getLocationId())) {
            throw new FoundException("ATP Threshold for Item ID " + atpThreshold.getItemId() +
                    " and Location ID " + atpThreshold.getLocationId() + " already exists.");
        }
        return atpThresholdRepository.save(atpThreshold);
    }

    public AtpThreshold updateAtpThresholdById(String thresholdId, AtpThreshold atpThresholdDetails) {
        AtpThreshold existingThreshold = getAtpThresholdById(thresholdId);
        existingThreshold.setMinThreshold(atpThresholdDetails.getMinThreshold());
        existingThreshold.setMaxThreshold(atpThresholdDetails.getMaxThreshold());
        return atpThresholdRepository.save(existingThreshold);
    }


    public AtpThreshold updateAtpThresholdByItemAndLocation(String itemId, String locationId,
            AtpThreshold atpThresholdDetails) {
        AtpThreshold existingThreshold = getAtpThresholdByItemAndLocation(itemId, locationId);
        existingThreshold.setMinThreshold(atpThresholdDetails.getMinThreshold());
        existingThreshold.setMaxThreshold(atpThresholdDetails.getMaxThreshold());
        return atpThresholdRepository.save(existingThreshold);
    }

    
    public String deleteAtpThresholdById(String thresholdId) {
        Optional<AtpThreshold> existingThreshold = atpThresholdRepository.findByThresholdId(thresholdId);
          if (existingThreshold.isPresent()) {
            atpThresholdRepository.delete(existingThreshold.get());
            return "Threshold deleted successfully.";
        } else {
            throw new FoundException("Demand with demandId " + thresholdId + " not found.");
        }

    }
}
