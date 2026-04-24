package com.suresell.mscoreapp.application.usecase;

import com.suresell.mscoreapp.application.dto.CreateQrPaymentRequest;
import com.suresell.mscoreapp.application.dto.QrPaymentDto;
import com.suresell.mscoreapp.domain.model.QrPaymentEntity;
import com.suresell.mscoreapp.infrastructure.persistence.QrPaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class QrPaymentService {

    private final QrPaymentRepository qrPaymentRepository;

    @Transactional
    public QrPaymentDto registerOrUpdateQrPayment(CreateQrPaymentRequest request) {
        Optional<QrPaymentEntity> existingOpt = qrPaymentRepository.findByPaymentDate(request.paymentDate());

        QrPaymentEntity entity;
        if (existingOpt.isPresent()) {
            entity = existingOpt.get();
            entity.setAmount(request.amount());
            entity.setNotes(request.notes());
            entity.setRegisteredBy(request.registeredBy());
            log.info("Actualizando pago QR para la fecha: {}", request.paymentDate());
        } else {
            entity = new QrPaymentEntity();
            entity.setPaymentDate(request.paymentDate());
            entity.setAmount(request.amount());
            entity.setNotes(request.notes());
            entity.setRegisteredBy(request.registeredBy());
            log.info("Creando nuevo registro de pago QR para la fecha: {}", request.paymentDate());
        }

        QrPaymentEntity saved = qrPaymentRepository.save(entity);
        return mapToDto(saved);
    }

    @Transactional(readOnly = true)
    public Optional<QrPaymentDto> getQrPaymentByDate(LocalDate date) {
        return qrPaymentRepository.findByPaymentDate(date)
                .map(this::mapToDto);
    }

    private QrPaymentDto mapToDto(QrPaymentEntity entity) {
        return QrPaymentDto.builder()
                .id(entity.getId())
                .paymentDate(entity.getPaymentDate())
                .amount(entity.getAmount())
                .notes(entity.getNotes())
                .registeredBy(entity.getRegisteredBy())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
