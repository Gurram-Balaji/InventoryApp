package com.App.fullStack.controller;

import com.App.fullStack.dto.SupplyDetailsResponse;
import com.App.fullStack.dto.SupplySummaryResponse;
import com.App.fullStack.exception.FoundException;
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

        @GetMapping
        public ResponseEntity<ApiResponse<Page<Supply>>> getAllSupplies(@RequestParam(defaultValue = "0") int page,
                        @RequestParam(defaultValue = "10") int size) {
                return APIResponseForFoundOrNot.generateResponse(supplyService.getAllSupplies(page, size),
                                "Supplies Found",
                                "Supplies Not Found");
        }

        @GetMapping("/{supplyId}")
        public ResponseEntity<ApiResponse<Supply>> getSupplyById(@PathVariable String supplyId) {
                return APIResponseForFoundOrNot.generateResponse(supplyService.getSupplyById(supplyId), "Supply Found",
                                "Supply Not Found");
        }

        @GetMapping("byItem/{itemId}/{locationId}")
        public ResponseEntity<ApiResponse<SupplyDetailsResponse>> getSuppliesByItemAndLocation(
                        @PathVariable String itemId,
                        @PathVariable String locationId) {
                System.out.println(itemId + "  " + locationId);
                return APIResponseForFoundOrNot.generateResponse(
                                supplyService.getSuppliesByItemIdAndLocationId(itemId, locationId),
                                "Supplies Found", "Supplies Not Found");
        }

        @GetMapping("byType/{supplyType}/{locationId}")
        public ResponseEntity<ApiResponse<SupplySummaryResponse>> getSuppliesByTypeAndLocation(
                        @PathVariable String supplyType,
                        @PathVariable String locationId) {

                if (!SupplyType.isValid(supplyType)) {
                        throw new FoundException(
                                        "Supplies with supplyType: " + supplyType + " not found.");

                }
                SupplyType type = SupplyType.valueOf(supplyType.toUpperCase());

                return APIResponseForFoundOrNot.generateResponse(
                                supplyService.getSuppliesByTypeAndLocationId(type, locationId),
                                "Supplies Found", "Supplies Not Found");
        }

        @PostMapping
        public ResponseEntity<ApiResponse<Supply>> addSupply(@RequestBody Supply supply) {
                return APIResponseForFoundOrNot.generateResponse(supplyService.addSupply(supply), "Supply Added",
                                "Supply Not Added");
        }

        @PatchMapping("/{supplyId}")
        public ResponseEntity<ApiResponse<Supply>> updateSupply(@PathVariable String supplyId,
                        @RequestBody Supply supplyDetails) {
                return APIResponseForFoundOrNot.generateResponse(supplyService.updateSupply(supplyId, supplyDetails),
                                "Supply Updated", "Supply Not Updated");
        }

        @DeleteMapping("/{supplyId}")
        public ResponseEntity<ApiResponse<String>> deleteSupply(@PathVariable String supplyId) {
                return ResponseEntity.status(HttpStatus.OK)
                                .body(new ApiResponse<>(true, "Supply Delete Operation.",
                                                supplyService.deleteSupply(supplyId)));
        }
}
