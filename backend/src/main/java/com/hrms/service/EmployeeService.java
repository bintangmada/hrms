package com.hrms.service;

import com.hrms.dto.EmployeeRequest;
import com.hrms.dto.EmployeeResponse;
import java.util.List;

public interface EmployeeService {

    EmployeeResponse createEmployee(EmployeeRequest request, String currentUsername);

    EmployeeResponse getEmployeeById(Long id);

    EmployeeResponse updateEmployee(Long id, EmployeeRequest request, String currentUsername);

    String deleteEmployee(Long id, String currentUsername);

    List<EmployeeResponse> getAllEmployees();

    EmployeeResponse getEmployeeByUserId(Long userId);
}
