package com.App.fullStack.repositories;

import com.App.fullStack.pojos.Demand;
import com.App.fullStack.pojos.DemandType;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DemandRepository extends MongoRepository<Demand, String> {
    boolean existsByItemId(String itemId); // Check if any demand exists for the given itemId

    boolean existsByLocationId(String locationId);

    List<Demand> findByItemIdAndLocationId(String itemId, String locationId);

    List<Demand> findByDemandTypeAndLocationId(DemandType demandType, String locationId);

    Optional<Demand> findByDemandId(String demandId);

    boolean existsByItemIdAndLocationIdAndDemandType(String itemId, String locationId, DemandType demandType);

    List<Demand> findByItemIdAndLocationIdAndDemandType(String itemId, String locationId, String demandType);

    List<Demand> findByItemIdAndDemandType(String itemId, String demandType);

    List<Demand> findByItemIdAndLocationIdAndDemandTypeIn(String itemId, String locationId,
                                                          List<String> demandTypes);
}
