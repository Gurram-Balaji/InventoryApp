package com.App.fullStack.repositories;

import com.App.fullStack.pojos.Location;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.Optional;

public interface LocationRepository extends MongoRepository<Location, String> {
    Optional<Location> findByLocationId(String locationId);
}
