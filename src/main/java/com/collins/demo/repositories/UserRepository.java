package com.collins.demo.repositories;

import com.collins.demo.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByEmailIgnoreCase(String email);
    Boolean existsByEmail(String email);
}
