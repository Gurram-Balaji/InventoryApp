package com.App.fullStack.service;

import com.App.fullStack.dto.ThresholdDTO;
import com.App.fullStack.exception.FoundException;
import com.App.fullStack.pojos.AtpThreshold;
import com.App.fullStack.pojos.Item;
import com.App.fullStack.pojos.Location;
import com.App.fullStack.repositories.AtpThresholdRepository;
import com.App.fullStack.utility.ItemAndLocationIDChecker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AtpThresholdServiceTest {

    @Mock
    private AtpThresholdRepository atpThresholdRepository;

    @Mock
    private ItemAndLocationIDChecker itemAndLocationIDChecker;

    @Mock
    private ItemService itemService;

    @Mock
    private LocationService locationService;

    @InjectMocks
    private AtpThresholdService atpThresholdService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getAllAtpThresholds_ShouldReturnPageOfThresholds_WhenThresholdsExist() {
        List<AtpThreshold> thresholdList = new ArrayList<>();
        thresholdList.add(new AtpThreshold());
        PageRequest pageRequest = PageRequest.of(0, 10);
        Page<AtpThreshold> thresholdPage = new PageImpl<>(thresholdList, pageRequest, thresholdList.size());

        when(atpThresholdRepository.findAll(pageRequest)).thenReturn(thresholdPage);

        Page<AtpThreshold> result = atpThresholdService.getAllAtpThresholds(0, 10);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(atpThresholdRepository, times(1)).findAll(pageRequest);
    }

    @Test
    void getAllAtpThresholds_ShouldThrowException_WhenNoThresholdsExist() {
        PageRequest pageRequest = PageRequest.of(0, 10);
        Page<AtpThreshold> emptyPage = Page.empty(pageRequest);

        when(atpThresholdRepository.findAll(pageRequest)).thenReturn(emptyPage);

        assertThrows(FoundException.class, () -> atpThresholdService.getAllAtpThresholds(0, 10));
    }

    @Test
    void getAtpThresholdById_ShouldReturnThreshold_WhenThresholdExists() {
        String thresholdId = "testId";
        AtpThreshold threshold = new AtpThreshold();
        threshold.setThresholdId(thresholdId);

        when(atpThresholdRepository.findByThresholdId(thresholdId)).thenReturn(Optional.of(threshold));

        AtpThreshold result = atpThresholdService.getAtpThresholdById(thresholdId);

        assertNotNull(result);
        assertEquals(thresholdId, result.getThresholdId());
        verify(atpThresholdRepository, times(1)).findByThresholdId(thresholdId);
    }

    @Test
    void getAtpThresholdById_ShouldThrowException_WhenThresholdDoesNotExist() {
        String thresholdId = "testId";

        when(atpThresholdRepository.findByThresholdId(thresholdId)).thenReturn(Optional.empty());

        assertThrows(FoundException.class, () -> atpThresholdService.getAtpThresholdById(thresholdId));
    }

    @Test
    void addAtpThreshold_ShouldAddThreshold_WhenValidThresholdIsProvided() {
        AtpThreshold threshold = new AtpThreshold("thresholdId", "itemId", "locationId", 10, 20);

        when(atpThresholdRepository.save(threshold)).thenReturn(threshold);

        AtpThreshold result = atpThresholdService.AddAtpThreshold(threshold);

        assertNotNull(result);
        assertEquals(threshold.getThresholdId(), result.getThresholdId());
        verify(atpThresholdRepository, times(1)).save(threshold);
    }

    @Test
    void addAtpThreshold_ShouldThrowException_WhenThresholdAlreadyExists() {
        AtpThreshold threshold = new AtpThreshold("thresholdId", "itemId", "locationId", 10, 20);

        when(atpThresholdRepository.existsByItemIdAndLocationId(threshold.getItemId(), threshold.getLocationId())).thenReturn(true);

        assertThrows(FoundException.class, () -> atpThresholdService.AddAtpThreshold(threshold));
    }

    @Test
    void updateAtpThresholdById_ShouldUpdateThreshold_WhenValidThresholdIsProvided() {
        String thresholdId = "testId";
        AtpThreshold existingThreshold = new AtpThreshold();
        AtpThreshold updatedThreshold = new AtpThreshold("testId", "itemId", "locationId", 5, 15);

        when(atpThresholdRepository.findByThresholdId(thresholdId)).thenReturn(Optional.of(existingThreshold));
        when(atpThresholdRepository.save(existingThreshold)).thenReturn(updatedThreshold);

        AtpThreshold result = atpThresholdService.updateAtpThresholdById(thresholdId, updatedThreshold);

        assertNotNull(result);
        assertEquals(updatedThreshold.getMinThreshold(), result.getMinThreshold());
        assertEquals(updatedThreshold.getMaxThreshold(), result.getMaxThreshold());
        verify(atpThresholdRepository, times(1)).save(existingThreshold);
    }

    @Test
    void deleteAtpThresholdById_ShouldDeleteThreshold_WhenThresholdExists() {
        String thresholdId = "testId";
        AtpThreshold threshold = new AtpThreshold();
        when(atpThresholdRepository.findByThresholdId(thresholdId)).thenReturn(Optional.of(threshold));

        String result = atpThresholdService.deleteAtpThresholdById(thresholdId);

        assertEquals("Threshold deleted successfully.", result);
        verify(atpThresholdRepository, times(1)).delete(threshold);
    }

    @Test
    void deleteAtpThresholdById_ShouldThrowException_WhenThresholdDoesNotExist() {
        String thresholdId = "testId";

        when(atpThresholdRepository.findByThresholdId(thresholdId)).thenReturn(Optional.empty());

        assertThrows(FoundException.class, () -> atpThresholdService.deleteAtpThresholdById(thresholdId));
    }

    @Test
    void getAllDemandWithDetails_ShouldReturnPaginatedList_WhenValidSearchIsProvided() {
        List<AtpThreshold> thresholds = new ArrayList<>();
        thresholds.add(new AtpThreshold("thresholdId", "itemId", "locationId", 10, 20));

        when(atpThresholdRepository.findAll()).thenReturn(thresholds);
        when(itemService.getItemByItemIdWithOutException(any())).thenReturn(new Item("id", "itemId", "itemDescription", null, null, null, 0.00, true, true, true));
        when(locationService.getLocationByIdWithoutException(any())).thenReturn(new Location());

        Page<ThresholdDTO> result = atpThresholdService.getAllDemandWithDetails(0, 10, "itemId");

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(atpThresholdRepository, times(1)).findAll();
    }
}
