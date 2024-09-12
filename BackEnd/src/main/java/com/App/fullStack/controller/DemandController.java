package com.App.fullStack.controller;

import com.App.fullStack.dto.DemandDTO;
import com.App.fullStack.dto.DemandDetailsResponse;
import com.App.fullStack.dto.DemandSummaryResponse;
import com.App.fullStack.pojos.Demand;
import com.App.fullStack.pojos.DemandType;
import com.App.fullStack.responseHandler.ApiResponse;
import com.App.fullStack.service.DemandService;
import com.App.fullStack.utility.APIResponseForFoundOrNot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/demand")
public class DemandController {

    @Autowired
    private DemandService demandService;

    // Constants for pagination defaults
    private static final String DEFAULT_PAGE = "0";
    private static final String DEFAULT_SIZE = "8";
    private static final String DEMANDS_FOUND = "Demands Found";
    private static final String DEMANDS_NOT_FOUND = "Demands Not Found";

    // Get all demands with pagination
    @GetMapping
    public ResponseEntity<ApiResponse<Page<Demand>>> getAllDemands(
            @RequestParam(defaultValue = DEFAULT_PAGE) int page,
            @RequestParam(defaultValue = DEFAULT_SIZE) int size) {
        
        Page<Demand> demands = demandService.getAllDemands(page, size);
        return APIResponseForFoundOrNot.generateResponse(demands, DEMANDS_FOUND, DEMANDS_NOT_FOUND);
    }

    // Get all demand details (with item and location names) with pagination and optional search
    @GetMapping("/all")
    public ResponseEntity<ApiResponse<Page<DemandDTO>>> getAllDemandWithDetails(
            @RequestParam(defaultValue = DEFAULT_PAGE) int page,
            @RequestParam(defaultValue = DEFAULT_SIZE) int size,
            @RequestParam(required = false) String search) {
        Page<DemandDTO> demandDetails = demandService.getAllDemandWithDetails(page, size, search);
        return APIResponseForFoundOrNot.generateResponse(demandDetails, DEMANDS_FOUND, DEMANDS_NOT_FOUND);
    }

    // Get demand by ID
    @GetMapping("/{demandId}")
    public ResponseEntity<ApiResponse<Demand>> getDemandById(@PathVariable String demandId) {
        Demand demand = demandService.getDemandById(demandId);
        return APIResponseForFoundOrNot.generateResponse(demand, DEMANDS_FOUND, DEMANDS_NOT_FOUND);
    }

    // Get demands by item and location
    @GetMapping("/byItem/{itemId}/{locationId}")
    public ResponseEntity<ApiResponse<DemandDetailsResponse>> getDemandsByItemAndLocation(
            @PathVariable String itemId, @PathVariable String locationId) {
        DemandDetailsResponse demandDetails = demandService.getDemandsByItemIdAndLocationId(itemId, locationId);
        return APIResponseForFoundOrNot.generateResponse(demandDetails, DEMANDS_FOUND, DEMANDS_NOT_FOUND);
    }

    // Get demands by demand type and location
    @GetMapping("/byType/{demandType}/{locationId}")
    public ResponseEntity<ApiResponse<DemandSummaryResponse>> getDemandsByTypeAndLocation(
            @PathVariable DemandType demandType, @PathVariable String locationId) {

        DemandSummaryResponse demandSummary = demandService.getDemandsByTypeAndLocationId(demandType, locationId);

        return APIResponseForFoundOrNot.generateResponse(demandSummary, DEMANDS_FOUND, DEMANDS_NOT_FOUND);
    }

    // Add a new demand
    @PostMapping
    public ResponseEntity<ApiResponse<Demand>> addDemand(@RequestBody Demand demand) {
        Demand addedDemand = demandService.addDemand(demand);
        return APIResponseForFoundOrNot.generateResponse(addedDemand, "Demand Added", "Demand Not Added");
    }

    // Update an existing demand
    @PatchMapping("/{demandId}")
    public ResponseEntity<ApiResponse<Demand>> updateDemand(
            @PathVariable String demandId, @RequestBody Demand demandDetails) {
        Demand updatedDemand = demandService.updateDemand(demandId, demandDetails);
        return APIResponseForFoundOrNot.generateResponse(updatedDemand, "Demand Updated", "Demand Not Updated");
    }

    // Delete a demand
    @DeleteMapping("/{demandId}")
    public ResponseEntity<ApiResponse<String>> deleteDemand(@PathVariable String demandId) {
        String result = demandService.deleteDemand(demandId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new ApiResponse<>(true, "Demand Delete Operation.", result));
    }
}
