package com.App.fullStack.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.App.fullStack.dto.*;
import com.App.fullStack.pojos.*;
import com.App.fullStack.exception.FoundException;
import com.App.fullStack.repositories.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.*;

public class AvailabilityServiceTest {

    @InjectMocks
    private AvailabilityService availabilityService;

    @Mock
    private SupplyRepository supplyRepository;

    @Mock
    private AtpThresholdRepository atpThresholdRepository;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private DemandRepository demandRepository;

    @Mock
    private AvailabilityConfig availabilityConfig;

    @Mock
    private LocationRepository locationRepository;

    private Supply supply;
    private Demand demand;
    private AtpThreshold atpThreshold;
    private Item item;
    private Location location;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        supply = new Supply("1", "item1", "location1", SupplyType.ONHAND, 100);
        demand = new Demand("1", DemandType.HARD_PROMISED, 50, "item1", "location1");
        atpThreshold = new AtpThreshold("id","item1", "location1", 20, 150);
        item = new Item("id","item1", "Sample Item", "Category1", "Type1", null, 200.0, true, true, true);
        location = new Location("id","location1", "Sample Location", null, true, true, true, "123 Street", null, null, "City", "State", "Country", "123456");
    }

    // Testing V1 Method: calculateAvailabilityByLocation
    @Test
    public void testCalculateAvailabilityByLocation_Success() {
        when(supplyRepository.findByItemIdAndLocationIdAndSupplyType("item1", "location1", "ONHAND"))
                .thenReturn(Collections.singletonList(supply));
        when(demandRepository.findByItemIdAndLocationIdAndDemandType("item1", "location1", "HARD_PROMISED"))
                .thenReturn(Collections.singletonList(demand));

        int result = availabilityService.calculateAvailabilityByLocation("item1", "location1");
        assertEquals(50, result);  // Supply (100) - Demand (50)
    }

    @Test
    public void testCalculateAvailabilityByLocation_NoSupplyOrDemandFound() {
        when(supplyRepository.findByItemIdAndLocationIdAndSupplyType("item1", "location1", "ONHAND"))
                .thenReturn(Collections.emptyList());
        when(demandRepository.findByItemIdAndLocationIdAndDemandType("item1", "location1", "HARD_PROMISED"))
                .thenReturn(Collections.emptyList());

        Exception exception = assertThrows(FoundException.class, () -> availabilityService.calculateAvailabilityByLocation("item1", "location1"));
        assertEquals("Records with ItemId: item1 And LocationId location1 not found.", exception.getMessage());
    }

    // Testing V2 Method: calculateV2AvailabilityByLocation
    @Test
    public void testCalculateV2AvailabilityByLocation_Success() {
        when(supplyRepository.findByItemIdAndLocationIdAndSupplyType("item1", "location1", "ONHAND"))
                .thenReturn(Collections.singletonList(supply));
        when(demandRepository.findByItemIdAndLocationIdAndDemandType("item1", "location1", "HARD_PROMISED"))
                .thenReturn(Collections.singletonList(demand));
        when(atpThresholdRepository.findByItemIdAndLocationId("item1", "location1"))
                .thenReturn(Optional.of(atpThreshold));

        AvailabilityResponseV2V3 result = availabilityService.calculateV2AvailabilityByLocation("item1", "location1");

        assertEquals("item1", result.getItemId());
        assertEquals("location1", result.getLocationId());
        assertEquals(50, result.getAvailableQty());
        assertEquals("Yellow", result.getStockLevel()); // Between min (20) and max (150) thresholds
    }

    // Testing V3 Method: calculateV3AvailabilityByLocation with valid supply/demand types
    @Test
    public void testCalculateV3AvailabilityByLocation_Success() {
        // Mock configuration
        when(availabilityConfig.getSupplies()).thenReturn(new String[]{"ONHAND"});
        when(availabilityConfig.getDemands()).thenReturn(new String[]{"HARD_PROMISED"});
        when(availabilityConfig.getExcludedLocations()).thenReturn(null);

        when(supplyRepository.findByItemIdAndLocationIdAndSupplyTypeIn("item1", "location1", Collections.singletonList("ONHAND")))
                .thenReturn(Collections.singletonList(supply));
        when(demandRepository.findByItemIdAndLocationIdAndDemandTypeIn("item1", "location1", Collections.singletonList("HARD_PROMISED")))
                .thenReturn(Collections.singletonList(demand));
        when(atpThresholdRepository.findByItemIdAndLocationId("item1", "location1"))
                .thenReturn(Optional.of(atpThreshold));

        AvailabilityResponseV2V3 result = availabilityService.calculateV3AvailabilityByLocation("item1", "location1");

        assertEquals("item1", result.getItemId());
        assertEquals("location1", result.getLocationId());
        assertEquals(50, result.getAvailableQty());  // Supply (100) - Demand (50)
        assertEquals("Yellow", result.getStockLevel());  // Between thresholds
    }

    // Testing V3 Method: calculateV3AvailabilityByLocation for excluded location
    @Test
    public void testCalculateV3AvailabilityByLocation_ExcludedLocation() {
        when(availabilityConfig.getSupplies()).thenReturn(new String[]{"ONHAND"});
        when(availabilityConfig.getDemands()).thenReturn(new String[]{"HARD_PROMISED"});
        when(availabilityConfig.getExcludedLocations()).thenReturn(new String[]{"location1"});

        Exception exception = assertThrows(FoundException.class, () -> availabilityService.calculateV3AvailabilityByLocation("item1", "location1"));
        assertEquals("LocationId location1 is excluded from availability checks.", exception.getMessage());
    }

    // Testing getAvailabilityScatterData
    @Test
    public void testGetAvailabilityScatterData_Success() {
        when(itemRepository.findAll()).thenReturn(Collections.singletonList(item));
        when(locationRepository.findByLocationId("location1")).thenReturn(Optional.of(location));
        when(supplyRepository.findByItemIdAndLocationIdAndSupplyType("item1", "location1", "ONHAND"))
                .thenReturn(Collections.singletonList(supply));
        when(demandRepository.findByItemIdAndLocationIdAndDemandType("item1", "location1", "HARD_PROMISED"))
                .thenReturn(Collections.singletonList(demand));

        ScatterLocationDataDTO result = availabilityService.getAvailabilityScatterData("location1");

        assertNotNull(result);
        assertEquals("Sample Location", result.getLocationName());
        assertEquals(1, result.getScatterDataDTO().size());
        ScatterDataDTO scatterData = result.getScatterDataDTO().getFirst();
        assertEquals(200.0, scatterData.getItemPrice());
        assertEquals(100, scatterData.getSupplyQuantity());
        assertEquals(50, scatterData.getDemandQuantity());
    }

    @Test
    public void testGetAvailabilityScatterData_NetworkLocation() {
        when(itemRepository.findAll()).thenReturn(Collections.singletonList(item));
        when(locationRepository.findByLocationId("location1")).thenReturn(Optional.empty());
        when(supplyRepository.findByItemIdAndSupplyType("item1", "ONHAND"))
                .thenReturn(Collections.singletonList(supply));
        when(demandRepository.findByItemIdAndDemandType("item1", "HARD_PROMISED"))
                .thenReturn(Collections.singletonList(demand));

        ScatterLocationDataDTO result = availabilityService.getAvailabilityScatterData("location1");

        assertNotNull(result);
        assertEquals("NETWORK", result.getLocationName());
        assertEquals(1, result.getScatterDataDTO().size());
        ScatterDataDTO scatterData = result.getScatterDataDTO().getFirst();
        assertEquals(200.0, scatterData.getItemPrice());
        assertEquals(100, scatterData.getSupplyQuantity());
        assertEquals(50, scatterData.getDemandQuantity());
    }

    @Test
    void testCalculateAvailabilityByLocationWhenNoRecordsFound() {
        String itemId = "item1";
        String locationId = "loc1";

        when(supplyRepository.findByItemIdAndLocationIdAndSupplyType(itemId, locationId, "ONHAND"))
                .thenReturn(Collections.emptyList());
        when(demandRepository.findByItemIdAndLocationIdAndDemandType(itemId, locationId, "HARD_PROMISED"))
                .thenReturn(Collections.emptyList());

        assertThrows(FoundException.class, () -> availabilityService.calculateAvailabilityByLocation(itemId, locationId));
    }

    @Test
    void testCalculateAvailabilityByLocationWithValidData() {
        String itemId = "item1";
        String locationId = "loc1";
        Supply supply = new Supply(); // Assume Supply class has a default constructor
        supply.setQuantity(20);
        Demand demand = new Demand(); // Assume Demand class has a default constructor
        demand.setQuantity(5);

        when(supplyRepository.findByItemIdAndLocationIdAndSupplyType(itemId, locationId, "ONHAND"))
                .thenReturn(Collections.singletonList(supply));
        when(demandRepository.findByItemIdAndLocationIdAndDemandType(itemId, locationId, "HARD_PROMISED"))
                .thenReturn(Collections.singletonList(demand));

        int availability = availabilityService.calculateAvailabilityByLocation(itemId, locationId);

        assertEquals(15, availability); // 20 (supply) - 5 (demand)
    }

    @Test
    void testCalculateV2AvailabilityByLocationWhenNoRecordsFound() {
        String itemId = "item1";
        String locationId = "loc1";

        when(supplyRepository.findByItemIdAndLocationIdAndSupplyType(itemId, locationId, "ONHAND"))
                .thenReturn(Collections.emptyList());
        when(demandRepository.findByItemIdAndLocationIdAndDemandType(itemId, locationId, "HARD_PROMISED"))
                .thenReturn(Collections.emptyList());

        assertThrows(FoundException.class, () -> availabilityService.calculateV2AvailabilityByLocation(itemId, locationId));
    }

    @Test
    void testCalculateV2AvailabilityByLocationWithValidData() {
        String itemId = "item1";
        String locationId = "loc1";
        Supply supply = new Supply();
        supply.setQuantity(30);
        Demand demand = new Demand();
        demand.setQuantity(10);
        AtpThreshold threshold = new AtpThreshold();
        threshold.setMinThreshold(5);
        threshold.setMaxThreshold(25);

        when(supplyRepository.findByItemIdAndLocationIdAndSupplyType(itemId, locationId, "ONHAND"))
                .thenReturn(Collections.singletonList(supply));
        when(demandRepository.findByItemIdAndLocationIdAndDemandType(itemId, locationId, "HARD_PROMISED"))
                .thenReturn(Collections.singletonList(demand));
        when(atpThresholdRepository.findByItemIdAndLocationId(itemId, locationId))
                .thenReturn(Optional.of(threshold));

        AvailabilityResponseV2V3 response = availabilityService.calculateV2AvailabilityByLocation(itemId, locationId);

        assertNotNull(response);
        assertEquals(20, response.getAvailableQty());
        assertEquals("Yellow", response.getStockLevel());
    }

    @Test
    void testCalculateV3AvailabilityByLocationWhenLocationIsExcluded() {
        String itemId = "item1";
        String locationId = "excludedLocation";

        when(availabilityConfig.getExcludedLocations()).thenReturn(new String[]{"excludedLocation"});

        assertThrows(FoundException.class, () -> availabilityService.calculateV3AvailabilityByLocation(itemId, locationId));
    }

    @Test
    void testCalculateV3AvailabilityByLocationWhenNoRecordsFound() {
        String itemId = "item1";
        String locationId = "loc1";

        when(availabilityConfig.getSupplies()).thenReturn(new String[]{"ONHAND"});
        when(availabilityConfig.getDemands()).thenReturn(new String[]{"HARD_PROMISED"});
        when(supplyRepository.findByItemIdAndLocationIdAndSupplyTypeIn(itemId, locationId, List.of("ONHAND")))
                .thenReturn(Collections.emptyList());
        when(demandRepository.findByItemIdAndLocationIdAndDemandTypeIn(itemId, locationId, List.of("HARD_PROMISED")))
                .thenReturn(Collections.emptyList());

        assertThrows(FoundException.class, () -> availabilityService.calculateV3AvailabilityByLocation(itemId, locationId));
    }


    @Test
    void testGetAvailabilityScatterDataWithValidLocation() {
        String locationId = "loc1";
        List<Item> items = new ArrayList<>();
        Item item1 = new Item(); // Assume Item has a default constructor
        item1.setItemId("item1");
        item1.setItemDescription("Item 1");
        item1.setPrice(10L);
        items.add(item1);

        when(locationRepository.findByLocationId(locationId)).thenReturn(Optional.of(new Location()));
        when(itemRepository.findAll()).thenReturn(items);

        when(supplyRepository.findByItemIdAndLocationIdAndSupplyType("item1", locationId, "ONHAND"))
                .thenReturn(Collections.singletonList(new Supply("sId","itemid","locId",SupplyType.ONHAND,15))); // Assume Supply constructor sets quantity
        when(demandRepository.findByItemIdAndLocationIdAndDemandType("item1", locationId, "HARD_PROMISED"))
                .thenReturn(Collections.singletonList(new Demand("sId",DemandType.HARD_PROMISED,5,"itemid","locId"))); // Assume Demand constructor sets quantity

        ScatterLocationDataDTO scatterData = availabilityService.getAvailabilityScatterData(locationId);

        assertNotNull(scatterData);
        assertEquals(1, scatterData.getScatterDataDTO().size());
        assertEquals(15, scatterData.getScatterDataDTO().getFirst().getSupplyQuantity());
        assertEquals(5, scatterData.getScatterDataDTO().getFirst().getDemandQuantity());
    }

    @Test
    void testGetAvailabilityScatterDataWithInvalidLocation() {
        String locationId = "invalidLoc";

        when(locationRepository.findByLocationId(locationId)).thenReturn(Optional.empty());

        ScatterLocationDataDTO scatterData = availabilityService.getAvailabilityScatterData(locationId);

        assertNotNull(scatterData);
        assertEquals("NETWORK", scatterData.getLocationName());
    }

    // Test: Calculate availability by item when no records found
    @Test
    void testCalculateAvailabilityByItemWhenNoRecordsFound() {
        String itemId = "item1";

        when(supplyRepository.findByItemIdAndSupplyType(itemId, "ONHAND"))
                .thenReturn(Collections.emptyList());
        when(demandRepository.findByItemIdAndDemandType(itemId, "HARD_PROMISED"))
                .thenReturn(Collections.emptyList());

        assertThrows(FoundException.class, () -> availabilityService.calculateAvailabilityByItem(itemId));
    }

    // Test: Calculate availability by item with valid supply and demand data
    @Test
    void testCalculateAvailabilityByItemWithValidData() {
        String itemId = "item1";
        Supply supply = new Supply();
        supply.setQuantity(40);
        Demand demand = new Demand();
        demand.setQuantity(20);

        when(supplyRepository.findByItemIdAndSupplyType(itemId, "ONHAND"))
                .thenReturn(Collections.singletonList(supply));
        when(demandRepository.findByItemIdAndDemandType(itemId, "HARD_PROMISED"))
                .thenReturn(Collections.singletonList(demand));

        int availability = availabilityService.calculateAvailabilityByItem(itemId);

        assertEquals(20, availability); // 40 (supply) - 20 (demand)
    }

    // Test: Calculate V2 availability across all locations when no records found
    @Test
    void testCalculateV2AvailabilityInAllLocationWhenNoRecordsFound() {
        String itemId = "item1";

        when(supplyRepository.findByItemIdAndSupplyType(itemId, "ONHAND"))
                .thenReturn(Collections.emptyList());
        when(demandRepository.findByItemIdAndDemandType(itemId, "HARD_PROMISED"))
                .thenReturn(Collections.emptyList());
        when(atpThresholdRepository.findById(itemId)).thenReturn(Optional.empty());

        assertThrows(FoundException.class, () -> availabilityService.calculateV2AvailabilityInAllLocation(itemId));
    }

    // Test: Calculate V2 availability across all locations with valid data
    @Test
    void testCalculateV2AvailabilityInAllLocationWithValidData() {
        String itemId = "item1";
        Supply supply = new Supply();
        supply.setQuantity(100);
        Demand demand = new Demand();
        demand.setQuantity(50);
        AtpThreshold threshold = new AtpThreshold();
        threshold.setMinThreshold(20);
        threshold.setMaxThreshold(45);

        when(supplyRepository.findByItemIdAndSupplyType(itemId, "ONHAND"))
                .thenReturn(Collections.singletonList(supply));
        when(demandRepository.findByItemIdAndDemandType(itemId, "HARD_PROMISED"))
                .thenReturn(Collections.singletonList(demand));
        when(atpThresholdRepository.findById(itemId)).thenReturn(Optional.of(threshold));

        AvailabilityResponseV2V3 response = availabilityService.calculateV2AvailabilityInAllLocation(itemId);

        assertNotNull(response);
        assertEquals(50, response.getAvailableQty()); // 100 (supply) - 50 (demand)
        assertEquals("Green", response.getStockLevel()); // Available quantity exceeds max threshold
    }

    // Test: Calculate V3 availability when all records are valid
    @Test
    void testCalculateV3AvailabilityByLocationWithValidData() {
        String itemId = "item1";
        String locationId = "loc1";
        Supply supply = new Supply();
        supply.setQuantity(70);
        Demand demand = new Demand();
        demand.setQuantity(30);
        AtpThreshold threshold = new AtpThreshold();
        threshold.setMinThreshold(15);
        threshold.setMaxThreshold(60);

        when(availabilityConfig.getSupplies()).thenReturn(new String[]{"ONHAND"});
        when(availabilityConfig.getDemands()).thenReturn(new String[]{"HARD_PROMISED"});
        when(supplyRepository.findByItemIdAndLocationIdAndSupplyTypeIn(itemId, locationId, List.of("ONHAND")))
                .thenReturn(Collections.singletonList(supply));
        when(demandRepository.findByItemIdAndLocationIdAndDemandTypeIn(itemId, locationId, List.of("HARD_PROMISED")))
                .thenReturn(Collections.singletonList(demand));
        when(atpThresholdRepository.findByItemIdAndLocationId(itemId, locationId))
                .thenReturn(Optional.of(threshold));

        AvailabilityResponseV2V3 response = availabilityService.calculateV3AvailabilityByLocation(itemId, locationId);

        assertNotNull(response);
        assertEquals(40, response.getAvailableQty()); // 70 (supply) - 30 (demand)
        assertEquals("Yellow", response.getStockLevel()); // Available quantity between min and max threshold
    }

    // Test: Calculate stock level without thresholds
    @Test
    void testCalculateStockLevelWithoutThreshold() {
        int availableQty = 50;

        String stockLevel = availabilityService.calculateStockLevel(Optional.empty(), availableQty);

        assertEquals("Unknown", stockLevel); // No threshold available
    }

    // Test: Calculate stock level when quantity is below minimum threshold
    @Test
    void testCalculateStockLevelBelowMinThreshold() {
        AtpThreshold threshold = new AtpThreshold();
        threshold.setMinThreshold(30);
        threshold.setMaxThreshold(70);
        int availableQty = 20;

        String stockLevel = availabilityService.calculateStockLevel(Optional.of(threshold), availableQty);

        assertEquals("Red", stockLevel); // Quantity is below the minimum threshold
    }




    // Test: Get scatter data for an empty item list
    @Test
    void testGetAvailabilityScatterDataWithNoItems() {
        String locationId = "loc1";

        when(locationRepository.findByLocationId(locationId)).thenReturn(Optional.of(new Location()));
        when(itemRepository.findAll()).thenReturn(Collections.emptyList());

        ScatterLocationDataDTO scatterData = availabilityService.getAvailabilityScatterData(locationId);

        assertNotNull(scatterData);
        assertTrue(scatterData.getScatterDataDTO().isEmpty()); // No items available
    }

    // Test: Get scatter data for multiple items with valid data
    @Test
    void testGetAvailabilityScatterDataWithMultipleItems() {
        String locationId = "loc1";
        List<Item> items = new ArrayList<>();
        Item item1 = new Item();
        item1.setItemId("item1");
        item1.setItemDescription("Item 1");
        item1.setPrice(10L);

        Item item2 = new Item();
        item2.setItemId("item2");
        item2.setItemDescription("Item 2");
        item2.setPrice(15L);

        items.add(item1);
        items.add(item2);

        when(locationRepository.findByLocationId(locationId)).thenReturn(Optional.of(new Location()));
        when(itemRepository.findAll()).thenReturn(items);
        when(supplyRepository.findByItemIdAndLocationIdAndSupplyType(item1.getItemId(), locationId, "ONHAND"))
                .thenReturn(Collections.singletonList(new Supply("sId","itemid","locId",SupplyType.ONHAND,20)));
        when(demandRepository.findByItemIdAndLocationIdAndDemandType(item1.getItemId(), locationId, "HARD_PROMISED"))
                .thenReturn(Collections.singletonList(new Demand("sId",DemandType.HARD_PROMISED,5,"itemid","locId")));
        when(supplyRepository.findByItemIdAndLocationIdAndSupplyType(item2.getItemId(), locationId, "ONHAND"))
                .thenReturn(Collections.singletonList(new Supply("sId","itemid","locId",SupplyType.ONHAND,15)));
        when(demandRepository.findByItemIdAndLocationIdAndDemandType(item2.getItemId(), locationId, "HARD_PROMISED"))
                .thenReturn(Collections.singletonList(new Demand("sId",DemandType.HARD_PROMISED,10,"itemid","locId")));

        ScatterLocationDataDTO scatterData = availabilityService.getAvailabilityScatterData(locationId);

        assertNotNull(scatterData);
        assertEquals(2, scatterData.getScatterDataDTO().size()); // 2 items processed
        assertEquals(15, scatterData.getScatterDataDTO().get(1).getSupplyQuantity());
        assertEquals(10, scatterData.getScatterDataDTO().get(1).getDemandQuantity());
    }

}
