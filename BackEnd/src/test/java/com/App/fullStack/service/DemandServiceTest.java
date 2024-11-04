package com.App.fullStack.service;

import com.App.fullStack.dto.DemandDTO;
import com.App.fullStack.dto.DemandDetailsResponse;
import com.App.fullStack.dto.DemandSummaryResponse;
import com.App.fullStack.exception.FoundException;
import com.App.fullStack.pojos.*;
import com.App.fullStack.repositories.DemandRepository;
import com.App.fullStack.repositories.ItemRepository;
import com.App.fullStack.repositories.LocationRepository;
import com.App.fullStack.utility.ItemAndLocationIDChecker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class DemandServiceTest {

    @Mock
    private DemandRepository demandRepository;

    @Mock
    public ItemAndLocationIDChecker itemAndLocationIDChecker;

    @Mock
    private ItemService itemService;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private LocationService locationService;

    @Mock
    private LocationRepository locationRepository;

    @InjectMocks
    private DemandService demandService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetAllDemands() {
        List<Demand> demands = Arrays.asList(
                new Demand("d1", DemandType.HARD_PROMISED, 10, "item1", "loc1"),
                new Demand("d2", DemandType.PLANNED, 20, "item2", "loc2")
        );
        Pageable pageable = PageRequest.of(0, 10);
        Page<Demand> demandPage = new PageImpl<>(demands, pageable, demands.size());

        when(demandRepository.findAll(pageable)).thenReturn(demandPage);

        Page<Demand> result = demandService.getAllDemands(0, 10);
        assertEquals(2, result.getTotalElements());
    }

    @Test
    public void testGetDemandById() {
        String demandId = "d1";
        Demand demand = new Demand(demandId, DemandType.HARD_PROMISED, 10, "item1", "loc1");

        when(demandRepository.findByDemandId(demandId)).thenReturn(Optional.of(demand));

        Demand result = demandService.getDemandById(demandId);
        assertNotNull(result);
        assertEquals(demandId, result.getDemandId());
    }

    @Test
    public void testGetDemandByIdNotFound() {
        String demandId = "d1";

        when(demandRepository.findByDemandId(demandId)).thenReturn(Optional.empty());

        Exception exception = assertThrows(FoundException.class, () -> demandService.getDemandById(demandId));
        assertEquals("Demand with demandId " + demandId + " not found.", exception.getMessage());
    }

    @Test
    public void testGetDemandsByItemIdAndLocationId() {
        String itemId = "item1";
        String locationId = "loc1";
        List<Demand> demands = Collections.singletonList(
                new Demand("d1", DemandType.HARD_PROMISED, 10, itemId, locationId)
        );

        when(demandRepository.findByItemIdAndLocationId(itemId, locationId)).thenReturn(demands);

        DemandDetailsResponse result = demandService.getDemandsByItemIdAndLocationId(itemId, locationId);
        assertEquals(itemId, result.getItemId());
        assertEquals(locationId, result.getLocationId());
        assertEquals(10, result.getDemandDetails().get(DemandType.HARD_PROMISED));
    }

    @Test
    public void testGetDemandsByItemIdAndLocationIdNotFound() {
        String itemId = "item1";
        String locationId = "loc1";

        when(demandRepository.findByItemIdAndLocationId(itemId, locationId)).thenReturn(Collections.emptyList());

        Exception exception = assertThrows(FoundException.class, () ->
                demandService.getDemandsByItemIdAndLocationId(itemId, locationId));
        assertEquals("Demands with ItemId: " + itemId + " and locationId " + locationId + " not found.", exception.getMessage());
    }

    @Test
    public void testGetDemandsByTypeAndLocationId() {
        String locationId = "loc1";
        DemandType demandType = DemandType.HARD_PROMISED;
        List<Demand> demands = Arrays.asList(
                new Demand("d1", demandType, 10, "item1", locationId),
                new Demand("d2", demandType, 20, "item2", locationId)
        );

        when(demandRepository.findByDemandTypeAndLocationId(demandType, locationId)).thenReturn(demands);

        DemandSummaryResponse result = demandService.getDemandsByTypeAndLocationId(demandType, locationId);
        assertEquals(locationId, result.getLocationId());
        assertEquals(30, result.getDemandDetails().get(demandType));
    }

    @Test
    public void testGetDemandsByTypeAndLocationIdNotFound() {
        String locationId = "loc1";
        DemandType demandType = DemandType.HARD_PROMISED;

        when(demandRepository.findByDemandTypeAndLocationId(demandType, locationId)).thenReturn(Collections.emptyList());

        Exception exception = assertThrows(FoundException.class, () ->
                demandService.getDemandsByTypeAndLocationId(demandType, locationId));
        assertEquals("Demands with demandType " + demandType + " and locationId " + locationId + " not found.", exception.getMessage());
    }

    @Test
    void testAddDemandSuccess() {
        Demand newDemand = new Demand("d1", DemandType.HARD_PROMISED, 10, "item1", "loc1");

        // Mock behavior for demandRepository
        when(demandRepository.existsByItemIdAndLocationIdAndDemandType("item1", "loc1", DemandType.HARD_PROMISED)).thenReturn(false);

        // Ensure itemAndLocationIDChecker does nothing on validation
        doNothing().when(itemAndLocationIDChecker).validateItemAndLocationID("item1", "loc1");

        // Mock save behavior
        when(demandRepository.save(newDemand)).thenReturn(newDemand);

        // Act
        Demand result = demandService.addDemand(newDemand);

        // Assert
        assertNotNull(result);
        assertEquals("d1", result.getDemandId());

        // Verify the validateItemAndLocationID method was called
        verify(itemAndLocationIDChecker).validateItemAndLocationID("item1", "loc1");
    }


    @Test
    public void testAddDemandAlreadyExists() {
        Demand newDemand = new Demand("d1", DemandType.HARD_PROMISED, 10, "item1", "loc1");

        when(demandRepository.existsByItemIdAndLocationIdAndDemandType("item1", "loc1", DemandType.HARD_PROMISED)).thenReturn(true);

        Exception exception = assertThrows(FoundException.class, () -> demandService.addDemand(newDemand));
        assertEquals("Demands with itemId: item1, locationId: loc1 and demandType: HARD_PROMISED already exists.", exception.getMessage());
    }

    @Test
    public void testUpdateDemand() {
        String demandId = "d1";
        Demand existingDemand = new Demand(demandId, DemandType.HARD_PROMISED, 10, "item1", "loc1");
        Demand updatedDemandDetails = new Demand(demandId, DemandType.HARD_PROMISED, 20, "item1", "loc1");

        when(demandRepository.findByDemandId(demandId)).thenReturn(Optional.of(existingDemand));
        when(demandRepository.save(existingDemand)).thenReturn(updatedDemandDetails);

        Demand result = demandService.updateDemand(demandId, updatedDemandDetails);
        assertNotNull(result);
        assertEquals(20, result.getQuantity());
    }

    @Test
    public void testUpdateDemandNotFound() {
        String demandId = "d1";
        Demand updatedDemandDetails = new Demand(demandId, DemandType.HARD_PROMISED, 20, "item1", "loc1");

        when(demandRepository.findByDemandId(demandId)).thenReturn(Optional.empty());

        Exception exception = assertThrows(FoundException.class, () -> demandService.updateDemand(demandId, updatedDemandDetails));
        assertEquals("Demand with demandId " + demandId + " not found.", exception.getMessage());
    }

    @Test
    public void testDeleteDemand() {
        String demandId = "d1";
        Demand demand = new Demand(demandId, DemandType.HARD_PROMISED, 10, "item1", "loc1");

        when(demandRepository.findByDemandId(demandId)).thenReturn(Optional.of(demand));

        String result = demandService.deleteDemand(demandId);
        assertEquals("Demand deleted successfully", result);
        verify(demandRepository, times(1)).delete(demand);
    }

    @Test
    public void testDeleteDemandNotFound() {
        String demandId = "d1";

        when(demandRepository.findByDemandId(demandId)).thenReturn(Optional.empty());

        Exception exception = assertThrows(FoundException.class, () -> demandService.deleteDemand(demandId));
        assertEquals("Demand with demandId " + demandId + " not found.", exception.getMessage());
    }
    @Test
    public void testGetAllDemandsNotFound() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Demand> emptyPage = new PageImpl<>(Collections.emptyList(), pageable, 0);

        when(demandRepository.findAll(pageable)).thenReturn(emptyPage);

        Exception exception = assertThrows(FoundException.class, () -> demandService.getAllDemands(0, 10));
        assertEquals("Demand records not found.", exception.getMessage());
    }

    @Test
    public void testGetAllDemandWithDetailsWhenSearchIsNull() {
        int page = 0;
        int size = 10;

        // Mocking the demand repository to return a page of demands
        Demand demand = new Demand("d1", DemandType.HARD_PROMISED, 10, "item1", "loc1");
        List<Demand> demands = Collections.singletonList(demand);
        Pageable pageable = PageRequest.of(page, size);
        Page<Demand> demandPage = new PageImpl<>(demands, pageable, demands.size());

        when(demandRepository.findAll(pageable)).thenReturn(demandPage);
        when(itemService.getItemByItemIdWithOutException(demand.getItemId())).thenReturn(new Item("id","item1", "Item Description","Item cat", null, null, 0.0, true, true, true));
        when(locationService.getLocationByIdWithoutException(demand.getLocationId())).thenReturn(new Location("id","loc1", "Location Description", LocationType.DISTRIBUTION_CENTER, true, false, true, "Address1", null, null, "City1", "State1", "Country1", "12345"));

        Page<DemandDTO> result = demandService.getAllDemandWithDetails(page, size, null, null);

        assertEquals(1, result.getTotalElements());
        assertEquals("Item Description", result.getContent().getFirst().getItemDescription());
        assertEquals("Location Description", result.getContent().getFirst().getLocationDescription());
    }

    @Test
    public void testGetAllDemandWithDetailsWhenSearchIsEmpty() {
        int page = 0;
        int size = 10;

        Demand demand = new Demand("d1", DemandType.HARD_PROMISED, 10, "item1", "loc1");
        List<Demand> demands = Collections.singletonList(demand);
        Pageable pageable = PageRequest.of(page, size);
        Page<Demand> demandPage = new PageImpl<>(demands, pageable, demands.size());

        when(demandRepository.findAll(pageable)).thenReturn(demandPage);
        when(itemService.getItemByItemIdWithOutException(demand.getItemId())).thenReturn(new Item("id","item1", "Item Description","Item cat", null, null, 0.0, true, true, true));
        when(locationService.getLocationByIdWithoutException(demand.getLocationId())).thenReturn(new Location("id","loc1", "Location Description", LocationType.DISTRIBUTION_CENTER, true, false, true, "Address1", null, null, "City1", "State1", "Country1", "12345"));

        Page<DemandDTO> result = demandService.getAllDemandWithDetails(page, size, "", null);

        assertEquals(1, result.getTotalElements());
        assertEquals("Item Description", result.getContent().getFirst().getItemDescription());
        assertEquals("Location Description", result.getContent().getFirst().getLocationDescription());
    }

    @Test
    public void testGetAllDemandWithDetailsWhenSearchByItemAndNoResultsFound() {
        int page = 0;
        int size = 10;
        String search = "nonExistentItem";
        String searchBy = "item";

        // Mock behavior for item repository to return an empty list
        when(itemRepository.searchItemIdsByKeywordGetIds(search)).thenReturn(Collections.emptyList());

        Exception exception = assertThrows(FoundException.class, () -> demandService.getAllDemandWithDetails(page, size, search, searchBy));
        assertEquals("Demands not found.", exception.getMessage());
    }

    @Test
    public void testGetAllDemandWithDetailsWhenSearchByLocationAndNoResultsFound() {
        int page = 0;
        int size = 10;
        String search = "nonExistentLocation";
        String searchBy = "location";

        // Mock behavior for location repository to return an empty list
        when(locationRepository.searchLocationIdsByKeywordGetIds(search)).thenReturn(Collections.emptyList());

        Exception exception = assertThrows(FoundException.class, () -> demandService.getAllDemandWithDetails(page, size, search, searchBy));
        assertEquals("Demands not found.", exception.getMessage());
    }

    @Test
    public void testGetAllDemandWithDetailsWhenSearchByDemandTypeAndNoResultsFound() {
        int page = 0;
        int size = 10;
        String search = "nonExistentDemandType";
        String searchBy = "demandType";

        // Mock behavior for demand repository to return an empty page
        Pageable pageable = PageRequest.of(page, size);
        Page<Demand> emptyPage = new PageImpl<>(Collections.emptyList(), pageable, 0);
        when(demandRepository.findByDemandType(search, pageable)).thenReturn(emptyPage);

        Exception exception = assertThrows(FoundException.class, () -> demandService.getAllDemandWithDetails(page, size, search, searchBy));
        assertEquals("Demands not found..", exception.getMessage());
    }

    @Test
    public void testAddDemandWithExistingDemand() {
        Demand newDemand = new Demand("d1", DemandType.HARD_PROMISED, 10, "item1", "loc1");

        // Mock behavior for existing demand
        when(demandRepository.existsByItemIdAndLocationIdAndDemandType("item1", "loc1", DemandType.HARD_PROMISED)).thenReturn(true);

        Exception exception = assertThrows(FoundException.class, () -> demandService.addDemand(newDemand));
        assertEquals("Demands with itemId: item1, locationId: loc1 and demandType: HARD_PROMISED already exists.", exception.getMessage());
    }

    @Test
    public void testUpdateDemandWithInvalidDemandId() {
        String demandId = "invalidDemandId";
        Demand updatedDemandDetails = new Demand(demandId, DemandType.HARD_PROMISED, 20, "item1", "loc1");

        when(demandRepository.findByDemandId(demandId)).thenReturn(Optional.empty());

        Exception exception = assertThrows(FoundException.class, () -> demandService.updateDemand(demandId, updatedDemandDetails));
        assertEquals("Demand with demandId " + demandId + " not found.", exception.getMessage());
    }

    @Test
    public void testDeleteDemandWithInvalidDemandId() {
        String demandId = "invalidDemandId";

        when(demandRepository.findByDemandId(demandId)).thenReturn(Optional.empty());

        Exception exception = assertThrows(FoundException.class, () -> demandService.deleteDemand(demandId));
        assertEquals("Demand with demandId " + demandId + " not found.", exception.getMessage());
    }


    @Test
    public void testAddDemandWithValidDemand() {
        Demand newDemand = new Demand("d1", DemandType.HARD_PROMISED, 10, "item1", "loc1");

        // Mock behavior to ensure demand doesn't exist
        when(demandRepository.existsByItemIdAndLocationIdAndDemandType("item1", "loc1", DemandType.HARD_PROMISED)).thenReturn(false);
        doNothing().when(itemAndLocationIDChecker).validateItemAndLocationID(anyString(), anyString());
        when(demandRepository.save(newDemand)).thenReturn(newDemand);

        Demand result = demandService.addDemand(newDemand);

        assertNotNull(result);
        assertEquals(newDemand.getDemandId(), result.getDemandId());
    }

    @Test
    public void testGetAllDemandsWithPagination() {
        int page = 0;
        int size = 10;
        Demand demand1 = new Demand("d1", DemandType.HARD_PROMISED, 10, "item1", "loc1");
        Demand demand2 = new Demand("d2", DemandType.PLANNED, 20, "item2", "loc2");
        List<Demand> demands = Arrays.asList(demand1, demand2);
        Pageable pageable = PageRequest.of(page, size);
        Page<Demand> demandPage = new PageImpl<>(demands, pageable, demands.size());

        when(demandRepository.findAll(pageable)).thenReturn(demandPage);
        when(itemService.getItemByItemIdWithOutException(demand1.getItemId())).thenReturn(new Item("id","item1", "Item Description 1","Item cat", null, null, 0.0, true, true, true));
        when(locationService.getLocationByIdWithoutException(demand1.getLocationId())).thenReturn(new Location("id","loc1", "Location Description 1", LocationType.DISTRIBUTION_CENTER, true, false, true, "Address1", null, null, "City1", "State1", "Country1", "12345"));
        when(itemService.getItemByItemIdWithOutException(demand2.getItemId())).thenReturn(new Item("id","item2", "Item Description 2","Item cat", null, null, 0.0, true, true, true));
        when(locationService.getLocationByIdWithoutException(demand2.getLocationId())).thenReturn(new Location("id","loc2", "Location Description 2", LocationType.DISTRIBUTION_CENTER, true, false, true, "Address1", null, null, "City1", "State1", "Country1", "12345"));

        Page<DemandDTO> result = demandService.getAllDemandWithDetails(page, size, null, null);

        assertEquals(2, result.getTotalElements());
        assertEquals("Item Description 1", result.getContent().get(0).getItemDescription());
        assertEquals("Location Description 1", result.getContent().get(0).getLocationDescription());
        assertEquals("Item Description 2", result.getContent().get(1).getItemDescription());
        assertEquals("Location Description 2", result.getContent().get(1).getLocationDescription());
    }

    @Test
    public void testUpdateDemandWithExistingDemand() {
        String demandId = "d1";
        Demand existingDemand = new Demand(demandId, DemandType.HARD_PROMISED, 10, "item1", "loc1");
        Demand updatedDemand = new Demand(demandId, DemandType.HARD_PROMISED, 15, "item1", "loc1");

        when(demandRepository.findByDemandId(demandId)).thenReturn(Optional.of(existingDemand));
        when(demandRepository.save(existingDemand)).thenReturn(existingDemand);

        Demand result = demandService.updateDemand(demandId, updatedDemand);

        assertNotNull(result);
        assertEquals(15, result.getQuantity());
    }

    @Test
    public void testDeleteDemandWithValidDemandId() {
        String demandId = "d1";
        Demand existingDemand = new Demand(demandId, DemandType.HARD_PROMISED, 10, "item1", "loc1");

        when(demandRepository.findByDemandId(demandId)).thenReturn(Optional.of(existingDemand));

        String resultMessage = demandService.deleteDemand(demandId);

        assertEquals("Demand deleted successfully", resultMessage);
        verify(demandRepository, times(1)).delete(existingDemand);
    }

    @Test
    public void testAddDemandWithInvalidItemAndLocationID() {
        Demand newDemand = new Demand("d1", DemandType.HARD_PROMISED, 10, "item1", "loc1");

        // Mock behavior to ensure demand doesn't exist
        when(demandRepository.existsByItemIdAndLocationIdAndDemandType("item1", "loc1", DemandType.HARD_PROMISED)).thenReturn(false);

        // Use doThrow for the void method
        doThrow(new IllegalArgumentException("Invalid Item or Location ID"))
                .when(itemAndLocationIDChecker).validateItemAndLocationID("item1", "loc1");

        // Verify that the exception is thrown
        Exception exception = assertThrows(IllegalArgumentException.class, () -> demandService.addDemand(newDemand));
        assertEquals("Invalid Item or Location ID", exception.getMessage());
    }

    @Test
    public void testGetDemandsWithInvalidPageSize() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> demandService.getAllDemands(0, -5));
        assertEquals("Page size must not be less than one", exception.getMessage());
    }

    @Test
    public void testSearchDemandsByItemIdWithEmptyString() {
        String searchBy = "item";
        assertThrows(FoundException.class, () -> demandService.getAllDemandWithDetails(0, 8, "", searchBy));// Expect no results for an empty string
    }

    @Test
    public void testConcurrentAddDemand() throws InterruptedException {
        Demand newDemand = new Demand("d1", DemandType.HARD_PROMISED, 10, "item1", "loc1");

        // Simulate concurrent save calls
        when(demandRepository.existsByItemIdAndLocationIdAndDemandType(anyString(), anyString(), any(DemandType.class)))
                .thenReturn(false);

        // Create two threads to simulate concurrency
        Thread thread1 = new Thread(() -> demandService.addDemand(newDemand));
        Thread thread2 = new Thread(() -> demandService.addDemand(newDemand));

        thread1.start();
        thread2.start();

        thread1.join();
        thread2.join();

        verify(demandRepository, times(2)).save(any(Demand.class));
    }

    @Test
    public void testPartialUpdateDemand() {
        String demandId = "d1";
        Demand existingDemand = new Demand(demandId, DemandType.HARD_PROMISED, 10, "item1", "loc1");
        Demand partialUpdate = new Demand(demandId, null, 15, null, null); // Only update quantity

        when(demandRepository.findByDemandId(demandId)).thenReturn(Optional.of(existingDemand));
        when(demandRepository.save(any(Demand.class))).thenReturn(existingDemand);

        Demand result = demandService.updateDemand(demandId, partialUpdate);

        assertNotNull(result);
        assertEquals(15, result.getQuantity()); // Updated
        assertEquals(DemandType.HARD_PROMISED, result.getDemandType()); // Not changed
        assertEquals("item1", result.getItemId()); // Not changed
        assertEquals("loc1", result.getLocationId()); // Not changed
    }



}
