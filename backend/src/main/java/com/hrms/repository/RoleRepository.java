package com.hrms.repository;

import com.hrms.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

// ==============================================================================
// ROLE REPOSITORY
// ==============================================================================
// Interface for database operations on the Role entity, filtering out soft-deleted records.
// ==============================================================================

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByIdAndDeletedStatus(Long id, Integer deletedStatus);
    List<Role> findAllByDeletedStatus(Integer deletedStatus);
    Optional<Role> findByNameAndDeletedStatus(String name, Integer deletedStatus);
    Boolean existsByNameAndDeletedStatus(String name, Integer deletedStatus);
}
