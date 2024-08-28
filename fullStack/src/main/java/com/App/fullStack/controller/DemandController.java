package com.App.fullStack.controller;

import com.App.fullStack.dto.DemandDetailsResponse;
import com.App.fullStack.dto.DemandSummaryResponse;
import com.App.fullStack.exception.FoundException;
import com.App.fullStack.pojos.Demand;
import com.App.fullStack.pojos.DemandType;
import com.App.fullStack.responseHandler.ApiResponse;
import com.App.fullStack.service.DemandService;
import com.App.fullStack.utility.APIResponseForFoundOrNot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/demand")
public class DemandController {

    @Autowired
    private DemandService demandService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<Demand>>> getAllDemands() {
        return APIResponseForFoundOrNot.generateResponse(demandService.getAllDemands(), "Demands Found",
                "Demands Not Found");
    }

    @GetMapping("/{demandId}")
    public ResponseEntity<ApiResponse<Demand>> getDemandById(@PathVariable String demandId) {
        return APIResponseForFoundOrNot.generateResponse(demandService.getDemandById(demandId), "Demand Found",
                "Demand Not Found");
    }

    @GetMapping("byItem/{itemId}/{locationId}")
    public ResponseEntity<ApiResponse<DemandDetailsResponse>> getDemandsByItemAndLocation(
            @PathVariable String itemId,
            @PathVariable String locationId) {
        return APIResponseForFoundOrNot.generateResponse(
                demandService.getDemandsByItemIdAndLocationId(itemId, locationId),
                "Demands Found", "Demands Not Found");
    }

    @GetMapping("byType/{demandType}/{locationId}")
    public ResponseEntity<ApiResponse<DemandSummaryResponse>> getDemandsByTypeAndLocation(
            @PathVariable String demandType,
            @PathVariable String locationId) {

        if (!DemandType.isValid(demandType)) {
            throw new FoundException(
                    "Demands with demandType: " + demandType + " not found.");
        }
        DemandType type = DemandType.valueOf(demandType.toUpperCase());

        return APIResponseForFoundOrNot.generateResponse(
                demandService.getDemandsByTypeAndLocationId(type, locationId),
                "Demands Found", "Demands Not Found");
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Demand>> addDemand(@RequestBody Demand demand) {
        return APIResponseForFoundOrNot.generateResponse(demandService.addDemand(demand), "Demand Added",
                "Demand Not Added");
    }

    @PatchMapping("/{demandId}")
    public ResponseEntity<ApiResponse<Demand>> updateDemand(@PathVariable String demandId,
                                                            @RequestBody Demand demandDetails) {
        return APIResponseForFoundOrNot.generateResponse(demandService.updateDemand(demandId, demandDetails),
                "Demand Updated", "Demand Not Updated");
    }

    @DeleteMapping("/{demandId}")
    public ResponseEntity<ApiResponse<String>> deleteDemand(@PathVariable String demandId) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(new ApiResponse<>(true, "Demand Delete Operation.",
                        demandService.deleteDemand(demandId)));
    }
}
