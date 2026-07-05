package com.hrms.repository;

import com.hrms.entity.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

// ==============================================================================
// USER ROLE JUNCTION REPOSITORY
// ==============================================================================
// Interface for database operations on the UserRole manual many-to-many junction entity.
// ==============================================================================

@Repository
public interface UserRoleRepository extends JpaRepository<UserRole, Long> {
    List<UserRole> findAllByUserIdAndDeletedStatus(Long userId, Integer deletedStatus);
    List<UserRole> findAllByRoleIdAndDeletedStatus(Long roleId, Integer deletedStatus);
    Boolean existsByUserIdAndRoleIdAndDeletedStatus(Long userId, Long roleId, Integer deletedStatus);
}
