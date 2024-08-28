package com.App.fullStack.repositories;

import com.App.fullStack.pojos.AtpThreshold;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AtpThresholdRepository extends MongoRepository<AtpThreshold, String> {

    Optional<AtpThreshold> findByItemIdAndLocationId(String itemId, String locationId);

    boolean existsByItemIdAndLocationId(String itemId, String locationId);

    Optional<AtpThreshold> findByThresholdId(String thresholdId);
}
