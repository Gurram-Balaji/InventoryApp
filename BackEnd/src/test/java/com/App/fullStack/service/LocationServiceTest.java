package com.App.fullStack.service;

import com.App.fullStack.dto.LocationData;
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
import static org.mockito.Mockito.*;

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
                new Location("www", "loc1", "Location 1", LocationType.DISTRIBUTION_CENTER, true, false, true, "Address1", null, null, "City1", "State1", "Country1", "12345"),
                new Location("www", "loc2", "Location 2", LocationType.SUPPLIER_LOCATION, false, true, false, "Address2", null, null, "City2", "State2", "Country2", "67890")
        );
        Pageable pageable = PageRequest.of(0, 10);
        Page<Location> locationPage = new PageImpl<>(locations, pageable, locations.size());

        when(locationRepository.findAll(pageable)).thenReturn(locationPage);

        Page<Location> result = locationService.getAllLocations(0, 10, null);
        assertEquals(2, result.getTotalElements());
        assertEquals(locations, result.getContent());
    }

    @Test
    public void testGetAllLocationsWithKeyword() {
        List<Location> locations = List.of(
                new Location("www", "loc1", "Location 1", LocationType.DISTRIBUTION_CENTER, true, false, true, "Address1", null, null, "City1", "State1", "Country1", "12345")
        );
        Pageable pageable = PageRequest.of(0, 10);
        Page<Location> locationPage = new PageImpl<>(locations, pageable, locations.size());

        when(locationRepository.searchLocationsByKeyword("Location", pageable)).thenReturn(locationPage);

        Page<Location> result = locationService.getAllLocations(0, 10, "Location");
        assertEquals(1, result.getTotalElements());
        assertEquals(locations, result.getContent());
    }

    @Test
    public void testGetLocationByIdFound() {
        String locationId = "loc1";
        Location location = new Location("www", locationId, "Location 1", LocationType.DISTRIBUTION_CENTER, true, false, true, "Address1", null, null, "City1", "State1", "Country1", "12345");

        when(locationRepository.findByLocationId(locationId)).thenReturn(Optional.of(location));

        Location result = locationService.getLocationById(locationId);
        assertNotNull(result);
        assertEquals(locationId, result.getLocationId());
        assertEquals("Location 1", result.getLocationDesc());
    }

    @Test
    public void testGetLocationByIdNotFound() {
        String locationId = "loc1";

        when(locationRepository.findByLocationId(locationId)).thenReturn(Optional.empty());

        FoundException exception = assertThrows(FoundException.class, () -> locationService.getLocationById(locationId));
        assertEquals("Location with locationId " + locationId + " not exist.", exception.getMessage());
    }

    @Test
    public void testAddLocationSuccessfully() {
        Location location = new Location("www", "loc1", "Location 1", LocationType.DISTRIBUTION_CENTER, true, false, true, "Address1", null, null, "City1", "State1", "Country1", "12345");

        when(locationRepository.findByLocationId(location.getLocationId())).thenReturn(Optional.empty());
        when(locationRepository.save(location)).thenReturn(location);

        Location result = locationService.addLocation(location);
        assertNotNull(result);
        assertEquals(location.getLocationId(), result.getLocationId());
    }

    @Test
    public void testAddLocationAlreadyExists() {
        Location location = new Location("www", "loc1", "Location 1", LocationType.DISTRIBUTION_CENTER, true, false, true, "Address1", null, null, "City1", "State1", "Country1", "12345");

        when(locationRepository.findByLocationId(location.getLocationId())).thenReturn(Optional.of(location));

        FoundException exception = assertThrows(FoundException.class, () -> locationService.addLocation(location));
        assertEquals("Location with locationId " + location.getLocationId() + " already exists.", exception.getMessage());
    }

    @Test
    public void testUpdateLocationSuccessfully() {
        String locationId = "loc1";
        Location existingLocation = new Location("www", locationId, "Location 1", LocationType.DISTRIBUTION_CENTER, true, false, true, "Address1", null, null, "City1", "State1", "Country1", "12345");
        Location locationDetails = new Location("www", locationId, "Updated Location", LocationType.SUPPLIER_LOCATION, false, true, false, "Updated Address", null, null, "Updated City", "Updated State", "Updated Country", "54321");

        when(locationRepository.findByLocationId(locationId)).thenReturn(Optional.of(existingLocation));
        when(locationRepository.save(existingLocation)).thenReturn(existingLocation);

        Location result = locationService.updateLocation(locationId, locationDetails);
        assertNotNull(result);
        assertEquals("Updated Location", result.getLocationDesc());
        assertEquals(LocationType.SUPPLIER_LOCATION, result.getLocationType());
        assertEquals("Updated Address", result.getAddressLine1());
    }

    @Test
    public void testUpdateLocationNotFound() {
        String locationId = "loc1";
        Location locationDetails = new Location("www", locationId, "Updated Location", LocationType.DISTRIBUTION_CENTER, false, true, false, "Updated Address", null, null, "Updated City", "Updated State", "Updated Country", "54321");

        when(locationRepository.findByLocationId(locationId)).thenReturn(Optional.empty());

        FoundException exception = assertThrows(FoundException.class, () -> locationService.updateLocation(locationId, locationDetails));
        assertEquals("Location with locationId " + locationId + " not exist.", exception.getMessage());
    }

    @Test
    public void testDeleteLocationSuccessfully() {
        String locationId = "loc1";
        Location location = new Location("www", locationId, "Location 1", LocationType.DISTRIBUTION_CENTER, true, false, true, "Address1", null, null, "City1", "State1", "Country1", "12345");

        when(locationRepository.findByLocationId(locationId)).thenReturn(Optional.of(location));
        when(supplyRepository.existsByLocationId(locationId)).thenReturn(false);
        when(demandRepository.existsByLocationId(locationId)).thenReturn(false);

        String result = locationService.deleteLocation(locationId);
        assertEquals("Location deleted successfully", result);
        verify(locationRepository, times(1)).delete(location);
    }

    @Test
    public void testDeleteLocationWithAssociatedSupply() {
        String locationId = "loc1";
        Location location = new Location("www", locationId, "Location 1", LocationType.DISTRIBUTION_CENTER, true, false, true, "Address1", null, null, "City1", "State1", "Country1", "12345");

        when(locationRepository.findByLocationId(locationId)).thenReturn(Optional.of(location));
        when(supplyRepository.existsByLocationId(locationId)).thenReturn(true);
        when(demandRepository.existsByLocationId(locationId)).thenReturn(false);

        FoundException exception = assertThrows(FoundException.class, () -> locationService.deleteLocation(locationId));
        assertEquals("Item cannot be deleted because it has associated supply records.", exception.getMessage());
    }

    @Test
    public void testGetAllLocationIdsWithoutKeyword() {
        List<String> locationIds = Arrays.asList("loc1", "loc2");
        when(locationRepository.findDistinctLocationIds(PageRequest.of(0, 10))).thenReturn(new PageImpl<>(locationIds));

        Page<String> result = locationService.getAllLocationIds(0, 10, null);
        assertEquals(2, result.getTotalElements());
        assertEquals(locationIds, result.getContent());
    }

    @Test
    public void testGetStackedBarDataWithLocations() {
        List<Location> locations = Arrays.asList(
                new Location("www", "loc1", "Location 1", LocationType.DISTRIBUTION_CENTER, true, false, true, "Address1", null, null, "City1", "State1", "Country1", "12345"),
                new Location("www", "loc2", "Location 2", LocationType.SUPPLIER_LOCATION, false, true, false, "Address2", null, null, "City2", "State2", "Country2", "67890")
        );

        when(locationRepository.findAll()).thenReturn(locations);
        when(supplyRepository.getTotalSupplyByLocationAndType("loc1", "ONHAND")).thenReturn(10);
        when(supplyRepository.getTotalSupplyByLocationAndType("loc1", "INTRANSIT")).thenReturn(5);
        when(demandRepository.getTotalDemandByLocationAndType("loc1", "HARD_PROMISED")).thenReturn(3);
        when(demandRepository.getTotalDemandByLocationAndType("loc1", "PLANNED")).thenReturn(2);

        when(supplyRepository.getTotalSupplyByLocationAndType("loc2", "ONHAND")).thenReturn(20);
        when(supplyRepository.getTotalSupplyByLocationAndType("loc2", "INTRANSIT")).thenReturn(15);
        when(demandRepository.getTotalDemandByLocationAndType("loc2", "HARD_PROMISED")).thenReturn(6);
        when(demandRepository.getTotalDemandByLocationAndType("loc2", "PLANNED")).thenReturn(4);

        List<LocationData> result = locationService.getStackedBarData();
        assertNotNull(result);
        assertEquals(2, result.size());

        LocationData locationData1 = result.getFirst();
        assertEquals("loc1", locationData1.getLocationId());
        assertEquals("Location 1", locationData1.getLocationDesc());
        assertEquals(10, locationData1.getSupplyDetails().get("ONHAND"));
        assertEquals(5, locationData1.getSupplyDetails().get("INTRANSIT"));
        assertEquals(3, locationData1.getDemandDetails().get("HARD_PROMISED"));
        assertEquals(2, locationData1.getDemandDetails().get("PLANNED"));

        LocationData locationData2 = result.get(1);
        assertEquals("loc2", locationData2.getLocationId());
        assertEquals("Location 2", locationData2.getLocationDesc());
        assertEquals(20, locationData2.getSupplyDetails().get("ONHAND"));
        assertEquals(15, locationData2.getSupplyDetails().get("INTRANSIT"));
        assertEquals(6, locationData2.getDemandDetails().get("HARD_PROMISED"));
        assertEquals(4, locationData2.getDemandDetails().get("PLANNED"));
    }

    @Test
    public void testGetLocationByIdWithoutExceptionLocationFound() {
        String locationId = "loc1";
        Location location = new Location("www", locationId, "Location 1", LocationType.DISTRIBUTION_CENTER, true, false, true, "Address1", null, null, "City1", "State1", "Country1", "12345");

        when(locationRepository.findByLocationId(locationId)).thenReturn(Optional.of(location));

        Location result = locationService.getLocationByIdWithoutException(locationId);
        assertNotNull(result);
        assertEquals(locationId, result.getLocationId());
    }

    @Test
    public void testGetLocationByIdWithoutExceptionLocationNotFound() {
        String locationId = "loc1";
        when(locationRepository.findByLocationId(locationId)).thenReturn(Optional.empty());

        Location result = locationService.getLocationByIdWithoutException(locationId);
        assertNull(result);
    }

    @Test
    public void testDeleteLocationWithAssociatedSupplyAndDemand() {
        String locationId = "loc1";
        Location location = new Location("www", locationId, "Location 1", LocationType.DISTRIBUTION_CENTER, true, false, true, "Address1", null, null, "City1", "State1", "Country1", "12345");

        when(locationRepository.findByLocationId(locationId)).thenReturn(Optional.of(location));
        when(supplyRepository.existsByLocationId(locationId)).thenReturn(true);
        when(demandRepository.existsByLocationId(locationId)).thenReturn(true);

        FoundException exception = assertThrows(FoundException.class, () -> locationService.deleteLocation(locationId));
        assertEquals("Item cannot be deleted because it has associated supply or demand records.", exception.getMessage());
    }

    @Test
    public void testUpdateLocationPartially() {
        String locationId = "loc1";
        Location existingLocation = new Location("www", locationId, "Location 1", LocationType.DISTRIBUTION_CENTER, true, false, true, "Address1", null, null, "City1", "State1", "Country1", "12345");
        Location locationDetails = new Location(null, null, "Updated Location", null, false, false, false, null, null, null, null, null, null, null);

        when(locationRepository.findByLocationId(locationId)).thenReturn(Optional.of(existingLocation));
        when(locationRepository.save(existingLocation)).thenReturn(existingLocation);

        Location result = locationService.updateLocation(locationId, locationDetails);
        assertNotNull(result);
        assertEquals("Updated Location", result.getLocationDesc());
        assertEquals(LocationType.DISTRIBUTION_CENTER, result.getLocationType()); // Unchanged field
        assertEquals("Address1", result.getAddressLine1()); // Unchanged field
    }

    @Test
    public void testGetAllLocationIdsWithKeyword() {
        List<String> locationIds = Arrays.asList("loc1", "loc3");
        when(locationRepository.searchLocationIdsByKeyword("loc", PageRequest.of(0, 10))).thenReturn(new PageImpl<>(locationIds));

        Page<String> result = locationService.getAllLocationIds(0, 10, "loc");
        assertEquals(2, result.getTotalElements());
        assertEquals(locationIds, result.getContent());
    }

    @Test
    public void testAddLocationWithInvalidInput_locationId() {
        Location invalidLocation = new Location("12323", null, "Something", LocationType.DISTRIBUTION_CENTER, false, false, false, "Address Something", null, null, null, null, "In country", "PIN 1233");
        FoundException exception = assertThrows(FoundException.class, () -> locationService.addLocation(invalidLocation));
        assertEquals("All required fields should be filled.", exception.getMessage());
    }

    @Test
    public void testAddLocationWithInvalidInput_locationDesc() {
        Location invalidLocation = new Location("12323", "loc Id", null, LocationType.DISTRIBUTION_CENTER, false, false, false, "Address Something", null, null, null, null, "In country", "PIN 1233");
        FoundException exception = assertThrows(FoundException.class, () -> locationService.addLocation(invalidLocation));
        assertEquals("All required fields should be filled.", exception.getMessage());
    }
    @Test
    public void testAddLocationWithInvalidInput_locationType() {
        Location invalidLocation = new Location("12323", "loc Id", "Something", null, false, false, false, "Address Something", null, null, null, null, "In country", "PIN 1233");
        FoundException exception = assertThrows(FoundException.class, () -> locationService.addLocation(invalidLocation));
        assertEquals("All required fields should be filled.", exception.getMessage());
    }
    @Test
    public void testAddLocationWithInvalidInput_locationAddress() {
        Location invalidLocation = new Location("12323", "loc Id", "Something", LocationType.DISTRIBUTION_CENTER, false, false, false, null, null, null, null, null, "In country", "PIN 1233");
        FoundException exception = assertThrows(FoundException.class, () -> locationService.addLocation(invalidLocation));
        assertEquals("All required fields should be filled.", exception.getMessage());
    }

    @Test
    public void testAddLocationWithInvalidInput_locationCountry() {
        Location invalidLocation = new Location("12323", "loc Id", "Something", LocationType.DISTRIBUTION_CENTER, false, false, false, "Address Something", null, null, null, null, null, "PIN 1233");
        FoundException exception = assertThrows(FoundException.class, () -> locationService.addLocation(invalidLocation));
        assertEquals("All required fields should be filled.", exception.getMessage());
    }

    @Test
    public void testAddLocationWithInvalidInput_locationPin() {
        Location invalidLocation = new Location("12323", "loc Id", "Something", LocationType.DISTRIBUTION_CENTER, false, false, false, "Address Something", null, null, null, null, "In country", null);
        FoundException exception = assertThrows(FoundException.class, () -> locationService.addLocation(invalidLocation));
        assertEquals("All required fields should be filled.", exception.getMessage());
    }

    @Test
    public void testGetAllLocationsEmptyList() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Location> emptyPage = new PageImpl<>(List.of());

        when(locationRepository.findAll(pageable)).thenReturn(emptyPage);

        Page<Location> result = locationService.getAllLocations(0, 10, null);
        assertEquals(0, result.getTotalElements());
        assertTrue(result.getContent().isEmpty());
    }


    @Test
    public void testDeleteLocationNotFound() {
        String locationId = "loc1";

        when(locationRepository.findByLocationId(locationId)).thenReturn(Optional.empty());

        FoundException exception = assertThrows(FoundException.class, () -> locationService.deleteLocation(locationId));
        assertEquals("Location with locationId " + locationId + " not exist.", exception.getMessage());
    }

    @Test
    public void testUpdateLocationPartialUpdate() {
        String locationId = "loc1";
        Location existingLocation = new Location("www", locationId, "Location 1", LocationType.DISTRIBUTION_CENTER, true, false, true, "Address1", null, null, "City1", "State1", "Country1", "12345");
        Location locationDetails = new Location(null, locationId, "Updated Desc", null, false, false, false, null, null, null, null, null, null, null);

        when(locationRepository.findByLocationId(locationId)).thenReturn(Optional.of(existingLocation));
        when(locationRepository.save(existingLocation)).thenReturn(existingLocation);

        Location result = locationService.updateLocation(locationId, locationDetails);
        assertNotNull(result);
        assertEquals("Updated Desc", result.getLocationDesc());
        assertEquals(LocationType.DISTRIBUTION_CENTER, result.getLocationType()); // Unchanged
        assertEquals("Address1", result.getAddressLine1()); // Unchanged
    }

    @Test
    public void testGetStackedBarDataNoLocations() {
        when(locationRepository.findAll()).thenReturn(List.of());

        List<LocationData> result = locationService.getStackedBarData();
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    public void testAddLocationNull() {
        assertThrows(NullPointerException.class, () -> locationService.addLocation(null));
    }

    @Test
    public void testAddLocationDuplicateLocationIdCaseInsensitive() {
        Location location = new Location("www", "loc1", "Location 1", LocationType.DISTRIBUTION_CENTER, true, false, true, "Address1", null, null, "City1", "State1", "Country1", "12345");
        Location existingLocation = new Location("www", "LOC1", "Location 1", LocationType.DISTRIBUTION_CENTER, true, false, true, "Address1", null, null, "City1", "State1", "Country1", "12345");

        when(locationRepository.findByLocationId("loc1")).thenReturn(Optional.of(existingLocation));

        FoundException exception = assertThrows(FoundException.class, () -> locationService.addLocation(location));
        assertEquals("Location with locationId loc1 already exists.", exception.getMessage());
    }

    @Test
    public void testDeleteLocationThrowsException() {
        String locationId = "loc1";
        Location location = new Location("www", locationId, "Location 1", LocationType.DISTRIBUTION_CENTER, true, false, true, "Address1", null, null, "City1", "State1", "Country1", "12345");

        when(locationRepository.findByLocationId(locationId)).thenReturn(Optional.of(location));
        when(supplyRepository.existsByLocationId(locationId)).thenReturn(false);
        when(demandRepository.existsByLocationId(locationId)).thenReturn(false);
        doThrow(new RuntimeException("Database error")).when(locationRepository).delete(location);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> locationService.deleteLocation(locationId));
        assertEquals("Database error", exception.getMessage());
    }

    @Test
    public void testGetLocationByIdNull() {
        assertThrows(FoundException.class, () -> locationService.getLocationById(null));
    }


}
