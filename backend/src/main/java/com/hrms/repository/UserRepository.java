package com.hrms.repository;

import com.hrms.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

// ==============================================================================
// USER REPOSITORY
// ==============================================================================
// Interface for database operations on the User entity.
// ==============================================================================

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByIdAndDeletedStatus(Long id, Integer deletedStatus);
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    Boolean existsByUsername(String username);
    Boolean existsByEmail(String email);
    Optional<User> findByVerificationTokenAndDeletedStatus(String token, Integer deletedStatus);
}
