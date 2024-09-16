package com.App.fullStack.controler;

import com.App.fullStack.controller.ItemController;
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
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ItemControllerTest {

    @Mock
    private ItemService itemService;

    @InjectMocks
    private ItemController itemController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getAllItems_Success() {
        Page<Item> mockPage = mock(Page.class);
        ApiResponse<Page<Item>> response = new ApiResponse<>(true, "Items Found", mockPage);
        when(itemService.getAllItems(0, 8, null)).thenReturn(mockPage);

        ResponseEntity<ApiResponse<Page<Item>>> result = itemController.getAllItems(0, 8, null);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(response.isSuccess(), Objects.requireNonNull(result.getBody()).isSuccess());
    }

    @Test
    void getItemById_Success() {
        Item item = new Item();
        ApiResponse<Item> response = new ApiResponse<>(true, "Item Found", item);
        when(itemService.getItemByItemId("itemId")).thenReturn(item);

        ResponseEntity<ApiResponse<Item>> result = itemController.getItemById("itemId");

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(response.getPayload(), Objects.requireNonNull(result.getBody()).getPayload());
    }

    @Test
    void addItem_Success() {
        Item item = new Item();
        ApiResponse<Item> response = new ApiResponse<>(true, "Item Added", item);
        when(itemService.addItem(item)).thenReturn(item);

        ResponseEntity<ApiResponse<Item>> result = itemController.addItem(item);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(response.getPayload(), Objects.requireNonNull(result.getBody()).getPayload());
    }

    @Test
    void deleteItem_Success() {
        ApiResponse<String> response = new ApiResponse<>(true, "Item Delete Operation.", "itemId");
        when(itemService.deleteItem("itemId")).thenReturn("itemId");

        ResponseEntity<ApiResponse<String>> result = itemController.deleteItem("itemId");

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(response.getPayload(), Objects.requireNonNull(result.getBody()).getPayload());
    }

}
