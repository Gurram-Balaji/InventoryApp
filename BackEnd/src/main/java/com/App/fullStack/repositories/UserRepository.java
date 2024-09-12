package com.App.fullStack.repositories;


import com.App.fullStack.pojos.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends MongoRepository<User,String> {
	@Query("{email :?0}")
	User findByEmail(String email);
	boolean existsByEmail(String email);
}
