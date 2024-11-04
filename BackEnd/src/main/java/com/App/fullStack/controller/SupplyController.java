package com.App.fullStack.controller;

import com.App.fullStack.dto.SupplyDTO;
import com.App.fullStack.dto.SupplyDetailsResponse;
import com.App.fullStack.dto.SupplySummaryResponse;
import com.App.fullStack.pojos.Supply;
import com.App.fullStack.pojos.SupplyType;
import com.App.fullStack.responseHandler.ApiResponse;
import com.App.fullStack.service.SupplyService;
import com.App.fullStack.utility.APIResponseForFoundOrNot;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/supply")
public class SupplyController {

    @Autowired
    private SupplyService supplyService;

    // Constants for pagination and messages
    private static final String DEFAULT_PAGE = "0";
    private static final String DEFAULT_SIZE = "8";
    private static final String SUPPLIES_FOUND = "Supplies Found";
    private static final String SUPPLIES_NOT_FOUND = "Supplies Not Found";
    private static final String SUPPLY_FOUND = "Supply Found";
    private static final String SUPPLY_NOT_FOUND = "Supply Not Found";
    private static final String SUPPLY_ADDED = "Supply Added";
    private static final String SUPPLY_NOT_ADDED = "Supply Not Added";
    private static final String SUPPLY_UPDATED = "Supply Updated";
    private static final String SUPPLY_NOT_UPDATED = "Supply Not Updated";
    private static final String SUPPLY_DELETE_OPERATION = "Supply Delete Operation.";

    // Get all supplies with pagination
    @GetMapping
    public ResponseEntity<ApiResponse<Page<Supply>>> getAllSupplies(
            @RequestParam(defaultValue = DEFAULT_PAGE) int page,
            @RequestParam(defaultValue = DEFAULT_SIZE) int size) {

        Page<Supply> supplies = supplyService.getAllSupplies(page, size);
        return APIResponseForFoundOrNot.generateResponse(supplies, SUPPLIES_FOUND, SUPPLIES_NOT_FOUND);
    }

    // Get all supplies with details including item and location names
    @GetMapping("/all")
    public ResponseEntity<ApiResponse<Page<SupplyDTO>>> getAllSuppliesWithDetails(
            @RequestParam(defaultValue = DEFAULT_PAGE) int page,
            @RequestParam(defaultValue = DEFAULT_SIZE) int size,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String searchBy) {
        Page<SupplyDTO> suppliesWithDetails = supplyService.getAllSuppliesWithDetails(page, size, search, searchBy);
        return APIResponseForFoundOrNot.generateResponse(suppliesWithDetails, SUPPLIES_FOUND, SUPPLIES_NOT_FOUND);
    }

    // Get a supply by ID
    @GetMapping("/{supplyId}")
    public ResponseEntity<ApiResponse<Supply>> getSupplyById(@PathVariable String supplyId) {
        Supply supply = supplyService.getSupplyById(supplyId);
        return APIResponseForFoundOrNot.generateResponse(supply, SUPPLY_FOUND, SUPPLY_NOT_FOUND);
    }

    // Get supplies by item ID and location ID
    @GetMapping("byItem/{itemId}/{locationId}")
    public ResponseEntity<ApiResponse<SupplyDetailsResponse>> getSuppliesByItemAndLocation(
            @PathVariable String itemId,
            @PathVariable String locationId) {

        SupplyDetailsResponse supplies = supplyService.getSuppliesByItemIdAndLocationId(itemId, locationId);
        return APIResponseForFoundOrNot.generateResponse(supplies, SUPPLIES_FOUND, SUPPLIES_NOT_FOUND);
    }

    // Get supplies by type and location ID
    @GetMapping("byType/{supplyType}/{locationId}")
    public ResponseEntity<ApiResponse<SupplySummaryResponse>> getSuppliesByTypeAndLocation(
            @PathVariable String supplyType,
            @PathVariable String locationId) {

        SupplyType type = SupplyType.valueOf(supplyType.toUpperCase());
        SupplySummaryResponse supplies = supplyService.getSuppliesByTypeAndLocationId(type, locationId);
        return APIResponseForFoundOrNot.generateResponse(supplies, SUPPLIES_FOUND, SUPPLIES_NOT_FOUND);
    }

    // Add a new supply
    @PostMapping
    public ResponseEntity<ApiResponse<Supply>> addSupply(@RequestBody Supply supply) {
        Supply newSupply = supplyService.addSupply(supply);
        return APIResponseForFoundOrNot.generateResponse(newSupply, SUPPLY_ADDED, SUPPLY_NOT_ADDED);
    }

    // Update an existing supply
    @PatchMapping("/{supplyId}")
    public ResponseEntity<ApiResponse<Supply>> updateSupply(
            @PathVariable String supplyId, @RequestBody Supply supplyDetails) {

        Supply updatedSupply = supplyService.updateSupply(supplyId, supplyDetails);
        return APIResponseForFoundOrNot.generateResponse(updatedSupply, SUPPLY_UPDATED, SUPPLY_NOT_UPDATED);
    }

    // Delete a supply by ID
    @DeleteMapping("/{supplyId}")
    public ResponseEntity<ApiResponse<String>> deleteSupply(@PathVariable String supplyId) {
        String result = supplyService.deleteSupply(supplyId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new ApiResponse<>(true, SUPPLY_DELETE_OPERATION, result));
    }
}
