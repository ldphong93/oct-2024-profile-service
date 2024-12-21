package com.learn.oct2024.profile_service.repository;

import com.learn.oct2024.common.model.entity.AppUser;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends MongoRepository<AppUser, String> {

    List<AppUser> findByRole(String role);

    @Query("{ 'username' : ?0 }")
    Optional<AppUser> findByUsername(String username);
}
