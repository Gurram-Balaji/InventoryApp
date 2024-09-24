package com.App.fullStack.repositories;

import com.App.fullStack.pojos.Item;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ItemRepository extends MongoRepository<Item, String> {

    long count(); // Get total count of items

    Optional<Item> findByItemId(String itemId);

    void deleteByItemId(String itemId);

    boolean existsByItemId(String itemId);

    @Query("{ $or: [ " +
            "{ 'itemId': { $regex: ?0, $options: 'i' } }, " +
            "{ 'itemDescription': { $regex: ?0, $options: 'i' } }, " +
            "{ 'category': { $regex: ?0, $options: 'i' } }, " +
            "{ 'type': { $regex: ?0, $options: 'i' } }, " +
            "{ 'status': { $regex: ?0, $options: 'i' } }, " +
            "{ $expr: { $regexMatch: { input: { $toString: '$price' }, regex: ?0, options: 'i' } } } "+
            "] }")
    Page<Item> searchItemsByKeyword(String keyword, Pageable pageable);

    @Query(value="{ $or: [ " +
            "{ 'itemId': { $regex: ?0, $options: 'i' } }, " +
            "{ 'itemDescription': { $regex: ?0, $options: 'i' } }, " +
            "] }",  fields = "{ 'itemId' : 1, 'itemDescription':1 , '_id': 0}")
    Page<String> searchItemIdsByKeyword(String keyword, Pageable pageable);

    @Query(value = "{}", fields = "{ 'itemId' : 1, 'itemDescription':1 , '_id': 0}")
    Page<String> findDistinctItemIds(Pageable pageable);

    @Query(value="{ $or: [ " +
            "{ 'itemId': { $regex: ?0, $options: 'i' } }, " +
            "{ 'itemDescription': { $regex: ?0, $options: 'i' } }, " +
            "] }",  fields = "{ 'itemId' : 1,'_id': 0}")
    List<Item> searchItemIdsByKeywordGetIds(String search);
}

