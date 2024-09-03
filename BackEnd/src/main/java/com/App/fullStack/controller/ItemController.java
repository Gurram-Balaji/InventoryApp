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


@RestController
@RequestMapping("/items")
public class ItemController {

    @Autowired
    public ItemService itemService;

    @GetMapping
    public ResponseEntity<ApiResponse<Page<Item>>> getAllItems(@RequestParam(defaultValue = "0") int page,
                                                               @RequestParam(defaultValue = "10") int size) {
        return APIResponseForFoundOrNot.generateResponse(itemService.getAllItems(page, size), "Items Found", "Items Not Found");
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<ApiResponse<Item>> getItemById(@PathVariable String itemId) {
        return APIResponseForFoundOrNot.generateResponse(itemService.getItemByItemId(itemId), "Item Found",
                "Item Not Found");
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Item>> addItem(@RequestBody Item item) {
        return APIResponseForFoundOrNot.generateResponse(itemService.addItem(item), "Item Added", "Item Not Added");
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<ApiResponse<Item>> updateItem(@PathVariable String itemId, @RequestBody Item itemDetails) {
        return APIResponseForFoundOrNot.generateResponse(itemService.updateItem(itemId, itemDetails), "Item Updated",
                "Item Not Updated");
    }


    @PatchMapping("FulfillmentOptions/{itemId}")
    public ResponseEntity<ApiResponse<Item>> updateItemFulfillmentOptions(@PathVariable String itemId, @RequestBody Item itemDetails) {
        return APIResponseForFoundOrNot.generateResponse(itemService.updateItemFulfillmentOptions(itemId, itemDetails), "Item Updated",
                "Item Not Updated");
    }

    @DeleteMapping("/{itemId}")
    public ResponseEntity<ApiResponse<String>> deleteItem(@PathVariable String itemId) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(new ApiResponse<>(true, "Item Delete Operation.", itemService.deleteItem(itemId)));
    }
}
