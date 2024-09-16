package com.App.fullStack.controller;

import com.App.fullStack.pojos.Item;
import com.App.fullStack.responseHandler.ApiResponse;
import com.App.fullStack.service.ItemService;
import com.App.fullStack.utility.APIResponseForFoundOrNot;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/items")
public class ItemController {

    @Autowired
    private ItemService itemService;

    // Constants for pagination and messages
    private static final String DEFAULT_PAGE = "0";
    private static final String DEFAULT_SIZE = "8";
    private static final String ITEMS_FOUND = "Items Found";
    private static final String ITEMS_NOT_FOUND = "Items Not Found";
    private static final String ITEM_FOUND = "Item Found";
    private static final String ITEM_NOT_FOUND = "Item Not Found";
    private static final String ITEM_ADDED = "Item Added";
    private static final String ITEM_NOT_ADDED = "Item Not Added";
    private static final String ITEM_UPDATED = "Item Updated";
    private static final String ITEM_NOT_UPDATED = "Item Not Updated";
    private static final String ITEM_DELETE_OPERATION = "Item Delete Operation.";
    private static final String ITEM_IDS_FOUND = "Item Ids Found.";

    // Get all items with optional search and pagination
    @GetMapping
    public ResponseEntity<ApiResponse<Page<Item>>> getAllItems(
            @RequestParam(defaultValue = DEFAULT_PAGE) int page,
            @RequestParam(defaultValue = DEFAULT_SIZE) int size,
            @RequestParam(required = false) String search) {
        
        Page<Item> items = itemService.getAllItems(page, size, search);
        return APIResponseForFoundOrNot.generateResponse(items, ITEMS_FOUND, ITEMS_NOT_FOUND);
    }

    // Get an item by ID
    @GetMapping("/{itemId}")
    public ResponseEntity<ApiResponse<Item>> getItemById(@PathVariable String itemId) {
        Item item = itemService.getItemByItemId(itemId);
        return APIResponseForFoundOrNot.generateResponse(item, ITEM_FOUND, ITEM_NOT_FOUND);
    }

    // Add a new item
    @PostMapping
    public ResponseEntity<ApiResponse<Item>> addItem(@RequestBody Item item) {
        Item addedItem = itemService.addItem(item);
        return APIResponseForFoundOrNot.generateResponse(addedItem, ITEM_ADDED, ITEM_NOT_ADDED);
    }

    // Update an existing item
    @PatchMapping("/{itemId}")
    public ResponseEntity<ApiResponse<Item>> updateItem(@PathVariable String itemId, @RequestBody Item itemDetails) {
        Item updatedItem = itemService.updateItem(itemId, itemDetails);
        return APIResponseForFoundOrNot.generateResponse(updatedItem, ITEM_UPDATED, ITEM_NOT_UPDATED);
    }

    // Delete an item by ID
    @DeleteMapping("/{itemId}")
    public ResponseEntity<ApiResponse<String>> deleteItem(@PathVariable String itemId) {
        String result = itemService.deleteItem(itemId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new ApiResponse<>(true, ITEM_DELETE_OPERATION, result));
    }

    // Get all item IDs
    @GetMapping("/ids")
    public ResponseEntity<ApiResponse<Page<String>>> getAllItemIds( @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                                                                    @RequestParam(defaultValue = DEFAULT_SIZE) int size,
                                                                    @RequestParam(required = false) String search) {

        Page<String> itemIds = itemService.getAllItemIds(page, size, search);
        return APIResponseForFoundOrNot.generateResponse(itemIds, ITEMS_FOUND, ITEMS_NOT_FOUND);
    }
}
