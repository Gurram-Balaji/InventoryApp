package com.App.fullStack.repositories;

import com.App.fullStack.pojos.Supply;
import com.App.fullStack.pojos.SupplyType;
import org.springframework.data.mongodb.repository.MongoRepository;
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
}
