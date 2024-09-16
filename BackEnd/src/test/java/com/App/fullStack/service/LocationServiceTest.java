package com.App.fullStack.service;

import com.App.fullStack.exception.FoundException;
import com.App.fullStack.pojos.Location;
import com.App.fullStack.pojos.LocationType;
import com.App.fullStack.repositories.DemandRepository;
import com.App.fullStack.repositories.LocationRepository;
import com.App.fullStack.repositories.SupplyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

public class LocationServiceTest {

    @Mock
    private LocationRepository locationRepository;

    @Mock
    private SupplyRepository supplyRepository;

    @Mock
    private DemandRepository demandRepository;

    @InjectMocks
    private LocationService locationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }


    @Test
    public void testGetAllLocationsWithoutKeyword() {
        List<Location> locations = Arrays.asList(
                new Location("dedaa","loc1", "Location 1", LocationType.DISTRIBUTION_CENTER, true, false, true, "Address1", null, null, "City1", "State1", "Country1", "12345"),
                new Location("dedaa","loc2", "Location 2", LocationType.SUPPLIER_LOCATION, false, true, false, "Address2", null, null, "City2", "State2", "Country2", "67890")
        );
        Pageable pageable = PageRequest.of(0, 10);
        Page<Location> locationPage = new PageImpl<>(locations, pageable, locations.size());

        when(locationRepository.findAll(pageable)).thenReturn(locationPage);

        Page<Location> result = locationService.getAllLocations(0, 10, null);
        assertEquals(2, result.getTotalElements());
    }

    @Test
    public void testGetLocationById() {
        String locationId = "loc1";
        Location location = new Location("www",locationId, "Location 1", LocationType.DISTRIBUTION_CENTER, true, false, true, "Address1", null, null, "City1", "State1", "Country1", "12345");

        when(locationRepository.findByLocationId(locationId)).thenReturn(Optional.of(location));

        Location result = locationService.getLocationById(locationId);
        assertNotNull(result);
        assertEquals(locationId, result.getLocationId());
    }

    @Test
    public void testGetLocationByIdNotFound() {
        String locationId = "loc1";

        when(locationRepository.findByLocationId(locationId)).thenReturn(Optional.empty());

        Exception exception = assertThrows(FoundException.class, () -> {
            locationService.getLocationById(locationId);
        });
        assertEquals("Location with locationId " + locationId + " not exist.", exception.getMessage());
    }

    @Test
    public void testAddLocation() {
        Location location = new Location("dddd","loc1", "Location 1", LocationType.DISTRIBUTION_CENTER, true, false, true, "Address1", null, null, "City1", "State1", "Country1", "12345");

        when(locationRepository.findByLocationId(location.getLocationId())).thenReturn(Optional.empty());
        when(locationRepository.save(location)).thenReturn(location);

        Location result = locationService.addLocation(location);
        assertNotNull(result);
        assertEquals(location.getLocationId(), result.getLocationId());
    }

    @Test
    public void testAddLocationAlreadyExists() {
        Location location = new Location("ddd","loc1", "Location 1", LocationType.DISTRIBUTION_CENTER, true, false, true, "Address1", null, null, "City1", "State1", "Country1", "12345");

        when(locationRepository.findByLocationId(location.getLocationId())).thenReturn(Optional.of(location));

        Exception exception = assertThrows(FoundException.class, () -> {
            locationService.addLocation(location);
        });
        assertEquals("Location with locationId " + location.getLocationId() + " already exists.", exception.getMessage());
    }

    @Test
    public void testUpdateLocation() {
        String locationId = "loc1";
        Location existingLocation = new Location("35rqewawe",locationId, "Location 1", LocationType.DISTRIBUTION_CENTER, true, false, true, "Address1", null, null, "City1", "State1", "Country1", "12345");
        Location locationDetails = new Location("35rqewawe",locationId, "Updated Location", LocationType.SUPPLIER_LOCATION, false, true, false, "Updated Address", null, null, "Updated City", "Updated State", "Updated Country", "54321");

        when(locationRepository.findByLocationId(locationId)).thenReturn(Optional.of(existingLocation));
        when(locationRepository.save(existingLocation)).thenReturn(existingLocation);

        Location result = locationService.updateLocation(locationId, locationDetails);
        assertNotNull(result);
        assertEquals("Updated Location", result.getLocationDesc());
        assertEquals(LocationType.SUPPLIER_LOCATION, result.getLocationType());
        assertEquals("Updated Address", result.getAddressLine1());
        assertEquals("Updated City", result.getCity());
        assertEquals("Updated State", result.getState());
        assertEquals("Updated Country", result.getCountry());
        assertEquals("54321", result.getPinCode());
        assertFalse(result.isPickupAllowed());
        assertTrue(result.isShippingAllowed());
        assertFalse(result.isDeliveryAllowed());
    }

    @Test
    public void testUpdateLocationNotFound() {
        String locationId = "loc1";
        Location locationDetails = new Location("35rqewawe",locationId, "Updated Location", LocationType.DISTRIBUTION_CENTER, false, true, false, "Updated Address", null, null, "Updated City", "Updated State", "Updated Country", "54321");

        when(locationRepository.findByLocationId(locationId)).thenReturn(Optional.empty());

        Exception exception = assertThrows(FoundException.class, () -> {
            locationService.updateLocation(locationId, locationDetails);
        });
        assertEquals("Location with locationId " + locationId + " not exist.", exception.getMessage());
    }

    @Test
    public void testDeleteLocation() {
        String locationId = "loc1";
        Location location = new Location("dddff",locationId, "Location 1", LocationType.DISTRIBUTION_CENTER, true, false, true, "Address1", null, null, "City1", "State1", "Country1", "12345");

        when(supplyRepository.existsByLocationId(locationId)).thenReturn(false);
        when(demandRepository.existsByLocationId(locationId)).thenReturn(false);
        when(locationRepository.findByLocationId(locationId)).thenReturn(Optional.of(location));

        String result = locationService.deleteLocation(locationId);
        assertEquals("Location deleted successfully", result);
    }

    @Test
    public void testDeleteLocationWithAssociatedSupply() {
        String locationId = "loc1";

        when(supplyRepository.existsByLocationId(locationId)).thenReturn(true);
        when(demandRepository.existsByLocationId(locationId)).thenReturn(false);

        Exception exception = assertThrows(FoundException.class, () -> {
            locationService.deleteLocation(locationId);
        });
        assertEquals("Item cannot be deleted because it has associated supply records.", exception.getMessage());
    }

    @Test
    public void testDeleteLocationWithAssociatedDemand() {
        String locationId = "loc1";

        when(supplyRepository.existsByLocationId(locationId)).thenReturn(false);
        when(demandRepository.existsByLocationId(locationId)).thenReturn(true);

        Exception exception = assertThrows(FoundException.class, () -> {
            locationService.deleteLocation(locationId);
        });
        assertEquals("Item cannot be deleted because it has associated demand records.", exception.getMessage());
    }

    @Test
    public void testDeleteLocationWithAssociatedSupplyAndDemand() {
        String locationId = "loc1";

        when(supplyRepository.existsByLocationId(locationId)).thenReturn(true);
        when(demandRepository.existsByLocationId(locationId)).thenReturn(true);

        Exception exception = assertThrows(FoundException.class, () -> {
            locationService.deleteLocation(locationId);
        });
        assertEquals("Item cannot be deleted because it has associated supply or demand records.", exception.getMessage());
    }

    @Test
    public void testGetLocationByIdWithoutException() {
        String locationId = "loc1";
        Location location = new Location("dffl", locationId, "Location 1", LocationType.DISTRIBUTION_CENTER, true, false, true, "Address1", null, null, "City1", "State1", "Country1", "12345");

        when(locationRepository.findByLocationId(locationId)).thenReturn(Optional.of(location));

        Location result = locationService.getLocationByIdWithoutException(locationId);
        assertNotNull(result);
        assertEquals(locationId, result.getLocationId());
    }

    @Test
    public void testGetLocationByIdWithoutExceptionNotFound() {
        String locationId = "loc1";

        when(locationRepository.findByLocationId(locationId)).thenReturn(Optional.empty());

        Location result = locationService.getLocationByIdWithoutException(locationId);
        assertNull(result);
    }

}
