package com.App.fullStack.repositories;

import com.App.fullStack.pojos.Location;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.List;


public interface LocationRepository extends MongoRepository<Location, String> {
    Optional<Location> findByLocationId(String locationId);

    boolean existsByLocationId(Object object);

     @Query("{ $or: [ " +
        "{ 'locationId': { $regex: ?0, $options: 'i' } }, " +
        "{ 'locationDesc': { $regex: ?0, $options: 'i' } }, " +
        "{ 'locationType': { $regex: ?0, $options: 'i' } }, " +
        "{ 'addressLine1': { $regex: ?0, $options: 'i' } }, " +
        "{ 'addressLine2': { $regex: ?0, $options: 'i' } }, " +
        "{ 'addressLine3': { $regex: ?0, $options: 'i' } }, " +
        "{ 'city': { $regex: ?0, $options: 'i' } }, " +
        "{ 'state': { $regex: ?0, $options: 'i' } }, " +
        "{ 'country': { $regex: ?0, $options: 'i' } }, " +
        "{ 'pinCode': { $regex: ?0, $options: 'i' } } " +
        "] }")
    Page<Location> searchLocationsByKeyword(@Param("keyword") String keyword, Pageable pageable);

@Query(value = "{}", fields = "{ 'locationId' : 1 , '_id': 0, 'locationDesc': 1}")
    List<String> findDistinctLocationIds();
}
