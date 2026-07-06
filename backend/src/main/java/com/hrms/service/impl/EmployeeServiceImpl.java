package com.hrms.service.impl;

import com.hrms.dto.EmployeeRequest;
import com.hrms.dto.EmployeeResponse;
import com.hrms.entity.Employee;
import com.hrms.entity.User;
import com.hrms.repository.EmployeeRepository;
import com.hrms.repository.UserRepository;
import com.hrms.service.EmployeeService;
import com.hrms.util.ValidationUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public EmployeeResponse createEmployee(EmployeeRequest request, String currentUsername) {
        // Enforce validation to prevent blank values or placeholder 'string'
        ValidationUtils.validateNotPlaceholder(request.getNik(), "NIK");
        ValidationUtils.validateNotPlaceholder(request.getFirstName(), "First name");
        ValidationUtils.validateNotPlaceholder(request.getEmail(), "Email");
        ValidationUtils.validateOptionalNotPlaceholder(request.getLastName(), "Last name");
        ValidationUtils.validateOptionalNotPlaceholder(request.getPhone(), "Phone number");
        ValidationUtils.validateOptionalNotPlaceholder(request.getPosition(), "Position");
        ValidationUtils.validateOptionalNotPlaceholder(request.getDepartment(), "Department");

        // Verify NIK uniqueness
        if (employeeRepository.findByNikAndDeletedStatus(request.getNik(), 0).isPresent()) {
            throw new IllegalArgumentException("Employee with NIK " + request.getNik() + " already exists!");
        }

        // Verify Email uniqueness
        if (employeeRepository.findByEmailAndDeletedStatus(request.getEmail(), 0).isPresent()) {
            throw new IllegalArgumentException("Employee with Email " + request.getEmail() + " already exists!");
        }

        // Resolve optional User link
        User user = null;
        if (request.getUserId() != null) {
            user = userRepository.findByIdAndDeletedStatus(request.getUserId(), 0)
                    .orElseThrow(() -> new IllegalArgumentException("User not found or has been deleted!"));

            // Verify User is not already linked to another employee
            if (employeeRepository.findByUserIdAndDeletedStatus(request.getUserId(), 0).isPresent()) {
                throw new IllegalArgumentException("User is already linked to another employee!");
            }
        }

        Employee employee = Employee.builder()
                .nik(request.getNik())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .position(request.getPosition())
                .department(request.getDepartment())
                .joinDate(request.getJoinDate())
                .salary(request.getSalary())
                .user(user)
                .build();

        // Audit Trail Setup
        employee.setCreatedBy(currentUsername != null ? currentUsername : "SYSTEM");
        employee.setStatus(1);
        employee.setDeletedStatus(0);

        Employee savedEmployee = employeeRepository.save(employee);
        return mapToResponse(savedEmployee);
    }

    @Override
    @Transactional(readOnly = true)
    public EmployeeResponse getEmployeeById(Long id) {
        Employee employee = employeeRepository.findByIdAndDeletedStatus(id, 0)
                .orElseThrow(() -> new IllegalArgumentException("Employee not found or has been deleted!"));
        return mapToResponse(employee);
    }

    @Override
    @Transactional
    public EmployeeResponse updateEmployee(Long id, EmployeeRequest request, String currentUsername) {
        // Enforce validation to prevent blank values or placeholder 'string'
        ValidationUtils.validateNotPlaceholder(request.getNik(), "NIK");
        ValidationUtils.validateNotPlaceholder(request.getFirstName(), "First name");
        ValidationUtils.validateNotPlaceholder(request.getEmail(), "Email");
        ValidationUtils.validateOptionalNotPlaceholder(request.getLastName(), "Last name");
        ValidationUtils.validateOptionalNotPlaceholder(request.getPhone(), "Phone number");
        ValidationUtils.validateOptionalNotPlaceholder(request.getPosition(), "Position");
        ValidationUtils.validateOptionalNotPlaceholder(request.getDepartment(), "Department");

        Employee employee = employeeRepository.findByIdAndDeletedStatus(id, 0)
                .orElseThrow(() -> new IllegalArgumentException("Employee not found or has been deleted!"));

        // Verify NIK uniqueness if changed
        if (!employee.getNik().equals(request.getNik()) &&
                employeeRepository.findByNikAndDeletedStatus(request.getNik(), 0).isPresent()) {
            throw new IllegalArgumentException("Employee with NIK " + request.getNik() + " already exists!");
        }

        // Verify Email uniqueness if changed
        if (!employee.getEmail().equals(request.getEmail()) &&
                employeeRepository.findByEmailAndDeletedStatus(request.getEmail(), 0).isPresent()) {
            throw new IllegalArgumentException("Employee with Email " + request.getEmail() + " already exists!");
        }

        // Resolve optional User link if changed
        User user = employee.getUser();
        if (request.getUserId() != null) {
            if (user == null || !user.getId().equals(request.getUserId())) {
                user = userRepository.findByIdAndDeletedStatus(request.getUserId(), 0)
                        .orElseThrow(() -> new IllegalArgumentException("User not found or has been deleted!"));

                // Verify User is not already linked to another employee
                if (employeeRepository.findByUserIdAndDeletedStatus(request.getUserId(), 0).isPresent()) {
                    throw new IllegalArgumentException("User is already linked to another employee!");
                }
            }
        } else {
            user = null;
        }

        employee.setNik(request.getNik());
        employee.setFirstName(request.getFirstName());
        employee.setLastName(request.getLastName());
        employee.setEmail(request.getEmail());
        employee.setPhone(request.getPhone());
        employee.setPosition(request.getPosition());
        employee.setDepartment(request.getDepartment());
        employee.setJoinDate(request.getJoinDate());
        employee.setSalary(request.getSalary());
        employee.setUser(user);

        // Audit Trail Setup
        employee.setUpdatedBy(currentUsername != null ? currentUsername : "SYSTEM");
        employee.setUpdatedAt(LocalDateTime.now());

        Employee updatedEmployee = employeeRepository.save(employee);
        return mapToResponse(updatedEmployee);
    }

    @Override
    @Transactional
    public String deleteEmployee(Long id, String currentUsername) {
        Employee employee = employeeRepository.findByIdAndDeletedStatus(id, 0)
                .orElseThrow(() -> new IllegalArgumentException("Employee not found or has been deleted!"));

        // Soft Delete Setup
        employee.setDeletedStatus(1);
        employee.setDeletedBy(currentUsername != null ? currentUsername : "SYSTEM");
        employee.setDeletedAt(LocalDateTime.now());

        employeeRepository.save(employee);
        return "Employee deleted successfully!";
    }

    @Override
    @Transactional(readOnly = true)
    public List<EmployeeResponse> getAllEmployees() {
        return employeeRepository.findAllByDeletedStatus(0).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public EmployeeResponse getEmployeeByUserId(Long userId) {
        Employee employee = employeeRepository.findByUserIdAndDeletedStatus(userId, 0)
                .orElseThrow(() -> new IllegalArgumentException("Employee profile not found for this user!"));
        return mapToResponse(employee);
    }

    // Helper: Map Entity to Response DTO
    private EmployeeResponse mapToResponse(Employee employee) {
        EmployeeResponse.EmployeeResponseBuilder builder = EmployeeResponse.builder()
                .id(employee.getId())
                .nik(employee.getNik())
                .firstName(employee.getFirstName())
                .lastName(employee.getLastName())
                .email(employee.getEmail())
                .phone(employee.getPhone())
                .position(employee.getPosition())
                .department(employee.getDepartment())
                .joinDate(employee.getJoinDate())
                .salary(employee.getSalary())
                .createdBy(employee.getCreatedBy())
                .updatedBy(employee.getUpdatedBy())
                .createdAt(employee.getCreatedAt())
                .updatedAt(employee.getUpdatedAt())
                .status(employee.getStatus())
                .deletedStatus(employee.getDeletedStatus());

        if (employee.getUser() != null) {
            builder.userId(employee.getUser().getId())
                   .username(employee.getUser().getUsername());
        }

        return builder.build();
    }
}
