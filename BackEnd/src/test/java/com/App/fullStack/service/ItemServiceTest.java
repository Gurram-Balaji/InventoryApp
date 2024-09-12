package com.App.fullStack.service;

import com.App.fullStack.exception.FoundException;
import com.App.fullStack.pojos.Item;
import com.App.fullStack.repositories.DemandRepository;
import com.App.fullStack.repositories.ItemRepository;
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

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ItemServiceTest {

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private SupplyRepository supplyRepository;

    @Mock
    private DemandRepository demandRepository;

    @InjectMocks
    private ItemService itemService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetAllItemsWithKeyword() {
        Pageable pageable = PageRequest.of(0, 10);
        List<Item> items = Arrays.asList(new Item(), new Item());
        Page<Item> itemPage = new PageImpl<>(items, pageable, items.size());
        String keyword = "search";

        when(itemRepository.searchItemsByKeyword(keyword, pageable)).thenReturn(itemPage);

        Page<Item> result = itemService.getAllItems(0, 10, keyword);
        assertEquals(items.size(), result.getContent().size());
    }

    @Test
    public void testGetAllItemsWithoutKeyword() {
        Pageable pageable = PageRequest.of(0, 10);
        List<Item> items = Arrays.asList(new Item(), new Item());
        Page<Item> itemPage = new PageImpl<>(items, pageable, items.size());

        when(itemRepository.findAll(pageable)).thenReturn(itemPage);

        Page<Item> result = itemService.getAllItems(0, 10, null);
        assertEquals(items.size(), result.getContent().size());
    }

    @Test
    public void testGetItemByItemId() {
        String itemId = "item1";
        Item item = new Item();
        when(itemRepository.findByItemId(itemId)).thenReturn(Optional.of(item));

        Item result = itemService.getItemByItemId(itemId);
        assertNotNull(result);
    }

    @Test
    public void testGetItemByItemIdNotFound() {
        String itemId = "item1";
        when(itemRepository.findByItemId(itemId)).thenReturn(Optional.empty());

        Exception exception = assertThrows(FoundException.class, () -> itemService.getItemByItemId(itemId));
        assertEquals("Item with itemId item1 not exist.", exception.getMessage());
    }

    @Test
    public void testAddItem() {
        Item item = new Item();
        when(itemRepository.findByItemId(item.getItemId())).thenReturn(Optional.empty());
        when(itemRepository.save(item)).thenReturn(item);

        Item result = itemService.addItem(item);
        assertNotNull(result);
        assertEquals(item, result);
    }

    @Test
    public void testAddItemAlreadyExists() {
        Item item = new Item();
        when(itemRepository.findByItemId(item.getItemId())).thenReturn(Optional.of(item));

        Exception exception = assertThrows(FoundException.class, () -> itemService.addItem(item));
        assertEquals("Item with itemId null already exists.", exception.getMessage());
    }

    @Test
    public void testUpdateItem() {
        String itemId = "item1";
        Item existingItem = new Item();
        existingItem.setItemDescription("Old Description");
        Item updatedItem = new Item();
        updatedItem.setItemDescription("New Description");
        when(itemRepository.findByItemId(itemId)).thenReturn(Optional.of(existingItem));
        when(itemRepository.save(existingItem)).thenReturn(existingItem);

        Item result = itemService.updateItem(itemId, updatedItem);
        assertNotNull(result);
        assertEquals("New Description", result.getItemDescription());
    }

    @Test
    public void testUpdateItemNotFound() {
        String itemId = "item1";
        Item updatedItem = new Item();
        when(itemRepository.findByItemId(itemId)).thenReturn(Optional.empty());

        Exception exception = assertThrows(FoundException.class, () -> itemService.updateItem(itemId, updatedItem));
        assertEquals("Item with itemId item1 not exist.", exception.getMessage());
    }

    @Test
    public void testDeleteItem() {
        String itemId = "item1";
        when(supplyRepository.existsByItemId(itemId)).thenReturn(false);
        when(demandRepository.existsByItemId(itemId)).thenReturn(false);
        when(itemRepository.existsByItemId(itemId)).thenReturn(true);
        doNothing().when(itemRepository).deleteByItemId(itemId);

        String result = itemService.deleteItem(itemId);
        assertEquals("Item deleted successfully.", result);
    }

    @Test
    public void testDeleteItemWithAssociatedRecords() {
        String itemId = "item1";
        when(supplyRepository.existsByItemId(itemId)).thenReturn(true);

        Exception exception = assertThrows(FoundException.class, () -> itemService.deleteItem(itemId));
        assertEquals("Item cannot be deleted because it has associated supply or demand records.", exception.getMessage());
    }

    @Test
    public void testGetAllItemIds() {
        List<String> itemIds = Arrays.asList("item1", "item2");
        when(itemRepository.findDistinctItemIds()).thenReturn(itemIds);

        List<String> result = itemService.getAllItemIds();
        assertEquals(itemIds, result);
    }

    @Test
    public void testGetItemByItemIdWithOutException() {
        String itemId = "item1";
        Item item = new Item();
        when(itemRepository.findByItemId(itemId)).thenReturn(Optional.of(item));

        Item result = itemService.getItemByItemIdWithOutException(itemId);
        assertNotNull(result);
    }

    @Test
    public void testGetItemByItemIdWithOutExceptionNotFound() {
        String itemId = "item1";
        when(itemRepository.findByItemId(itemId)).thenReturn(Optional.empty());

        Item result = itemService.getItemByItemIdWithOutException(itemId);
        assertNull(result);
    }
}
