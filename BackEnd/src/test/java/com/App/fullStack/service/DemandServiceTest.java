package com.App.fullStack.service;

import com.App.fullStack.dto.DemandDetailsResponse;
import com.App.fullStack.dto.DemandSummaryResponse;
import com.App.fullStack.dto.DemandDTO;
import com.App.fullStack.exception.FoundException;
import com.App.fullStack.pojos.*;
import com.App.fullStack.repositories.DemandRepository;
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
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class DemandServiceTest {

    @Mock
    private DemandRepository demandRepository;

    @Mock
    private ItemAndLocationIDChecker itemAndLocationIDChecker;

    @Mock
    private ItemService itemService;

    @Mock
    private LocationService locationService;

    @InjectMocks
    private DemandService demandService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetAllDemands() {
        List<Demand> demands = Arrays.asList(
                new Demand("d1",DemandType.HARD_PROMISED, 10, "item1", "loc1"),
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
        Demand demand = new Demand(demandId,DemandType.HARD_PROMISED, 10, "item1", "loc1");

        when(demandRepository.findByDemandId(demandId)).thenReturn(Optional.of(demand));

        Demand result = demandService.getDemandById(demandId);
        assertNotNull(result);
        assertEquals(demandId, result.getDemandId());
    }

    @Test
    public void testGetDemandByIdNotFound() {
        String demandId = "d1";

        when(demandRepository.findByDemandId(demandId)).thenReturn(Optional.empty());

        Exception exception = assertThrows(FoundException.class, () -> {
            demandService.getDemandById(demandId);
        });
        assertEquals("Demand with demandId " + demandId + " not found.", exception.getMessage());
    }

    @Test
    public void testGetDemandsByItemIdAndLocationId() {
        String itemId = "item1";
        String locationId = "loc1";
        List<Demand> demands = Collections.singletonList(
                new Demand("d1",DemandType.HARD_PROMISED, 10, itemId, locationId)
        );

        when(demandRepository.findByItemIdAndLocationId(itemId, locationId)).thenReturn(demands);

        DemandDetailsResponse result = demandService.getDemandsByItemIdAndLocationId(itemId, locationId);
        assertEquals(itemId, result.getItemId());
        assertEquals(locationId, result.getLocationId());
        assertEquals(10, result.getDemandDetails().get(DemandType.HARD_PROMISED));
    }

    @Test
    public void testGetDemandsByTypeAndLocationId() {
        String locationId = "loc1";
        DemandType demandType = DemandType.HARD_PROMISED;
        List<Demand> demands = Arrays.asList(
                new Demand("d1",demandType, 10, "item1", locationId),
                new Demand("d2", demandType, 20, "item2", locationId)
        );

        when(demandRepository.findByDemandTypeAndLocationId(demandType, locationId)).thenReturn(demands);

        DemandSummaryResponse result = demandService.getDemandsByTypeAndLocationId(demandType, locationId);
        assertEquals(locationId, result.getLocationId());
        assertEquals(30, result.getDemandDetails().get(demandType));
    }

    @Test
    public void testDeleteDemand() {
        String demandId = "d1";
        Demand demand = new Demand(demandId,DemandType.HARD_PROMISED, 10, "item1", "loc1");

        when(demandRepository.findByDemandId(demandId)).thenReturn(Optional.of(demand));

        String result = demandService.deleteDemand(demandId);
        assertEquals("Demand deleted successfully", result);
    }



}
