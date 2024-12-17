package com.hackathon.finservice.Repositories;

import com.hackathon.finservice.Entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByEmailIgnoreCase(String email);
    Optional<User> findByEmail(String username);
    User getReferenceByEmailIgnoreCase(String email);
}