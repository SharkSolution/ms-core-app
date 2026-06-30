package com.suresell.mscoreapp.application.usecase;

import com.suresell.mscoreapp.application.dto.PayrollCreateRequest;
import com.suresell.mscoreapp.application.dto.PayrollDto;
import com.suresell.mscoreapp.application.mapper.PayrollMapper;
import com.suresell.mscoreapp.domain.model.PayrollEntity;
import com.suresell.mscoreapp.domain.repository.PayrollRepository;
import com.suresell.mscoreapp.shared.enums.PaymentMode;
import com.suresell.mscoreapp.shared.enums.PayrollStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ManagePayrollUseCase {

    private final PayrollRepository payrollRepository;
    private final PayrollMapper payrollMapper;

    @Transactional
    public PayrollDto createPayroll(PayrollCreateRequest request) {
        PayrollEntity entity = payrollMapper.toEntity(request);
        entity.setCode(payrollRepository.generateNextCode());
        
        entity.setTotalValue(calculateTotalValue(entity));
        
        PayrollEntity saved = payrollRepository.save(entity);
        return payrollMapper.toDto(saved);
    }

    private BigDecimal calculateTotalValue(PayrollEntity entity) {
        BigDecimal valuePerDay = entity.getValuePerDay() != null ? entity.getValuePerDay() : BigDecimal.ZERO;
        BigDecimal workedDays = entity.getWorkedDays() != null ? BigDecimal.valueOf(entity.getWorkedDays()) : BigDecimal.ZERO;
        PaymentMode mode = entity.getPaymentMode() != null ? entity.getPaymentMode() : PaymentMode.PER_SHIFT;

        // Base segun la modalidad de pago:
        //  - PER_SHIFT  -> valor por turno x turnos trabajados
        //  - PER_HOUR   -> valor por hora  x horas trabajadas (se reusa workedDays)
        //  - WITH_BENEFITS / WITHOUT_BENEFITS -> salario mensual fijo (no se multiplica)
        BigDecimal base;
        switch (mode) {
            case PER_SHIFT:
            case PER_HOUR:
                base = valuePerDay.multiply(workedDays);
                break;
            case WITH_BENEFITS:
            case WITHOUT_BENEFITS:
            default:
                base = valuePerDay;
                break;
        }

        BigDecimal commissions = entity.getCommissions() != null ? entity.getCommissions() : BigDecimal.ZERO;
        BigDecimal bonuses = entity.getBonuses() != null ? entity.getBonuses() : BigDecimal.ZERO;
        BigDecimal overtime = entity.getOvertimeValue() != null ? entity.getOvertimeValue() : BigDecimal.ZERO;

        return base.add(commissions).add(bonuses).add(overtime);
    }

    public List<PayrollDto> getPayrolls(PayrollStatus status, LocalDate startDate, LocalDate endDate) {
        List<PayrollEntity> entities;
        
        if (status != null && startDate != null && endDate != null) {
            entities = payrollRepository.findByStatusAndPaymentDateBetween(status, startDate, endDate);
        } else if (startDate != null && endDate != null) {
            entities = payrollRepository.findByPaymentDateBetween(startDate, endDate);
        } else if (status != null) {
            entities = payrollRepository.findByStatus(status);
        } else {
            entities = payrollRepository.findAll();
        }
        
        return entities.stream()
                .map(payrollMapper::toDto)
                .collect(Collectors.toList());
    }

    public PayrollDto getPayrollById(String id) {
        return payrollRepository.findById(id)
                .map(payrollMapper::toDto)
                .orElseThrow(() -> new RuntimeException("Payroll not found"));
    }

    @Transactional
    public PayrollDto approvePayroll(String id) {
        PayrollEntity entity = payrollRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Payroll not found"));
                
        if (entity.getStatus() != PayrollStatus.PENDING) {
            throw new RuntimeException("Only pending payrolls can be approved");
        }
        
        entity.setStatus(PayrollStatus.PAID);
        return payrollMapper.toDto(payrollRepository.save(entity));
    }

    @Transactional
    public PayrollDto cancelPayroll(String id) {
        PayrollEntity entity = payrollRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Payroll not found"));
                
        if (entity.getStatus() != PayrollStatus.PENDING) {
            throw new RuntimeException("Only pending payrolls can be cancelled");
        }
        
        entity.setStatus(PayrollStatus.CANCELLED);
        return payrollMapper.toDto(payrollRepository.save(entity));
    }

    @Transactional
    public void deletePayroll(String id) {
        PayrollEntity entity = payrollRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Payroll not found"));
                
        if (entity.getStatus() != PayrollStatus.PENDING) {
            throw new RuntimeException("Only pending payrolls can be deleted");
        }
        
        payrollRepository.deleteById(id);
    }
}
