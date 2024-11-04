package com.App.fullStack.service;

import static com.App.fullStack.pojos.LocationType.HUB;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.App.fullStack.dto.SupplyDetailsResponse;
import com.App.fullStack.dto.SupplySummaryResponse;
import com.App.fullStack.exception.FoundException;
import com.App.fullStack.pojos.Item;
import com.App.fullStack.pojos.Location;
import com.App.fullStack.pojos.Supply;
import com.App.fullStack.pojos.SupplyType;
import com.App.fullStack.repositories.ItemRepository;
import com.App.fullStack.repositories.LocationRepository;
import com.App.fullStack.repositories.SupplyRepository;
import com.App.fullStack.dto.SupplyDTO;
import com.App.fullStack.utility.ItemAndLocationIDChecker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.*;

public class SupplyServiceTest {

    @InjectMocks
    private SupplyService supplyService;

    @Mock
    private SupplyRepository supplyRepository;
    @Mock
    private ItemRepository itemRepository;


    @Mock
    private LocationRepository locationRepository;

    @Mock
    private ItemAndLocationIDChecker itemAndLocationIDChecker;

    @Mock
    private ItemService itemService;

    @Mock
    private LocationService locationService;

    private Supply supply;
    private Item item;
    private Location location;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        supply = new Supply("1", "item1", "location1", SupplyType.ONHAND, 10);
        item = new Item("id","item1", "Sample Item", "Category1", "Type1", null, 100.0, true, true, true);
        location = new Location("id","location1", "Sample Location", HUB, true, true, true, "123 Street", null, null, "City", "State", "Country", "123456");
    }

    @Test
    public void testGetAllSupplies_Success() {
        Page<Supply> supplyPage = new PageImpl<>(Collections.singletonList(supply));
        when(supplyRepository.findAll(PageRequest.of(0, 10))).thenReturn(supplyPage);

        Page<Supply> result = supplyService.getAllSupplies(0, 10);
        assertFalse(result.isEmpty());
        assertEquals(1, result.getTotalElements());
    }

    @Test
    public void testGetAllSupplies_Empty() {
        when(supplyRepository.findAll(PageRequest.of(0, 10))).thenReturn(Page.empty());

        Exception exception = assertThrows(FoundException.class, () -> supplyService.getAllSupplies(0, 10));
        assertEquals("Supplies not found.", exception.getMessage());
    }

    @Test
    public void testGetSupplyById_Found() {
        when(supplyRepository.findBySupplyId("1")).thenReturn(Optional.of(supply));

        Supply result = supplyService.getSupplyById("1");
        assertEquals(supply, result);
    }

    @Test
    public void testGetSupplyById_NotFound() {
        when(supplyRepository.findBySupplyId("2")).thenReturn(Optional.empty());

        Exception exception = assertThrows(FoundException.class, () -> supplyService.getSupplyById("2"));
        assertEquals("Supply with supplyId 2 not found.", exception.getMessage());
    }

    @Test
    public void testGetSuppliesByItemIdAndLocationId_Found() {
        when(supplyRepository.findByItemIdAndLocationId("item1", "location1"))
                .thenReturn(Collections.singletonList(supply));

        SupplyDetailsResponse result = supplyService.getSuppliesByItemIdAndLocationId("item1", "location1");

        assertNotNull(result);
        assertEquals("item1", result.getItemId());
        assertEquals("location1", result.getLocationId());
        assertEquals(10, result.getSupplyDetails().get(SupplyType.ONHAND));
    }

    @Test
    public void testGetSuppliesByItemIdAndLocationId_NotFound() {
        when(supplyRepository.findByItemIdAndLocationId("item1", "location1"))
                .thenReturn(Collections.emptyList());

        Exception exception = assertThrows(FoundException.class, () -> supplyService.getSuppliesByItemIdAndLocationId("item1", "location1"));
        assertEquals("Supplies with ItemId: item1 and locationId location1 not found.", exception.getMessage());
    }

    @Test
    public void testGetSuppliesByTypeAndLocationId_Found() {
        when(supplyRepository.findBySupplyTypeAndLocationId(SupplyType.ONHAND, "location1"))
                .thenReturn(Collections.singletonList(supply));

        SupplySummaryResponse result = supplyService.getSuppliesByTypeAndLocationId(SupplyType.ONHAND, "location1");

        assertNotNull(result);
        assertEquals("location1", result.getLocationId());
    }

    @Test
    public void testGetSuppliesByTypeAndLocationId_NotFound() {
        when(supplyRepository.findBySupplyTypeAndLocationId(SupplyType.ONHAND, "location1"))
                .thenReturn(Collections.emptyList());

        Exception exception = assertThrows(FoundException.class, () -> supplyService.getSuppliesByTypeAndLocationId(SupplyType.ONHAND, "location1"));
        assertEquals("Supplies with supplyType ONHAND and locationId location1 not found.", exception.getMessage());
    }

    @Test
    public void testAddSupply_Success() {
        when(supplyRepository.existsByItemIdAndLocationIdAndSupplyType("item1", "location1", SupplyType.ONHAND))
                .thenReturn(false);
        doNothing().when(itemAndLocationIDChecker).validateItemAndLocationID("item1", "location1");
        when(supplyRepository.save(supply)).thenReturn(supply);

        Supply result = supplyService.addSupply(supply);
        assertEquals(supply, result);
    }

    @Test
    public void testAddSupply_AlreadyExists() {
        when(supplyRepository.existsByItemIdAndLocationIdAndSupplyType("item1", "location1", SupplyType.ONHAND))
                .thenReturn(true);

        Exception exception = assertThrows(FoundException.class, () -> supplyService.addSupply(supply));
        assertEquals("Supplies with itemId: item1, locationId: location1 and supplyType: ONHAND already exists.", exception.getMessage());
    }

    @Test
    public void testUpdateSupply_Found() {
        when(supplyRepository.findBySupplyId("1")).thenReturn(Optional.of(supply));
        when(supplyRepository.save(supply)).thenReturn(supply);

        Supply updatedSupply = new Supply("1", "item1", "location1", SupplyType.ONHAND, 20);
        Supply result = supplyService.updateSupply("1", updatedSupply);
        assertEquals(20, result.getQuantity());
    }

    @Test
    public void testUpdateSupply_NotFound() {
        when(supplyRepository.findBySupplyId("2")).thenReturn(Optional.empty());

        Exception exception = assertThrows(FoundException.class, () -> supplyService.updateSupply("2", supply));
        assertEquals("Supply with supplyId 2 not found.", exception.getMessage());
    }

    @Test
    public void testDeleteSupply_Found() {
        when(supplyRepository.findBySupplyId("1")).thenReturn(Optional.of(supply));
        doNothing().when(supplyRepository).delete(supply);
        String result = supplyService.deleteSupply("1");
        assertEquals("Supply deleted successfully", result);
    }

    @Test
    public void testDeleteSupply_NotFound() {
        when(supplyRepository.findBySupplyId("2")).thenReturn(Optional.empty());

        Exception exception = assertThrows(FoundException.class, () -> supplyService.deleteSupply("2"));
        assertEquals("Supply with supplyId 2 not found.", exception.getMessage());
    }

    @Test
    public void testGetAllSuppliesWithDetails_Success() {
        when(supplyRepository.findAll(PageRequest.of(0, 10))).thenReturn(new PageImpl<>(Collections.singletonList(supply)));
        when(itemService.getItemByItemIdWithOutException("item1")).thenReturn(item);
        when(locationService.getLocationByIdWithoutException("location1")).thenReturn(location);

        Page<SupplyDTO> result = supplyService.getAllSuppliesWithDetails(0, 10, null, null);
        assertFalse(result.isEmpty());
        assertEquals(1, result.getTotalElements());
    }

    @Test
    public void testGetAllSuppliesWithDetails_Empty() {
        when(supplyRepository.findAll(PageRequest.of(0, 10))).thenReturn(Page.empty());

        Exception exception = assertThrows(FoundException.class, () -> supplyService.getAllSuppliesWithDetails(0, 10, null, null));
        assertEquals("Supplies not found.", exception.getMessage());
    }

    @Test
    public void testAddAllSuppliesWithDetails() {
        List<Supply> supplies = Collections.singletonList(supply);
        when(itemService.getItemByItemId("item1")).thenReturn(item);
        when(locationService.getLocationByIdWithoutException("location1")).thenReturn(location);

        List<SupplyDTO> result = supplyService.addAllSuppliesWithDetails(supplies);
        assertEquals(1, result.size());
        assertNull(result.getFirst().getItemDescription());
    }

    @Test
    public void testGetAllSuppliesWithDetails_SearchNotFound() {
        // Mock the item repository to return an empty list (no matching items)
        when(itemRepository.searchItemIdsByKeywordGetIds(anyString()))
                .thenReturn(Collections.emptyList());

        Exception exception = assertThrows(FoundException.class, () ->
                supplyService.getAllSuppliesWithDetails(0, 10, "invalidItem", "item"));
        assertEquals("Supplies not found.", exception.getMessage());
    }

    @Test
    public void testGetAllSuppliesWithDetails_SearchNotFound_EmptySupplies() {
        // Mock the item repository to return some items
        List<Item> mockItems = List.of(new Item("itemId", "Item 1","Item name","Item cat", null, null, 0.0, true, true, true), new Item("itemId", "Item 2","Item name","Item cat", null, null, 0.0, true, true, true));
        when(itemRepository.searchItemIdsByKeywordGetIds(anyString()))
                .thenReturn(mockItems);

        // Mock the supply repository to return an empty page
        when(supplyRepository.findByItemIdIn(anyList(), any(PageRequest.class)))
                .thenReturn(Page.empty());

        Exception exception = assertThrows(FoundException.class, () ->
                supplyService.getAllSuppliesWithDetails(0, 10, "someItem", "item"));
        assertEquals("Supplies not found.", exception.getMessage());
    }


    @Test
    public void testAddSupply_InvalidItemOrLocation() {
        doThrow(new FoundException("Invalid item or location"))
                .when(itemAndLocationIDChecker).validateItemAndLocationID("item1", "location1");

        Exception exception = assertThrows(FoundException.class, () ->
                supplyService.addSupply(supply));
        assertEquals("Invalid item or location", exception.getMessage());
    }

    @Test
    public void testGetSuppliesByItemIdAndLocationId_MultipleTypes() {
        Supply supply2 = new Supply("2", "item1", "location1", SupplyType.DAMAGED, 5);
        when(supplyRepository.findByItemIdAndLocationId("item1", "location1"))
                .thenReturn(Arrays.asList(supply, supply2));

        SupplyDetailsResponse result = supplyService.getSuppliesByItemIdAndLocationId("item1", "location1");

        assertEquals(2, result.getSupplyDetails().size());
        assertEquals(10, result.getSupplyDetails().get(SupplyType.ONHAND));
        assertEquals(5, result.getSupplyDetails().get(SupplyType.DAMAGED));
    }

    @Test
    public void testGetAllSuppliesWithDetails_SearchByLocation() {
        when(locationRepository.searchLocationIdsByKeywordGetIds("Sample Location"))
                .thenReturn(Collections.singletonList(location));
        when(supplyRepository.findByLocationIdIn(anyList(), any(PageRequest.class)))
                .thenReturn(new PageImpl<>(Collections.singletonList(supply)));
        when(itemService.getItemByItemIdWithOutException("item1")).thenReturn(item);
        when(locationService.getLocationByIdWithoutException("location1")).thenReturn(location);

        Page<SupplyDTO> result = supplyService.getAllSuppliesWithDetails(0, 10, "Sample Location", "location");
        assertFalse(result.isEmpty());
        assertEquals(1, result.getTotalElements());
    }
    @Test
    public void testUpdateSupply_NoQuantityPassed() {
        Supply updatedSupply = new Supply("1", "item1", "location1", SupplyType.ONHAND, 0);
        when(supplyRepository.findBySupplyId("1")).thenReturn(Optional.of(supply));

        Exception exception = assertThrows(FoundException.class, () -> supplyService.updateSupply("1", updatedSupply));
        assertEquals("Invalid quantity", exception.getMessage());
    }


    @Test
    public void testGetSuppliesByTypeAndLocationId_NoSupplyTypes() {
        when(supplyRepository.findBySupplyTypeAndLocationId(SupplyType.DAMAGED, "location1"))
                .thenReturn(Collections.emptyList());

        Exception exception = assertThrows(FoundException.class, () -> supplyService.getSuppliesByTypeAndLocationId(SupplyType.DAMAGED, "location1"));
        assertEquals("Supplies with supplyType DAMAGED and locationId location1 not found.", exception.getMessage());
    }


}
