package com.App.fullStack.service;

import com.App.fullStack.exception.FoundException;
import com.App.fullStack.pojos.Location;
import com.App.fullStack.repositories.LocationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.App.fullStack.repositories.DemandRepository;
import com.App.fullStack.repositories.SupplyRepository;
import java.util.List;
import java.util.Optional;

@Service
public class LocationService {

    @Autowired
    private LocationRepository locationRepository;

    @Autowired
    private SupplyRepository supplyRepository;

    @Autowired
    private DemandRepository demandRepository;

    public List<Location> getAllLocations() {
        try {
            return locationRepository.findAll();
        } catch (Exception e) {
            throw new FoundException("Locations not exist.");

        }
    }

    public Location getLocationById(String locationId) {
        Optional<Location> existingLocation = locationRepository.findByLocationId(locationId);
        if (existingLocation.isPresent())
            return existingLocation.get();
        else
            throw new FoundException("Location with locationId " +locationId + " not exist.");
    }

    public Location addLocation(Location location) {
        // Check if an item with the same itemId already exists
        Optional<Location> existinglocation = locationRepository.findByLocationId(location.getLocationId());
        if (existinglocation.isPresent())
            // Throw an exception or handle the case where the itemId already exists
            throw new FoundException("Location with locationId " + location.getLocationId() + " already exists.");
        return locationRepository.save(location);
    }

    public Location updateLocation(String locationId, Location locationDetails) {
        Optional<Location> existingLocation = locationRepository.findByLocationId(locationId);
        if (existingLocation.isPresent()) {
            // Updating fields based on the new POJO structure
            Location location = existingLocation.get();
            // Update fields only if they are not null
            if (locationDetails.getLocationDesc() != null)
                location.setLocationDesc(locationDetails.getLocationDesc());
            if (locationDetails.getLocationType() != null)
                location.setLocationType(locationDetails.getLocationType());
            if (locationDetails.isPickupAllowed())
                location.setPickupAllowed(locationDetails.isPickupAllowed());
            if (locationDetails.isShippingAllowed())
                location.setShippingAllowed(locationDetails.isShippingAllowed());
            if (locationDetails.isDeliveryAllowed())
                location.setDeliveryAllowed(locationDetails.isDeliveryAllowed());
            if (locationDetails.getAddressLine1() != null)
                location.setAddressLine1(locationDetails.getAddressLine1());
            if (locationDetails.getAddressLine2() != null)
                location.setAddressLine2(locationDetails.getAddressLine2());
            if (locationDetails.getAddressLine3() != null)
                location.setAddressLine3(locationDetails.getAddressLine3());
            if (locationDetails.getCity() != null)
                location.setCity(locationDetails.getCity());
            if (locationDetails.getState() != null)
                location.setState(locationDetails.getState());
            if (locationDetails.getCountry() != null)
                location.setCountry(locationDetails.getCountry());
            if (locationDetails.getPinCode() != null)
                location.setPinCode(locationDetails.getPinCode());
            return locationRepository.save(location);
        } else {
            throw new FoundException("Location with locationId " +locationId + " not exist.");
        }
    }

    public String deleteLocation(String locationId) {
        // Check if any supply or demand exists for this item
        boolean supplyExists = supplyRepository.existsByLocationId(locationId);
        boolean demandExists = demandRepository.existsByLocationId(locationId);

        if (supplyExists || demandExists) {
            // Throw an exception or handle the case where deletion is not allowed
            if (supplyExists && demandExists)
                throw new FoundException("Item cannot be deleted because it has associated supply or demand records.");
            if (supplyExists)
                throw new FoundException("Item cannot be deleted because it has associated supply records.");
            if (demandExists)
                throw new FoundException("Item cannot be deleted because it has associated demand records.");
        }

        Optional<Location> location = locationRepository.findByLocationId(locationId);
        if (location.isPresent()) {
            locationRepository.delete(location.get());
            return "Location deleted successfully";
        } else {
            throw new FoundException("Location with locationId " +locationId + " not exist.");
        }

    }
}
