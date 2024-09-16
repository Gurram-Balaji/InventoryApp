package com.App.fullStack.service;

import com.App.fullStack.dto.LocationData;
import com.App.fullStack.exception.FoundException;
import com.App.fullStack.pojos.Location;
import com.App.fullStack.repositories.LocationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.App.fullStack.repositories.DemandRepository;
import com.App.fullStack.repositories.SupplyRepository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LocationService {

    @Autowired
    public LocationRepository locationRepository;

    @Autowired
    public SupplyRepository supplyRepository;

    @Autowired
    public DemandRepository demandRepository;

    public Page<Location> getAllLocations(int page, int size, String keyword) {
        Pageable pageable = PageRequest.of(page, size);

        if (keyword != null && !keyword.isEmpty()) {
            return locationRepository.searchLocationsByKeyword(keyword, pageable);
        } else {
            return locationRepository.findAll(pageable);
        }
    }

    public Location getLocationById(String locationId) {
        Optional<Location> existingLocation = locationRepository.findByLocationId(locationId);

        if (existingLocation.isPresent())
            return existingLocation.get();

        throw new FoundException("Location with locationId " + locationId + " not exist.");
    }

    public Location addLocation(Location location) {
        // Check if an item with the same itemId already exists
        Optional<Location> existingLocation = locationRepository.findByLocationId(location.getLocationId());

        // Throw an exception or handle the case where the itemId already exists
        if (existingLocation.isPresent())
            throw new FoundException("Location with locationId " + location.getLocationId() + " already exists.");

        return locationRepository.save(location);
    }

    public Location updateLocation(String locationId, Location locationDetails) {
        Optional<Location> existingLocation = locationRepository.findByLocationId(locationId);

        if (existingLocation.isPresent()) {
            // Retrieve the existing location entity
            Location location = existingLocation.get();

            // Update fields from locationDetails
            if (locationDetails.getLocationDesc() != null)
                location.setLocationDesc(locationDetails.getLocationDesc());

            if (locationDetails.getLocationType() != null)
                location.setLocationType(locationDetails.getLocationType());

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

            // Update boolean values
            location.setPickupAllowed(locationDetails.isPickupAllowed());
            location.setShippingAllowed(locationDetails.isShippingAllowed());
            location.setDeliveryAllowed(locationDetails.isDeliveryAllowed());

            // Save the updated location object to the database
            return locationRepository.save(location);
        }

        throw new FoundException("Location with locationId " + locationId + " not exist.");
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
            throw new FoundException("Item cannot be deleted because it has associated demand records.");
        }

        Optional<Location> location = locationRepository.findByLocationId(locationId);
        if (location.isPresent()) {
            locationRepository.delete(location.get());
            return "Location deleted successfully";
        }
        throw new FoundException("Location with locationId " + locationId + " not exist.");
    }

    public Page<String> getAllLocationIds(int page,int size,String search) {
        Pageable pageable = PageRequest.of(page, size);

        if (search != null && !search.isEmpty()) {
            return locationRepository.searchLocationIdsByKeyword(search, pageable);
        } else {
            return locationRepository.findDistinctLocationIds(pageable);
        }

    }

    public Location getLocationByIdWithoutException(String locationId) {

        Optional<Location> existingLocation = locationRepository.findByLocationId(locationId);

        if (existingLocation.isPresent())
            return existingLocation.get();
        else
            return null;
    }

    public List<LocationData> getStackedBarData() {
        // Fetch all locations
        List<Location> locations = locationRepository.findAll();

        // Map each location to LocationData DTO
        return locations.stream().map(location -> {
            // Fetch supply and demand data for each location
            Map<String, Integer> supplyDetails = new HashMap<>();
            Map<String, Integer> demandDetails = new HashMap<>();

            // Supply: Assuming supplyRepository provides methods to get supply counts by type
            int onHand = supplyRepository.getTotalSupplyByLocationAndType(location.getLocationId(), "ONHAND");
            int inTransit = supplyRepository.getTotalSupplyByLocationAndType(location.getLocationId(), "INTRANSIT");

            supplyDetails.put("ONHAND", onHand);
            supplyDetails.put("INTRANSIT", inTransit);

            // Demand: Assuming demandRepository provides methods to get demand counts by type
            int hardPromised = demandRepository.getTotalDemandByLocationAndType(location.getLocationId(), "HARD_PROMISED");
            int planned = demandRepository.getTotalDemandByLocationAndType(location.getLocationId(), "PLANNED");

            demandDetails.put("HARD_PROMISED", hardPromised);
            demandDetails.put("PLANNED", planned);

            // Create and return the LocationData object
            return new LocationData(location.getLocationId(), location.getLocationDesc(), supplyDetails, demandDetails);
        }).collect(Collectors.toList());
    }
}
