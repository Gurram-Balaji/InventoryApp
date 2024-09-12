package com.App.fullStack.service;

import com.App.fullStack.dto.AvailabilityConfig;
import com.App.fullStack.dto.AvailabilityResponseV2V3;
import com.App.fullStack.exception.FoundException;
import com.App.fullStack.pojos.AtpThreshold;
import com.App.fullStack.pojos.Demand;
import com.App.fullStack.pojos.Supply;
import com.App.fullStack.repositories.AtpThresholdRepository;
import com.App.fullStack.repositories.DemandRepository;
import com.App.fullStack.repositories.SupplyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.util.Collections;
import java.util.Optional;

import static com.App.fullStack.pojos.DemandType.HARD_PROMISED;
import static com.App.fullStack.pojos.SupplyType.ONHAND;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AvailabilityServiceTest {

    @Mock
    private SupplyRepository supplyRepository;

    @Mock
    private AtpThresholdRepository atpThresholdRepository;

    @Mock
    private DemandRepository demandRepository;

    @Mock
    private AvailabilityConfig availabilityConfig;

    @InjectMocks
    private AvailabilityService availabilityService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCalculateAvailabilityByLocation() {
        // Setup
        String itemId = "item1";
        String locationId = "loc1";

        Supply supply = new Supply("supply1", itemId, locationId, ONHAND, 100);
        Demand demand = new Demand("demand1", HARD_PROMISED, 50, itemId, locationId);

        when(supplyRepository.findByItemIdAndLocationIdAndSupplyType(itemId, locationId, "ONHAND"))
                .thenReturn(Collections.singletonList(supply));
        when(demandRepository.findByItemIdAndLocationIdAndDemandType(itemId, locationId, "HARD_PROMISED"))
                .thenReturn(Collections.singletonList(demand));

        // Test
        int result = availabilityService.calculateAvailabilityByLocation(itemId, locationId);

        // Verify
        assertEquals(50, result);
    }

    @Test
    void testCalculateAvailabilityByItem() {
        // Setup
        String itemId = "item1";

        Supply supply = new Supply("supply1", itemId, "loc1", ONHAND, 100);
        Demand demand = new Demand("demand1", HARD_PROMISED, 50, itemId, "loc1");

        when(supplyRepository.findByItemIdAndSupplyType(itemId, "ONHAND")).thenReturn(Collections.singletonList(supply));
        when(demandRepository.findByItemIdAndDemandType(itemId, "HARD_PROMISED")).thenReturn(Collections.singletonList(demand));

        // Test
        int result = availabilityService.calculateAvailabilityByItem(itemId);

        // Verify
        assertEquals(50, result);
    }

    @Test
    void testCalculateV2AvailabilityByLocation() {
        // Setup
        String itemId = "item1";
        String locationId = "loc1";

        Supply supply = new Supply("supply1", itemId, locationId, ONHAND, 100);
        Demand demand = new Demand("demand1", HARD_PROMISED, 50, itemId, locationId);
        AtpThreshold threshold = new AtpThreshold("threshold1", itemId, locationId, 30, 70);

        when(supplyRepository.findByItemIdAndLocationIdAndSupplyType(itemId, locationId, "ONHAND"))
                .thenReturn(Collections.singletonList(supply));
        when(demandRepository.findByItemIdAndLocationIdAndDemandType(itemId, locationId, "HARD_PROMISED"))
                .thenReturn(Collections.singletonList(demand));
        when(atpThresholdRepository.findByItemIdAndLocationId(itemId, locationId))
                .thenReturn(Optional.of(threshold));

        // Test
        AvailabilityResponseV2V3 result = availabilityService.calculateV2AvailabilityByLocation(itemId, locationId);

        // Verify
        assertNotNull(result);
        assertEquals(itemId, result.getItemId());
        assertEquals(locationId, result.getLocationId());
        assertEquals(50, result.getAvailableQty());
        assertEquals("Yellow", result.getStockLevel());
    }

    @Test
    void testCalculateV3AvailabilityByLocation() {
        // Setup
        String itemId = "item1";
        String locationId = "loc1";

        Supply supply = new Supply("supply1", itemId, locationId, ONHAND, 100);
        Demand demand = new Demand("demand1", HARD_PROMISED, 50, itemId, locationId);
        AtpThreshold threshold = new AtpThreshold("threshold1", itemId, locationId, 30, 70);

        when(supplyRepository.findByItemIdAndLocationIdAndSupplyTypeIn(eq(itemId), eq(locationId), any()))
                .thenReturn(Collections.singletonList(supply));
        when(demandRepository.findByItemIdAndLocationIdAndDemandTypeIn(eq(itemId), eq(locationId), any()))
                .thenReturn(Collections.singletonList(demand));
        when(atpThresholdRepository.findByItemIdAndLocationId(itemId, locationId))
                .thenReturn(Optional.of(threshold));
        when(availabilityConfig.getSupplies()).thenReturn(new String[]{"ONHAND"});
        when(availabilityConfig.getDemands()).thenReturn(new String[]{"HARD_PROMISED"});
        when(availabilityConfig.getExcludedLocations()).thenReturn(new String[]{});

        // Test
        AvailabilityResponseV2V3 result = availabilityService.calculateV3AvailabilityByLocation(itemId, locationId);

        // Verify
        assertNotNull(result);
        assertEquals(itemId, result.getItemId());
        assertEquals(locationId, result.getLocationId());
        assertEquals(50, result.getAvailableQty());
        assertEquals("Yellow", result.getStockLevel());
    }

    @Test
    void testCalculateV3AvailabilityByLocationWithExcludedLocation() {
        // Setup
        String itemId = "item1";
        String locationId = "excludedLoc";

        when(availabilityConfig.getExcludedLocations()).thenReturn(new String[]{locationId});

        // Test and Verify
        FoundException thrown = assertThrows(FoundException.class, () ->
                availabilityService.calculateV3AvailabilityByLocation(itemId, locationId));

        assertEquals("LocationId excludedLoc is excluded from availability checks.", thrown.getMessage());
    }
}
