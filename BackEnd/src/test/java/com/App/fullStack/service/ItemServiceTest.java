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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

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
    public void testGetAllItemsWithoutKeyword() {
        Pageable pageable = PageRequest.of(0, 10);
        List<Item> items = Arrays.asList(new Item("itemId", "Item 1","Item name","Item cat", null, null, 0.0, true, true, true),
                new Item("itemId", "Item 2","Item name","Item cat", null, null, 0.0, true, true, true));
        Page<Item> itemPage = new PageImpl<>(items, pageable, items.size());

        when(itemRepository.findAll(pageable)).thenReturn(itemPage);

        Page<Item> result = itemService.getAllItems(0, 10, null);
        assertEquals(items.size(), result.getContent().size());
    }

    @Test
    public void testGetAllItemsWithKeyword() {
        Pageable pageable = PageRequest.of(0, 10);
        List<Item> items = Collections.singletonList(new Item("itemId", "Item 1","Item name","Item cat", null, null, 0.0, true, true, true));
        Page<Item> itemPage = new PageImpl<>(items, pageable, items.size());

        when(itemRepository.searchItemsByKeyword("Item", pageable)).thenReturn(itemPage);

        Page<Item> result = itemService.getAllItems(0, 10, "Item");
        assertEquals(items.size(), result.getContent().size());
    }

    @Test
    public void testGetItemByItemId() {
        String itemId = "Item1";
        Item item = new Item(itemId, "Item1","Item name","Item cat", null, null, 0.0, true, true, true);
        when(itemRepository.findByItemId(itemId)).thenReturn(Optional.of(item));

        Item result = itemService.getItemByItemId(itemId);
        assertNotNull(result);
        assertEquals(itemId, result.getItemId());
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
        Item item = new Item("itemId", "Item 1","Item name","Item cat", null, null, 0.0, true, true, true);
        when(itemRepository.findByItemId(item.getItemId())).thenReturn(Optional.empty());
        when(itemRepository.save(item)).thenReturn(item);

        Item result = itemService.addItem(item);
        assertNotNull(result);
        assertEquals(item, result);
    }

    @Test
    public void testAddItemAlreadyExists() {
        Item item = new Item("itemId", "Item1","Item name","Item cat", null, null, 0.0, true, true, true);
        when(itemRepository.findByItemId(item.getItemId())).thenReturn(Optional.of(item));

        Exception exception = assertThrows(FoundException.class, () -> itemService.addItem(item));
        assertEquals("Item with itemId Item1 already exists.", exception.getMessage());
    }

    @Test
    public void testUpdateItem() {
        String itemId = "item1";
        Item existingItem = new Item(itemId, "Item 1","Item name","Item cat", null, null, 0.0, true, true, true);
        Item updatedItem = new Item(itemId, "Item 1","New Description","Item cat", null, null, 0.0, true, true, true);
        when(itemRepository.findByItemId(itemId)).thenReturn(Optional.of(existingItem));
        when(itemRepository.save(existingItem)).thenReturn(existingItem);

        Item result = itemService.updateItem(itemId, updatedItem);
        assertNotNull(result);
        assertEquals("New Description", result.getItemDescription());
    }

    @Test
    public void testUpdateItemNotFound() {
        String itemId = "item1";
        Item updatedItem = new Item(itemId, "Item 1","Item name","Item cat", null, null, 0.0, true, true, true);
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
    public void testGetItemByItemIdWithOutException() {
        String itemId = "item1";
        Item item = new Item(itemId, "Item 1","Item name","Item cat", null, null, 0.0, true, true, true);
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
    @Test
    public void testGetAllItemsNoResults() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Item> emptyPage = new PageImpl<>(Collections.emptyList(), pageable, 0);

        when(itemRepository.findAll(pageable)).thenReturn(emptyPage);

        Page<Item> result = itemService.getAllItems(0, 10, null);
        assertTrue(result.isEmpty());
    }

    @Test
    public void testGetAllItemsWithKeywordNoResults() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Item> emptyPage = new PageImpl<>(Collections.emptyList(), pageable, 0);

        when(itemRepository.searchItemsByKeyword("InvalidKeyword", pageable)).thenReturn(emptyPage);

        Page<Item> result = itemService.getAllItems(0, 10, "InvalidKeyword");
        assertTrue(result.isEmpty());
    }

    @Test
    public void testAddItemWithNullDescription() {
        Item item = new Item("Id", "itemId",null,"Item cat", null, null, 0.0, true, true, true);
        when(itemRepository.findByItemId(item.getItemId())).thenReturn(Optional.empty());
        when(itemRepository.save(item)).thenReturn(item);

        Item result = itemService.addItem(item);
        assertNotNull(result);
        assertNull(result.getItemDescription());
    }

    @Test
    public void testUpdateItemNoFieldsUpdated() {
        String itemId = "item1";
        Item existingItem = new Item(itemId, "Item 1","Item name","Item cat", null, null, 0.0, true, true, true);
        Item updatedItem = new Item(itemId, null, null, null, null, null, 0.0, false, false, false);

        when(itemRepository.findByItemId(itemId)).thenReturn(Optional.of(existingItem));
        when(itemRepository.save(existingItem)).thenReturn(existingItem);

        Item result = itemService.updateItem(itemId, updatedItem);
        assertEquals(existingItem, result); // No fields were updated
    }

    @Test
    public void testDeleteItemNotFound() {
        String itemId = "item1";
        when(itemRepository.existsByItemId(itemId)).thenReturn(false);

        Exception exception = assertThrows(FoundException.class, () -> itemService.deleteItem(itemId));
        assertEquals("Item with itemId item1 not exist.", exception.getMessage());
    }

    @Test
    public void testGetItemByItemIdWithOutExceptionNullItemId() {
        Item result = itemService.getItemByItemIdWithOutException(null);
        assertNull(result);
    }

    @Test
    public void testUpdateItemNullItemId() {
        String itemId = null;
        Item updatedItem = new Item("id", itemId,"New Description","Item cat", null, null, 0.0, true, true, true);

        Exception exception = assertThrows(FoundException.class, () -> itemService.updateItem(itemId, updatedItem));
        assertEquals("Item with itemId null not exist.", exception.getMessage());
    }

    @Test
    public void testGetAllItemsWithKeywordEmptyResult() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Item> emptyPage = Page.empty(pageable);

        when(itemRepository.searchItemsByKeyword("NonExistentItem", pageable)).thenReturn(emptyPage);

        Page<Item> result = itemService.getAllItems(0, 10, "NonExistentItem");
        assertNotNull(result);
        assertTrue(result.getContent().isEmpty());
    }

    /**
     * Test getAllItemIds with a search keyword that returns item IDs.
     */
    @Test
    public void testGetAllItemIdsWithSearch() {
        Pageable pageable = PageRequest.of(0, 10);
        List<String> itemIds = Arrays.asList("item1", "item2");
        Page<String> itemIdPage = new PageImpl<>(itemIds, pageable, itemIds.size());

        when(itemRepository.searchItemIdsByKeyword("item", pageable)).thenReturn(itemIdPage);

        Page<String> result = itemService.getAllItemIds(0, 10, "item");
        assertNotNull(result);
        assertEquals(2, result.getContent().size());
        assertEquals(itemIds, result.getContent());
    }

    /**
     * Test getAllItemIds with a search keyword that returns no item IDs.
     */
    @Test
    public void testGetAllItemIdsWithSearchEmptyResult() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<String> emptyPage = Page.empty(pageable);

        when(itemRepository.searchItemIdsByKeyword("nonexistent", pageable)).thenReturn(emptyPage);

        Page<String> result = itemService.getAllItemIds(0, 10, "nonexistent");
        assertNotNull(result);
        assertTrue(result.getContent().isEmpty());
    }

    /**
     * Test getAllItemIds without a search keyword that returns distinct item IDs.
     */
    @Test
    public void testGetAllItemIdsWithoutSearch() {
        Pageable pageable = PageRequest.of(0, 10);
        List<String> itemIds = Arrays.asList("item1", "item2", "item3");
        Page<String> itemIdPage = new PageImpl<>(itemIds, pageable, itemIds.size());

        when(itemRepository.findDistinctItemIds(pageable)).thenReturn(itemIdPage);

        Page<String> result = itemService.getAllItemIds(0, 10, null);
        assertNotNull(result);
        assertEquals(3, result.getContent().size());
        assertEquals(itemIds, result.getContent());
    }

    /**
     * Test getAllItemIds without a search keyword that returns no item IDs.
     */
    @Test
    public void testGetAllItemIdsWithoutSearchEmptyResult() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<String> emptyPage = Page.empty(pageable);

        when(itemRepository.findDistinctItemIds(pageable)).thenReturn(emptyPage);

        Page<String> result = itemService.getAllItemIds(0, 10, "");
        assertNotNull(result);
        assertTrue(result.getContent().isEmpty());
    }

    /**
     * Test deleteItem when the item does not exist.
     */
    @Test
    public void testDeleteItemWhenItemDoesNotExist() {
        String itemId = "nonExistentItem";
        when(supplyRepository.existsByItemId(itemId)).thenReturn(false);
        when(demandRepository.existsByItemId(itemId)).thenReturn(false);
        when(itemRepository.existsByItemId(itemId)).thenReturn(false);

        Exception exception = assertThrows(FoundException.class, () -> itemService.deleteItem(itemId));
        assertEquals("Item with itemId nonExistentItem not exist.", exception.getMessage());
    }

    /**
     * Test updateItem with partial fields (some fields are null or default).
     */
    @Test
    public void testUpdateItemPartialUpdate() {
        String itemId = "item1";
        Item existingItem = new Item(itemId, "Item 1", "Original Description", "Original Category", null, null, 100.0, true, true, true);
        Item updateDetails = new Item(); // Partial update: only some fields set
        updateDetails.setItemDescription("Updated Description");
        // Leave category as null to test partial update
        updateDetails.setPrice(0.0); // Assuming price=0 means no update

        when(itemRepository.findByItemId(itemId)).thenReturn(Optional.of(existingItem));
        when(itemRepository.save(existingItem)).thenReturn(existingItem);

        Item result = itemService.updateItem(itemId, updateDetails);
        assertNotNull(result);
        assertEquals("Updated Description", result.getItemDescription());
        assertEquals("Original Category", result.getCategory()); // Should remain unchanged
        assertEquals(100.0, result.getPrice()); // Should remain unchanged since updateDetails.price was 0
    }

    /**
     * Test getAllItems with keyword that includes leading/trailing spaces.
     */
    @Test
    public void testGetAllItemsWithKeywordWithSpaces() {
        Pageable pageable = PageRequest.of(0, 10);
        List<Item> items = Arrays.asList(
                new Item("item1", "Item One", "Description One", "Category A", null, null, 50.0, true, true, true),
                new Item("item2", "Item Two", "Description Two", "Category B", null, null, 75.0, true, true, true)
        );
        Page<Item> itemPage = new PageImpl<>(items, pageable, items.size());

        when(itemRepository.searchItemsByKeyword("  Item  ", pageable)).thenReturn(itemPage);

        Page<Item> result = itemService.getAllItems(0, 10, "  Item  ");
        assertNotNull(result);
        assertEquals(2, result.getContent().size());
    }


    @Test
    public void testUpdateItemWithNullDetails() {
        String itemId = "item1";
        Item existingItem = new Item(itemId, "Item 1", "Original Description", "Original Category", null, null, 100.0, true, true, true);

        when(itemRepository.findByItemId(itemId)).thenReturn(Optional.of(existingItem));
        when(itemRepository.save(existingItem)).thenReturn(existingItem);

        // Assuming that passing null for itemDetails is not allowed and should throw an exception
      assertThrows(NullPointerException.class, () -> itemService.updateItem(itemId, null));
        // Depending on implementation, you might throw a custom exception instead
    }

    /**
     * Test deleteItem when demand exists but supply does not.
     */
    @Test
    public void testDeleteItemWithDemandAssociated() {
        String itemId = "item1";
        when(supplyRepository.existsByItemId(itemId)).thenReturn(false);
        when(demandRepository.existsByItemId(itemId)).thenReturn(true);

        Exception exception = assertThrows(FoundException.class, () -> itemService.deleteItem(itemId));
        assertEquals("Item cannot be deleted because it has associated supply or demand records.", exception.getMessage());
    }

    /**
     * Test addItem with all fields set.
     */
    @Test
    public void testAddItemAllFieldsSet() {
        Item item = new Item("itemId", "Item Name", "Full Description", "Category A", null, null, 150.0, true, false, true);
        when(itemRepository.findByItemId(item.getItemId())).thenReturn(Optional.empty());
        when(itemRepository.save(item)).thenReturn(item);

        Item result = itemService.addItem(item);
        assertNotNull(result);
        assertEquals("Item Name", result.getItemId());
        assertEquals("Full Description", result.getItemDescription());
        assertEquals("Category A", result.getCategory());
        assertEquals(150.0, result.getPrice());
        assertTrue(result.isPickupAllowed());
        assertFalse(result.isShippingAllowed());
        assertTrue(result.isDeliveryAllowed());
    }


    @Test
    public void testGetItemByItemIdWithOutExceptionInvalidFormat() {
        String invalidItemId = ""; // Assuming empty string is invalid
        when(itemRepository.findByItemId(invalidItemId)).thenReturn(Optional.empty());

        Item result = itemService.getItemByItemIdWithOutException(invalidItemId);
        assertNull(result);
    }
}
