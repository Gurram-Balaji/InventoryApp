package com.App.fullStack.controller;

import com.App.fullStack.dto.ThresholdDTO;
import com.App.fullStack.pojos.AtpThreshold;
import com.App.fullStack.responseHandler.ApiResponse;
import com.App.fullStack.service.AtpThresholdService;
import com.App.fullStack.utility.APIResponseForFoundOrNot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/atpThresholds")
public class AtpThresholdController {

    @Autowired
    public AtpThresholdService atpThresholdService;

    @GetMapping
    public ResponseEntity<ApiResponse<Page<AtpThreshold>>> getAllAtpThresholds(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "8") int size) {
        Page<AtpThreshold> thresholds = atpThresholdService.getAllAtpThresholds(page, size);
        return APIResponseForFoundOrNot.generateResponse(thresholds, "ATP Thresholds Found", "No ATP Thresholds Found");
    }

    // getting all item and location with names
    @GetMapping("/all")
    public ResponseEntity<ApiResponse<Page<ThresholdDTO>>> getAllThresholdWithDetails(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "8") int size,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String searchBy) {
        Page<ThresholdDTO> ThresholdFullDetails = atpThresholdService.getAllDemandWithDetails(page, size, search, searchBy);
        return APIResponseForFoundOrNot.generateResponse(ThresholdFullDetails,
                "Threshold Found",
                "Threshold Not Found");
    }

    @GetMapping("/{thresholdId}")
    public ResponseEntity<ApiResponse<AtpThreshold>> getAtpThresholdById(@PathVariable String thresholdId) {
        AtpThreshold threshold = atpThresholdService.getAtpThresholdById(thresholdId);
        return APIResponseForFoundOrNot.generateResponse(threshold, "ATP Threshold Found", "ATP Threshold Not Found");
    }

    @GetMapping("/{itemId}/{locationId}")
    public ResponseEntity<ApiResponse<AtpThreshold>> getAtpThresholdByItemAndLocation(
            @PathVariable String itemId,
            @PathVariable String locationId) {
        AtpThreshold threshold = atpThresholdService.getAtpThresholdByItemAndLocation(itemId, locationId);
        return APIResponseForFoundOrNot.generateResponse(threshold, "ATP Threshold Found", "ATP Threshold Not Found");
    }

    @PostMapping
    public ResponseEntity<ApiResponse<AtpThreshold>> createAtpThreshold(@RequestBody AtpThreshold atpThreshold) {
        AtpThreshold createdThreshold = atpThresholdService.AddAtpThreshold(atpThreshold);
        return new ResponseEntity<>(new ApiResponse<>(true, "ATP Threshold Created Successfully.", createdThreshold),
                HttpStatus.CREATED);
    }

    @PatchMapping("/{thresholdId}")
    public ResponseEntity<ApiResponse<AtpThreshold>> updateAtpThresholdById(
            @PathVariable String thresholdId,
            @RequestBody AtpThreshold atpThresholdDetails) {
        AtpThreshold updatedThreshold = atpThresholdService.updateAtpThresholdById(thresholdId, atpThresholdDetails);
        return new ResponseEntity<>(new ApiResponse<>(true, "ATP Threshold Updated Successfully.", updatedThreshold),
                HttpStatus.OK);
    }

    @PatchMapping("/{itemId}/{locationId}")
    public ResponseEntity<ApiResponse<AtpThreshold>> updateAtpThresholdByItemAndLocation(
            @PathVariable String itemId,
            @PathVariable String locationId,
            @RequestBody AtpThreshold atpThresholdDetails) {
        AtpThreshold updatedThreshold = atpThresholdService.updateAtpThresholdByItemAndLocation(itemId, locationId,
                atpThresholdDetails);
        return new ResponseEntity<>(new ApiResponse<>(true, "ATP Threshold Updated Successfully.", updatedThreshold),
                HttpStatus.OK);
    }

    @DeleteMapping("/{thresholdId}")
    public ResponseEntity<ApiResponse<String>> deleteAtpThresholdById(@PathVariable String thresholdId) {
        String message = atpThresholdService.deleteAtpThresholdById(thresholdId);
        return new ResponseEntity<>(new ApiResponse<>(true, "Threshold Delete Operation.", message), HttpStatus.OK);
    }
}
