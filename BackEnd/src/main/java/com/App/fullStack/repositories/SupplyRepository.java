package com.App.fullStack.repositories;

import com.App.fullStack.pojos.Supply;
import com.App.fullStack.pojos.SupplyType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SupplyRepository extends MongoRepository<Supply, String> {
    boolean existsByItemId(String itemId); // Check if any supply exists for the given itemId

    boolean existsByLocationId(String locationId);

    List<Supply> findByItemIdAndLocationId(String itemId, String locationId);

    List<Supply> findBySupplyTypeAndLocationId(SupplyType supplyType, String locationId);

    boolean existsByItemIdAndLocationIdAndSupplyType(String itemId, String locationId, SupplyType supplyType);

    Optional<Supply> findBySupplyId(String supplyId);

    List<Supply> findByItemIdAndLocationIdAndSupplyType(String itemId, String locationId, String supplyType);

    List<Supply> findByItemIdAndSupplyType(String itemId, String supplyType);

    List<Supply> findByItemIdAndLocationIdAndSupplyTypeIn(String itemId, String locationId,
                                                          List<String> supplyTypes);
    @Query(value = "{ 'locationId': ?0, 'supplyType': ?1 }", fields = "{ 'quantity': 1 }")
    List<Supply> findSuppliesByLocationIdAndSupplyType(String locationId, String supplyType);

    default int getTotalSupplyByLocationAndType(String locationId, String supplyType) {
        List<Supply> supplies = findSuppliesByLocationIdAndSupplyType(locationId, supplyType);
        return supplies.stream().mapToInt(Supply::getQuantity).sum();
    }

    Page<Supply> findByItemIdIn(List<String> itemIds, Pageable pageable);

    Page<Supply> findByLocationIdIn(List<String> locationsIds, Pageable pageable);

    @Query(value="{ 'supplyType': { $regex: ?0, $options: 'i' }  }")
    Page<Supply> findBySupplyType(String search, Pageable pageable);
}
