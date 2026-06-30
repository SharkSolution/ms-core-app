package com.suresell.mscoreapp.infrastructure.web.adapter;

import com.suresell.mscoreapp.application.dto.EmployeeDto;
import com.suresell.mscoreapp.application.dto.EmployeeRequest;
import com.suresell.mscoreapp.application.usecase.ManageEmployeeUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/employees")
@RequiredArgsConstructor
@Tag(name = "Employees", description = "Maestra de empleados con su salario base por modalidad")
public class EmployeeController {

    private final ManageEmployeeUseCase useCase;

    @GetMapping
    @Operation(summary = "Listar todos los empleados")
    public List<EmployeeDto> getAll() {
        return useCase.getAllEmployees();
    }

    @GetMapping("/active")
    @Operation(summary = "Listar solo los empleados activos")
    public List<EmployeeDto> getActive() {
        return useCase.getActiveEmployees();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener un empleado por ID")
    public EmployeeDto getById(@PathVariable Long id) {
        return useCase.getEmployeeById(id);
    }

    @PostMapping
    @Operation(summary = "Crear un empleado")
    public ResponseEntity<EmployeeDto> create(@Valid @RequestBody EmployeeRequest request) {
        return new ResponseEntity<>(useCase.createEmployee(request), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Editar un empleado")
    public EmployeeDto update(@PathVariable Long id, @Valid @RequestBody EmployeeRequest request) {
        return useCase.updateEmployee(id, request);
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Activar o desactivar un empleado")
    public EmployeeDto updateStatus(@PathVariable Long id, @RequestParam boolean active) {
        return useCase.updateStatus(id, active);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar un empleado definitivamente")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        useCase.deleteEmployee(id);
        return ResponseEntity.noContent().build();
    }
}
