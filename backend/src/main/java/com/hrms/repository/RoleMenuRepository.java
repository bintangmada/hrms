package com.hrms.repository;

import com.hrms.entity.RoleMenu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface RoleMenuRepository extends JpaRepository<RoleMenu, Long> {
    Optional<RoleMenu> findByIdAndDeletedStatus(Long id, Integer deletedStatus);
    Optional<RoleMenu> findByRoleIdAndMenuIdAndDeletedStatus(Long roleId, Long menuId, Integer deletedStatus);
    List<RoleMenu> findAllByRoleIdAndDeletedStatus(Long roleId, Integer deletedStatus);
    List<RoleMenu> findAllByMenuIdAndDeletedStatus(Long menuId, Integer deletedStatus);
}
