package com.hrms.repository;

import com.hrms.entity.Menu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface MenuRepository extends JpaRepository<Menu, Long> {
    Optional<Menu> findByIdAndDeletedStatus(Long id, Integer deletedStatus);
    Optional<Menu> findByCodeAndDeletedStatus(String code, Integer deletedStatus);
    boolean existsByNameAndDeletedStatus(String name, Integer deletedStatus);
    boolean existsByCodeAndDeletedStatus(String code, Integer deletedStatus);
    List<Menu> findAllByDeletedStatus(Integer deletedStatus);
}
