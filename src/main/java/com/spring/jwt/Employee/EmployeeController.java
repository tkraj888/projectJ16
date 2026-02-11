package com.spring.jwt.Employee;

import com.spring.jwt.EmployeeFarmerSurvey.BaseResponseDTO1;
import com.spring.jwt.exception.BaseException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/employees")
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeService employeeService;

    // ================= GET BY EMPLOYEE ID =================
    @GetMapping("/{employeeId}")
    public ResponseEntity<BaseResponseDTO1<EmployeeResponseDTO>> getEmployeeById(
            @PathVariable Long employeeId) {

        return ResponseEntity.ok(
                new BaseResponseDTO1<>(
                        "200",
                        "Employee fetched successfully",
                        employeeService.getEmployeeById(employeeId)
                )
        );
    }

    // ================= GET BY USER ID =================
    @GetMapping("/user/{userId}")
    public ResponseEntity<BaseResponseDTO1<EmployeeResponseDTO>> getEmployeeByUserId(
            @PathVariable Long userId) {

        return ResponseEntity.ok(
                new BaseResponseDTO1<>(
                        "200",
                        "Employee fetched successfully",
                        employeeService.getEmployeeByUserId(userId)
                )
        );
    }

    // ================= GET ALL (PAGINATION) =================
    @GetMapping("/all")
    public ResponseEntity<BaseResponseDTO1<Page<EmployeeResponseDTO>>> getAllEmployees(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        return ResponseEntity.ok(
                new BaseResponseDTO1<>(
                        "200",
                        "Employees fetched successfully",
                        employeeService.getAllEmployees(page, size)
                )
        );
    }

    @PatchMapping("/user/{userId}")
    public ResponseEntity<BaseResponseDTO1<EmployeeResponseDTO>> patchEmployeeByUserId(
            @PathVariable Long userId,
            @RequestBody EmployeeUpdateRequestDTO dto) {

        try {
            return ResponseEntity.ok(
                    new BaseResponseDTO1<>(
                            "200",
                            "Employee updated successfully",
                            employeeService.patchEmployeeByUserId(userId, dto)
                    )
            );
        } catch (BaseException e) {
            throw e;
        }
    }

    // ================= PATCH ACCOUNT LOCK BY USER ID =================
    @PatchMapping("/user/{userId}/account-lock")
    public ResponseEntity<BaseResponseDTO1<EmployeeResponseDTO>> updateAccountLockStatus(
            @PathVariable Long userId,
            @RequestParam Boolean accountLocked) {

        return ResponseEntity.ok(
                new BaseResponseDTO1<>(
                        "200",
                        "Account lock status updated successfully",
                        employeeService.updateAccountLockStatusByUserId(userId, accountLocked)
                )
        );
    }

    @GetMapping("/getUsers")
    public ResponseEntity<BaseResponseDTO1<Page<UserListResponseDTO>>> getUsers(
            @RequestParam(required = false) String role,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(new BaseResponseDTO1<>(
                "200",
                "Account"+role+ "get successfully",
                employeeService.getUsers(role, page, size)
        ));
    }

}
