package com.App.fullStack.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.App.fullStack.dto.SupplyDetailsResponse;
import com.App.fullStack.dto.SupplyDTO;
import com.App.fullStack.dto.SupplySummaryResponse;
import com.App.fullStack.exception.FoundException;
import com.App.fullStack.pojos.*;
import com.App.fullStack.repositories.SupplyRepository;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SupplyServiceTest {

    @Mock
    private SupplyRepository supplyRepository;

    @Mock
    private ItemService itemService;

    @Mock
    private LocationService locationService;

    @Mock
    private ItemAndLocationIDChecker itemAndLocationIDChecker;

    @InjectMocks
    private SupplyService supplyService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetAllSupplies_Success() {
        List<Supply> supplies = new ArrayList<>();
        supplies.add(new Supply("supply1", "item1", "loc1", SupplyType.ONHAND, 100));
        Page<Supply> supplyPage = new PageImpl<>(supplies);
        when(supplyRepository.findAll(any(Pageable.class))).thenReturn(supplyPage);

        Page<Supply> result = supplyService.getAllSupplies(0, 10);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(supplyRepository, times(1)).findAll(any(Pageable.class));
    }

    @Test
    public void testGetAllSupplies_NotFound() {
        when(supplyRepository.findAll(any(Pageable.class))).thenReturn(Page.empty());

        FoundException exception = assertThrows(FoundException.class, () -> {
            supplyService.getAllSupplies(0, 10);
        });

        assertEquals("Supplies not found.", exception.getMessage());
    }

    @Test
    public void testGetSupplyById_Success() {
        Supply supply = new Supply("supply1", "item1", "loc1", SupplyType.ONHAND, 100);
        when(supplyRepository.findBySupplyId("supply1")).thenReturn(Optional.of(supply));

        Supply result = supplyService.getSupplyById("supply1");

        assertNotNull(result);
        assertEquals("supply1", result.getSupplyId());
        verify(supplyRepository, times(1)).findBySupplyId("supply1");
    }

    @Test
    public void testGetSupplyById_NotFound() {
        when(supplyRepository.findBySupplyId("supply1")).thenReturn(Optional.empty());

        FoundException exception = assertThrows(FoundException.class, () -> {
            supplyService.getSupplyById("supply1");
        });

        assertEquals("Supply with supplyId supply1 not found.", exception.getMessage());
    }

    @Test
    public void testGetSuppliesByItemIdAndLocationId_Success() {
        List<Supply> supplies = new ArrayList<>();
        supplies.add(new Supply("supply1", "item1", "loc1", SupplyType.ONHAND, 100));
        when(supplyRepository.findByItemIdAndLocationId("item1", "loc1")).thenReturn(supplies);

        SupplyDetailsResponse result = supplyService.getSuppliesByItemIdAndLocationId("item1", "loc1");

        assertNotNull(result);
        assertEquals("item1", result.getItemId());
        assertEquals(1, result.getSupplyDetails().size());
        verify(supplyRepository, times(1)).findByItemIdAndLocationId("item1", "loc1");
    }

    @Test
    public void testGetSuppliesByItemIdAndLocationId_NotFound() {
        when(supplyRepository.findByItemIdAndLocationId("item1", "loc1")).thenReturn(new ArrayList<>());

        FoundException exception = assertThrows(FoundException.class, () -> {
            supplyService.getSuppliesByItemIdAndLocationId("item1", "loc1");
        });

        assertEquals("Supplies with ItemId: item1 and locationId loc1 not found.", exception.getMessage());
    }

    @Test
    public void testAddSupply_Success() {
        Supply supply = new Supply("supply1", "item1", "loc1", SupplyType.ONHAND, 100);
        when(supplyRepository.existsByItemIdAndLocationIdAndSupplyType(anyString(), anyString(), any(SupplyType.class))).thenReturn(false);
        when(supplyRepository.save(any(Supply.class))).thenReturn(supply);

        Supply result = supplyService.addSupply(supply);

        assertNotNull(result);
        assertEquals("supply1", result.getSupplyId());
        verify(supplyRepository, times(1)).save(any(Supply.class));
    }

    @Test
    public void testAddSupply_AlreadyExists() {
        when(supplyRepository.existsByItemIdAndLocationIdAndSupplyType(anyString(), anyString(), any(SupplyType.class))).thenReturn(true);

        FoundException exception = assertThrows(FoundException.class, () -> {
            supplyService.addSupply(new Supply("supply1", "item1", "loc1", SupplyType.ONHAND, 100));
        });

        assertEquals("Supplies with itemId: item1, locationId: loc1 and supplyType: ONHAND already exists.", exception.getMessage());
    }

}
