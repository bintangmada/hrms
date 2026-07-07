package com.hrms.service.impl;

import com.hrms.dto.EmployeeRequest;
import com.hrms.dto.EmployeeResponse;
import com.hrms.entity.Employee;
import com.hrms.entity.User;
import com.hrms.repository.EmployeeRepository;
import com.hrms.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class EmployeeServiceImplTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private EmployeeServiceImpl employeeService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testCreateEmployee_Success() {
        EmployeeRequest request = EmployeeRequest.builder()
                .nik("123456")
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .phone("08123456789")
                .position("Software Engineer")
                .department("IT")
                .joinDate(LocalDate.now())
                .salary(BigDecimal.valueOf(10000000))
                .userId(1L)
                .build();

        User user = User.builder().id(1L).username("johndoe").build();

        Employee mockSavedEmployee = Employee.builder()
                .id(100L)
                .nik("123456")
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .phone("08123456789")
                .position("Software Engineer")
                .department("IT")
                .joinDate(LocalDate.now())
                .salary(BigDecimal.valueOf(10000000))
                .userId(1L)
                .build();
        mockSavedEmployee.setCreatedBy("admin");
        mockSavedEmployee.setDeletedStatus(0);

        when(employeeRepository.findByNikAndDeletedStatus("123456", 0)).thenReturn(Optional.empty());
        when(employeeRepository.findByEmailAndDeletedStatus("john.doe@example.com", 0)).thenReturn(Optional.empty());
        when(userRepository.findByIdAndDeletedStatus(1L, 0)).thenReturn(Optional.of(user));
        when(employeeRepository.findByUserIdAndDeletedStatus(1L, 0)).thenReturn(Optional.empty());
        when(employeeRepository.save(any(Employee.class))).thenReturn(mockSavedEmployee);

        EmployeeResponse response = employeeService.createEmployee(request, "admin");

        assertNotNull(response);
        assertEquals(100L, response.getId());
        assertEquals("123456", response.getNik());
        assertEquals("John", response.getFirstName());
        assertEquals("johndoe", response.getUsername());
        verify(employeeRepository, times(1)).save(any(Employee.class));
    }

    @Test
    public void testCreateEmployee_DuplicateNik() {
        EmployeeRequest request = EmployeeRequest.builder()
                .nik("123456")
                .firstName("John")
                .email("john.doe@example.com")
                .build();

        when(employeeRepository.findByNikAndDeletedStatus("123456", 0)).thenReturn(Optional.of(new Employee()));

        assertThrows(IllegalArgumentException.class, () -> employeeService.createEmployee(request, "admin"));
        verify(employeeRepository, never()).save(any(Employee.class));
    }

    @Test
    public void testCreateEmployee_DuplicateEmail() {
        EmployeeRequest request = EmployeeRequest.builder()
                .nik("123456")
                .firstName("John")
                .email("john.doe@example.com")
                .build();

        when(employeeRepository.findByNikAndDeletedStatus("123456", 0)).thenReturn(Optional.empty());
        when(employeeRepository.findByEmailAndDeletedStatus("john.doe@example.com", 0)).thenReturn(Optional.of(new Employee()));

        assertThrows(IllegalArgumentException.class, () -> employeeService.createEmployee(request, "admin"));
    }

    @Test
    public void testCreateEmployee_UserAlreadyLinked() {
        EmployeeRequest request = EmployeeRequest.builder()
                .nik("123456")
                .firstName("John")
                .email("john.doe@example.com")
                .userId(1L)
                .build();

        User user = User.builder().id(1L).username("johndoe").build();

        when(employeeRepository.findByNikAndDeletedStatus("123456", 0)).thenReturn(Optional.empty());
        when(employeeRepository.findByEmailAndDeletedStatus("john.doe@example.com", 0)).thenReturn(Optional.empty());
        when(userRepository.findByIdAndDeletedStatus(1L, 0)).thenReturn(Optional.of(user));
        when(employeeRepository.findByUserIdAndDeletedStatus(1L, 0)).thenReturn(Optional.of(new Employee()));

        assertThrows(IllegalArgumentException.class, () -> employeeService.createEmployee(request, "admin"));
    }

    @Test
    public void testCreateEmployee_PlaceholderValue() {
        EmployeeRequest request = EmployeeRequest.builder()
                .nik("string")
                .firstName("John")
                .email("john.doe@example.com")
                .build();

        assertThrows(IllegalArgumentException.class, () -> employeeService.createEmployee(request, "admin"));
    }

    @Test
    public void testGetEmployeeById_Success() {
        Employee employee = Employee.builder()
                .id(1L)
                .nik("123")
                .firstName("Jane")
                .email("jane@example.com")
                .build();

        when(employeeRepository.findByIdAndDeletedStatus(1L, 0)).thenReturn(Optional.of(employee));

        EmployeeResponse response = employeeService.getEmployeeById(1L);

        assertNotNull(response);
        assertEquals("123", response.getNik());
        assertEquals("Jane", response.getFirstName());
    }

    @Test
    public void testGetEmployeeById_NotFound() {
        when(employeeRepository.findByIdAndDeletedStatus(1L, 0)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> employeeService.getEmployeeById(1L));
    }

    @Test
    public void testUpdateEmployee_Success() {
        EmployeeRequest request = EmployeeRequest.builder()
                .nik("123456")
                .firstName("John Updated")
                .email("john.updated@example.com")
                .userId(2L)
                .build();

        User newUser = User.builder().id(2L).username("newuser").build();

        Employee existing = Employee.builder()
                .id(100L)
                .nik("123")
                .firstName("John")
                .email("john@example.com")
                .userId(1L)
                .build();

        Employee updated = Employee.builder()
                .id(100L)
                .nik("123456")
                .firstName("John Updated")
                .email("john.updated@example.com")
                .userId(2L)
                .build();

        when(employeeRepository.findByIdAndDeletedStatus(100L, 0)).thenReturn(Optional.of(existing));
        when(employeeRepository.findByNikAndDeletedStatus("123456", 0)).thenReturn(Optional.empty());
        when(employeeRepository.findByEmailAndDeletedStatus("john.updated@example.com", 0)).thenReturn(Optional.empty());
        when(userRepository.findByIdAndDeletedStatus(2L, 0)).thenReturn(Optional.of(newUser));
        when(employeeRepository.findByUserIdAndDeletedStatus(2L, 0)).thenReturn(Optional.empty());
        when(employeeRepository.save(any(Employee.class))).thenReturn(updated);

        EmployeeResponse response = employeeService.updateEmployee(100L, request, "admin");

        assertNotNull(response);
        assertEquals("John Updated", response.getFirstName());
        assertEquals("newuser", response.getUsername());
        verify(employeeRepository, times(1)).save(any(Employee.class));
    }

    @Test
    public void testDeleteEmployee_Success() {
        Employee existing = Employee.builder().id(1L).nik("123").build();
        when(employeeRepository.findByIdAndDeletedStatus(1L, 0)).thenReturn(Optional.of(existing));

        String result = employeeService.deleteEmployee(1L, "admin");

        assertEquals("Employee deleted successfully!", result);
        assertEquals(1, existing.getDeletedStatus());
        assertEquals("admin", existing.getDeletedBy());
        verify(employeeRepository, times(1)).save(existing);
    }

    @Test
    public void testGetAllEmployees() {
        Employee emp1 = Employee.builder().id(1L).nik("1").email("a@a.com").firstName("a").build();
        Employee emp2 = Employee.builder().id(2L).nik("2").email("b@b.com").firstName("b").build();

        when(employeeRepository.findAllByDeletedStatus(0)).thenReturn(List.of(emp1, emp2));

        List<EmployeeResponse> list = employeeService.getAllEmployees();

        assertEquals(2, list.size());
        assertEquals("a", list.get(0).getFirstName());
        assertEquals("b", list.get(1).getFirstName());
    }

    @Test
    public void testGetEmployeeByUserId_Success() {
        Employee employee = Employee.builder().id(5L).nik("99").email("test@test.com").firstName("Test").build();
        when(employeeRepository.findByUserIdAndDeletedStatus(10L, 0)).thenReturn(Optional.of(employee));

        EmployeeResponse response = employeeService.getEmployeeByUserId(10L);

        assertNotNull(response);
        assertEquals("Test", response.getFirstName());
    }

    @Test
    public void testGetEmployeeByUserId_NotFound() {
        when(employeeRepository.findByUserIdAndDeletedStatus(10L, 0)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> employeeService.getEmployeeByUserId(10L));
    }
}
