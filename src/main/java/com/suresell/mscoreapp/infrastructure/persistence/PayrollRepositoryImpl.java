package com.suresell.mscoreapp.infrastructure.persistence;

import com.suresell.mscoreapp.domain.model.PayrollEntity;
import com.suresell.mscoreapp.domain.repository.PayrollRepository;
import com.suresell.mscoreapp.infrastructure.persistence.jpa.PayrollJpaRepository;
import com.suresell.mscoreapp.shared.enums.PayrollStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class PayrollRepositoryImpl implements PayrollRepository {

    private final PayrollJpaRepository jpaRepository;

    @Override
    public PayrollEntity save(PayrollEntity payroll) {
        return jpaRepository.save(payroll);
    }

    @Override
    public Optional<PayrollEntity> findById(String id) {
        return jpaRepository.findById(id);
    }

    @Override
    public Optional<PayrollEntity> findByCode(String code) {
        return jpaRepository.findByCode(code);
    }

    @Override
    public List<PayrollEntity> findAll() {
        return jpaRepository.findAll();
    }

    @Override
    public List<PayrollEntity> findByStatus(PayrollStatus status) {
        return jpaRepository.findByStatus(status);
    }

    @Override
    public List<PayrollEntity> findByPaymentDateBetween(LocalDate startDate, LocalDate endDate) {
        return jpaRepository.findByPaymentDateBetween(startDate, endDate);
    }

    @Override
    public List<PayrollEntity> findByStatusAndPaymentDateBetween(PayrollStatus status, LocalDate startDate, LocalDate endDate) {
        return jpaRepository.findByStatusAndPaymentDateBetween(status, startDate, endDate);
    }

    @Override
    public void deleteById(String id) {
        jpaRepository.deleteById(id);
    }

    @Override
    public String generateNextCode() {
        String prefix = "NM-";
        Optional<String> lastCodeOpt = jpaRepository.findLastCodeByPrefix(prefix + "%");
        
        if (lastCodeOpt.isPresent()) {
            String lastCode = lastCodeOpt.get();
            try {
                int number = Integer.parseInt(lastCode.replace(prefix, ""));
                return String.format("%s%05d", prefix, number + 1);
            } catch (NumberFormatException e) {
                return prefix + "00001";
            }
        }
        return prefix + "00001";
    }
}
