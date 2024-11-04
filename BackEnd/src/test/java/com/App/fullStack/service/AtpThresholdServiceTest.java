package com.App.fullStack.service;

import com.App.fullStack.dto.ThresholdDTO;
import com.App.fullStack.exception.FoundException;
import com.App.fullStack.pojos.AtpThreshold;
import com.App.fullStack.pojos.Item;
import com.App.fullStack.pojos.Location;
import com.App.fullStack.pojos.LocationType;
import com.App.fullStack.repositories.AtpThresholdRepository;
import com.App.fullStack.repositories.ItemRepository;
import com.App.fullStack.repositories.LocationRepository;
import com.App.fullStack.utility.ItemAndLocationIDChecker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AtpThresholdServiceTest {

    @InjectMocks
    private AtpThresholdService atpThresholdService;

    @Mock
    private AtpThresholdRepository atpThresholdRepository;

    @Mock
    private ItemAndLocationIDChecker itemAndLocationIDChecker;

    @Mock
    private ItemService itemService;

    @Mock
    private LocationService locationService;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private LocationRepository locationRepository;

    private AtpThreshold atpThreshold;

    @BeforeEach
    void setUp() {
        atpThreshold = new AtpThreshold("thresholdId", "itemId", "locationId", 10, 20);
    }

    // Test getAllAtpThresholds method
    @Test
    void getAllAtpThresholds_ShouldReturnThresholds_WhenThresholdsExist() {
        List<AtpThreshold> thresholds = Collections.singletonList(atpThreshold);
        Page<AtpThreshold> thresholdPage = new PageImpl<>(thresholds);

        when(atpThresholdRepository.findAll(any(Pageable.class))).thenReturn(thresholdPage);

        Page<AtpThreshold> result = atpThresholdService.getAllAtpThresholds(0, 10);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(atpThresholdRepository).findAll(any(Pageable.class));
    }

    @Test
    void getAllAtpThresholds_ShouldThrowException_WhenNoThresholdsExist() {
        when(atpThresholdRepository.findAll(any(Pageable.class))).thenReturn(Page.empty());

        Exception exception = assertThrows(FoundException.class, () -> {
            atpThresholdService.getAllAtpThresholds(0, 10);
        });

        assertEquals("No ATP Thresholds found.", exception.getMessage());
    }

    // Test getAtpThresholdById method
    @Test
    void getAtpThresholdById_ShouldReturnThreshold_WhenExists() {
        when(atpThresholdRepository.findByThresholdId("thresholdId")).thenReturn(Optional.of(atpThreshold));

        AtpThreshold result = atpThresholdService.getAtpThresholdById("thresholdId");

        assertNotNull(result);
        assertEquals("thresholdId", result.getThresholdId());
        verify(atpThresholdRepository).findByThresholdId("thresholdId");
    }

    @Test
    void getAtpThresholdById_ShouldThrowException_WhenIdIsNull() {
        Exception exception = assertThrows(FoundException.class, () -> {
            atpThresholdService.getAtpThresholdById(null);
        });

        assertEquals("ATP Threshold with ID null not found.", exception.getMessage());
    }

    @Test
    void getAtpThresholdById_ShouldThrowException_WhenNotFound() {
        when(atpThresholdRepository.findByThresholdId("invalidId")).thenReturn(Optional.empty());

        Exception exception = assertThrows(FoundException.class, () -> {
            atpThresholdService.getAtpThresholdById("invalidId");
        });

        assertEquals("ATP Threshold with ID invalidId not found.", exception.getMessage());
    }

    // Test getAtpThresholdByItemAndLocation method
    @Test
    void getAtpThresholdByItemAndLocation_ShouldReturnThreshold_WhenExists() {
        when(atpThresholdRepository.findByItemIdAndLocationId("itemId", "locationId")).thenReturn(Optional.of(atpThreshold));

        AtpThreshold result = atpThresholdService.getAtpThresholdByItemAndLocation("itemId", "locationId");

        assertNotNull(result);
        assertEquals("thresholdId", result.getThresholdId());
        verify(atpThresholdRepository).findByItemIdAndLocationId("itemId", "locationId");
    }

    @Test
    void getAtpThresholdByItemAndLocation_ShouldThrowException_WhenItemIdOrLocationIdIsNull() {
        Exception exception = assertThrows(FoundException.class, () -> {
            atpThresholdService.getAtpThresholdByItemAndLocation(null, null);
        });

        assertEquals("ATP Threshold with Item ID null and Location ID null not found.", exception.getMessage());
    }

    @Test
    void getAtpThresholdByItemAndLocation_ShouldThrowException_WhenNotFound() {
        when(atpThresholdRepository.findByItemIdAndLocationId("itemId", "locationId")).thenReturn(Optional.empty());

        Exception exception = assertThrows(FoundException.class, () -> {
            atpThresholdService.getAtpThresholdByItemAndLocation("itemId", "locationId");
        });

        assertEquals("ATP Threshold with Item ID itemId and Location ID locationId not found.", exception.getMessage());
    }

    @Test
    void addAtpThreshold_ShouldThrowException_WhenThresholdIsNull() {
        Exception exception = assertThrows(FoundException.class, () -> {
            atpThresholdService.AddAtpThreshold(null);
        });

        assertEquals("Cannot add null ATP Threshold.", exception.getMessage());
    }

    @Test
    void addAtpThreshold_ShouldThrowException_WhenThresholdIsEmpty() {
        AtpThreshold emptyThreshold = new AtpThreshold(null, null, null, 0, 0);

        Exception exception = assertThrows(FoundException.class, () -> {
            atpThresholdService.AddAtpThreshold(emptyThreshold);
        });

        assertEquals("Cannot add empty ATP Threshold.", exception.getMessage());
    }

    @Test
    void addAtpThreshold_ShouldThrowException_WhenMinGreaterThanMax() {
        AtpThreshold invalidThreshold = new AtpThreshold("thresholdId", "itemId", "locationId", 20, 10);

        Exception exception = assertThrows(FoundException.class, () -> {
            atpThresholdService.AddAtpThreshold(invalidThreshold);
        });

        assertEquals("ATP Threshold Min is greater then Max Threshold.", exception.getMessage());
    }

    @Test
    void addAtpThreshold_ShouldThrowException_WhenThresholdAlreadyExists() {
        when(atpThresholdRepository.existsByItemIdAndLocationId("itemId", "locationId")).thenReturn(true);

        Exception exception = assertThrows(FoundException.class, () -> {
            atpThresholdService.AddAtpThreshold(atpThreshold);
        });

        assertEquals("ATP Threshold for Item ID itemId and Location ID locationId already exists.", exception.getMessage());
    }

    // Test updateAtpThresholdById method
    @Test
    void updateAtpThresholdById_ShouldUpdateThreshold_WhenValid() {
        when(atpThresholdRepository.findByThresholdId("thresholdId")).thenReturn(Optional.of(atpThreshold));
        AtpThreshold updateDetails = new AtpThreshold("thresholdId", "itemId", "locationId", 15, 25);
        when(atpThresholdRepository.save(any(AtpThreshold.class))).thenReturn(updateDetails);

        AtpThreshold result = atpThresholdService.updateAtpThresholdById("thresholdId", updateDetails);

        assertNotNull(result);
        assertEquals(15, result.getMinThreshold());
        assertEquals(25, result.getMaxThreshold());
        verify(atpThresholdRepository).save(any(AtpThreshold.class));
    }

    @Test
    void updateAtpThresholdById_ShouldThrowException_WhenThresholdIdIsNull() {
        Exception exception = assertThrows(FoundException.class, () -> {
            atpThresholdService.updateAtpThresholdById(null, atpThreshold);
        });

        assertEquals("Cannot update with null details.", exception.getMessage());
    }

    @Test
    void updateAtpThresholdById_ShouldThrowException_WhenUpdateDetailsIsNull() {
        Exception exception = assertThrows(FoundException.class, () -> {
            atpThresholdService.updateAtpThresholdById("thresholdId", null);
        });

        assertEquals("Cannot update with null details.", exception.getMessage());
    }

    // Test deleteAtpThresholdById method
    @Test
    void deleteAtpThresholdById_ShouldDeleteThreshold_WhenExists() {
        when(atpThresholdRepository.findByThresholdId("thresholdId")).thenReturn(Optional.of(atpThreshold));

        String result = atpThresholdService.deleteAtpThresholdById("thresholdId");

        assertEquals("Threshold deleted successfully.", result);
        verify(atpThresholdRepository).delete(atpThreshold);
    }

    @Test
    void deleteAtpThresholdById_ShouldThrowException_WhenNotFound() {
        when(atpThresholdRepository.findByThresholdId("invalidId")).thenReturn(Optional.empty());

        Exception exception = assertThrows(FoundException.class, () -> {
            atpThresholdService.deleteAtpThresholdById("invalidId");
        });

        assertEquals("Demand with demandId invalidId not found.", exception.getMessage());
    }

    @Test
    void getAllDemandWithDetails_ShouldThrowException_WhenNoThresholdsFoundByItem() {
        when(itemRepository.searchItemIdsByKeywordGetIds("itemId")).thenReturn(Collections.emptyList());

        Exception exception = assertThrows(FoundException.class, () -> {
            atpThresholdService.getAllDemandWithDetails(0, 10, "itemId", "item");
        });

        assertEquals("Thresholds not found.", exception.getMessage());
    }

    @Test
    void getAllDemandWithDetails_ShouldThrowException_WhenNoThresholdsFoundByLocation() {
        when(locationRepository.searchLocationIdsByKeywordGetIds("locationId")).thenReturn(Collections.emptyList());

        Exception exception = assertThrows(FoundException.class, () -> {
            atpThresholdService.getAllDemandWithDetails(0, 10, "locationId", "location");
        });

        assertEquals("Thresholds not found.", exception.getMessage());
    }

    @Test
    void getAllDemandWithDetails_ShouldThrowException_WhenNoThresholdsFound() {
        when(atpThresholdRepository.findAll(any(Pageable.class))).thenReturn(Page.empty());

        Exception exception = assertThrows(FoundException.class, () -> {
            atpThresholdService.getAllDemandWithDetails(0, 10, null, null);
        });

        assertEquals("Threshold not found.", exception.getMessage());
    }

    // Test AddThresholdDetails method
    @Test
    void addThresholdDetails_ShouldReturnThresholdDTOs_WhenThresholdsExist() {
        List<AtpThreshold> thresholds = Collections.singletonList(atpThreshold);
        when(itemService.getItemByItemIdWithOutException("itemId")).thenReturn(new Item("itemId", "Item1","Item name","Item cat", null, null, 0.0, true, true, true));
        when(locationService.getLocationByIdWithoutException("locationId")).thenReturn(new Location("www", "loc1", "Location 1", LocationType.DISTRIBUTION_CENTER, true, false, true, "Address1", null, null, "City1", "State1", "Country1", "12345"));

        List<ThresholdDTO> result = atpThresholdService.AddThresholdDetails(thresholds);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Item name", result.getFirst().getItemDescription());
        assertEquals("Location 1", result.getFirst().getLocationDescription());
    }

    @Test
    void addThresholdDetails_ShouldReturnEmptyList_WhenNoThresholds() {
        List<ThresholdDTO> result = atpThresholdService.AddThresholdDetails(Collections.emptyList());

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void getAllDemandWithDetails_ShouldReturnThresholdDTOs_WhenSearchByItem() {
        List<AtpThreshold> thresholds = Collections.singletonList(atpThreshold);
        Page<AtpThreshold> thresholdPage = new PageImpl<>(thresholds);
        when(itemRepository.searchItemIdsByKeywordGetIds("itemId")).thenReturn(Collections.singletonList(new Item("itemId", "Item1","Item name","Item cat", null, null, 0.0, true, true, true)));
        when(atpThresholdRepository.findByItemIdIn(anyList(), any(Pageable.class))).thenReturn(thresholdPage);

        Page<ThresholdDTO> result = atpThresholdService.getAllDemandWithDetails(0, 10, "itemId", "item");

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(atpThresholdRepository).findByItemIdIn(anyList(), any(Pageable.class));
    }

    @Test
    void getAllDemandWithDetails_ShouldReturnThresholdDTOs_WhenSearchByLocation() {
        List<AtpThreshold> thresholds = Collections.singletonList(atpThreshold);
        Page<AtpThreshold> thresholdPage = new PageImpl<>(thresholds);
        when(locationRepository.searchLocationIdsByKeywordGetIds("locationId")).thenReturn(Collections.singletonList(new Location("www", "loc1", "Location 1", LocationType.DISTRIBUTION_CENTER, true, false, true, "Address1", null, null, "City1", "State1", "Country1", "12345")));
        when(atpThresholdRepository.findByLocationIdIn(anyList(), any(Pageable.class))).thenReturn(thresholdPage);

        Page<ThresholdDTO> result = atpThresholdService.getAllDemandWithDetails(0, 10, "locationId", "location");

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(atpThresholdRepository).findByLocationIdIn(anyList(), any(Pageable.class));
    }
}
