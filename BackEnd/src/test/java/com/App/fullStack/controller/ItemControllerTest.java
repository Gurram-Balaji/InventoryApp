package com.App.fullStack.controller;

import com.App.fullStack.pojos.Item;
import com.App.fullStack.responseHandler.ApiResponse;
import com.App.fullStack.service.ItemService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Objects;

class ItemControllerTest {

    @Mock
    private ItemService itemService;

    @InjectMocks
    private ItemController itemController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // Test case for getting all items successfully
    @Test
    void getAllItems_Success() {
        Page<Item> mockPage = mock(Page.class);
        when(itemService.getAllItems(0, 8, null)).thenReturn(mockPage);

        ResponseEntity<ApiResponse<Page<Item>>> result = itemController.getAllItems(0, 8, null);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertTrue(Objects.requireNonNull(result.getBody()).isSuccess());
        assertEquals(mockPage, result.getBody().getPayload());

        verify(itemService, times(1)).getAllItems(0, 8, null);
    }

    // Test case when no items are found
    @Test
    void getAllItems_NotFound() {
        Page<Item> mockPage = mock(Page.class);
        when(itemService.getAllItems(0, 8, null)).thenReturn(mockPage);

        ResponseEntity<ApiResponse<Page<Item>>> result = itemController.getAllItems(0, 8, null);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(result.getBody());
    }

    // Test case for getting an item by its ID successfully
    @Test
    void getItemById_Success() {
        Item mockItem = new Item();
        when(itemService.getItemByItemId("item123")).thenReturn(mockItem);

        ResponseEntity<ApiResponse<Item>> result = itemController.getItemById("item123");

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertTrue(Objects.requireNonNull(result.getBody()).isSuccess());
        assertEquals(mockItem, result.getBody().getPayload());

        verify(itemService, times(1)).getItemByItemId("item123");
    }

    // Test case when an item is not found by its ID
    @Test
    void getItemById_NotFound() {
        when(itemService.getItemByItemId("item123")).thenReturn(null);

        ResponseEntity<ApiResponse<Item>> result = itemController.getItemById("item123");

        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
        assertFalse(Objects.requireNonNull(result.getBody()).isSuccess());
        assertNull(result.getBody().getPayload());

        verify(itemService, times(1)).getItemByItemId("item123");
    }

    // Test case for adding a new item successfully
    @Test
    void addItem_Success() {
        Item mockItem = new Item();
        when(itemService.addItem(mockItem)).thenReturn(mockItem);

        ResponseEntity<ApiResponse<Item>> result = itemController.addItem(mockItem);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertTrue(Objects.requireNonNull(result.getBody()).isSuccess());
        assertEquals(mockItem, result.getBody().getPayload());

        verify(itemService, times(1)).addItem(mockItem);
    }

    // Test case for adding a new item failure (e.g., invalid data)
    @Test
    void addItem_Failure() {
        Item mockItem = new Item();
        when(itemService.addItem(mockItem)).thenReturn(null);

        ResponseEntity<ApiResponse<Item>> result = itemController.addItem(mockItem);

        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
        assertFalse(Objects.requireNonNull(result.getBody()).isSuccess());
        assertNull(result.getBody().getPayload());

        verify(itemService, times(1)).addItem(mockItem);
    }

    // Test case for updating an item successfully
    @Test
    void updateItem_Success() {
        Item mockItem = new Item();
        when(itemService.updateItem("item123", mockItem)).thenReturn(mockItem);

        ResponseEntity<ApiResponse<Item>> result = itemController.updateItem("item123", mockItem);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertTrue(Objects.requireNonNull(result.getBody()).isSuccess());
        assertEquals(mockItem, result.getBody().getPayload());

        verify(itemService, times(1)).updateItem("item123", mockItem);
    }

    // Test case for updating an item when the item is not found
    @Test
    void updateItem_NotFound() {
        Item mockItem = new Item();
        when(itemService.updateItem("item123", mockItem)).thenReturn(null);

        ResponseEntity<ApiResponse<Item>> result = itemController.updateItem("item123", mockItem);

        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
        assertFalse(Objects.requireNonNull(result.getBody()).isSuccess());
        assertNull(result.getBody().getPayload());

        verify(itemService, times(1)).updateItem("item123", mockItem);
    }

    // Test case for deleting an item successfully
    @Test
    void deleteItem_Success() {
        when(itemService.deleteItem("item123")).thenReturn("item123");

        ResponseEntity<ApiResponse<String>> result = itemController.deleteItem("item123");

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertTrue(Objects.requireNonNull(result.getBody()).isSuccess());
        assertEquals("item123", result.getBody().getPayload());

        verify(itemService, times(1)).deleteItem("item123");
    }

    // Test case for deleting an item when the item is not found
    @Test
    void deleteItem_NotFound() {
        when(itemService.deleteItem("item123")).thenReturn(null);

        ResponseEntity<ApiResponse<String>> result = itemController.deleteItem("item123");

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertTrue(Objects.requireNonNull(result.getBody()).isSuccess());
        assertNull(result.getBody().getPayload());

        verify(itemService, times(1)).deleteItem("item123");
    }

    // Test case for fetching item IDs successfully
    @Test
    void getAllItemIds_Success() {
        Page<String> mockPage = mock(Page.class);
        when(itemService.getAllItemIds(0, 8, null)).thenReturn(mockPage);

        ResponseEntity<ApiResponse<Page<String>>> result = itemController.getAllItemIds(0, 8, null);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertTrue(Objects.requireNonNull(result.getBody()).isSuccess());
        assertEquals(mockPage, result.getBody().getPayload());

        verify(itemService, times(1)).getAllItemIds(0, 8, null);
    }

    // Test case for fetching item IDs when none are found
    @Test
    void getAllItemIds_NotFound() {
        when(itemService.getAllItemIds(0, 8, null)).thenReturn(null);

        ResponseEntity<ApiResponse<Page<String>>> result = itemController.getAllItemIds(0, 8, null);

        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
        assertFalse(Objects.requireNonNull(result.getBody()).isSuccess());
        assertNull(result.getBody().getPayload());

        verify(itemService, times(1)).getAllItemIds(0, 8, null);
    }
}
