package com.App.fullStack.controller;

import com.App.fullStack.pojos.Item;
import com.App.fullStack.responseHandler.ApiResponse;
import com.App.fullStack.service.ItemService;
import com.App.fullStack.utility.APIResponseForFoundOrNot;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/items")
public class ItemController {

    @Autowired
    private ItemService itemService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<Item>>> getAllItems() {
        return APIResponseForFoundOrNot.generateResponse(itemService.getAllItems(), "Items Found", "Items Not Found");
    }

    @GetMapping("/{itemid}")
    public ResponseEntity<ApiResponse<Item>> getItemById(@PathVariable String itemid) {
        return APIResponseForFoundOrNot.generateResponse(itemService.getItemByItemId(itemid), "Item Found",
                "Item Not Found");
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Item>> addItem(@RequestBody Item item) {
        return APIResponseForFoundOrNot.generateResponse(itemService.addItem(item), "Item Added", "Item Not Added");
    }

    @PatchMapping("/{itemid}")
    public ResponseEntity<ApiResponse<Item>> updateItem(@PathVariable String itemid, @RequestBody Item itemDetails) {
        return APIResponseForFoundOrNot.generateResponse(itemService.updateItem(itemid, itemDetails), "Item Updated",
                "Item Not Updated");
    }

    @DeleteMapping("/{itemid}")
    public ResponseEntity<ApiResponse<String>> deleteItem(@PathVariable String itemid) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(new ApiResponse<>(true, "Item Delete Operation.", itemService.deleteItem(itemid)));
    }
}
