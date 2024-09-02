package com.App.fullStack.repositories;

import com.App.fullStack.pojos.Item;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ItemRepository extends MongoRepository<Item, String> {
    Optional<Item> findByItemId(String itemId); // Custom query to find by itemId

    void deleteByItemId(String itemId);

    boolean existsByItemId(String itemId);
}
