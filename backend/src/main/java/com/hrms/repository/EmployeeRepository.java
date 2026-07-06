package com.hrms.repository;

import com.hrms.entity.Employee;
import com.hrms.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    Optional<Employee> findByIdAndDeletedStatus(Long id, Integer deletedStatus);

    Optional<Employee> findByNikAndDeletedStatus(String nik, Integer deletedStatus);

    Optional<Employee> findByEmailAndDeletedStatus(String email, Integer deletedStatus);

    Optional<Employee> findByUserAndDeletedStatus(User user, Integer deletedStatus);

    Optional<Employee> findByUserIdAndDeletedStatus(Long userId, Integer deletedStatus);

    List<Employee> findAllByDeletedStatus(Integer deletedStatus);
}
