package com.suresell.mscoreapp.infrastructure.persistence;

import com.suresell.mscoreapp.application.usecase.ValeraEntityMapper;
import com.suresell.mscoreapp.application.dto.Valera;
import com.suresell.mscoreapp.shared.enums.ValeraStatus;
import com.suresell.mscoreapp.shared.enums.ValeraType;
import com.suresell.mscoreapp.domain.model.ValeraEntity;
import com.suresell.mscoreapp.domain.port.out.ValeraRepository;
import com.suresell.mscoreapp.infrastructure.persistence.jpa.ValeraPanacheRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class ValeraRepositoryImpl implements ValeraRepository {

    private static final Logger logger = LoggerFactory.getLogger(ValeraRepositoryImpl.class);

    private final ValeraEntityMapper mapper;
    private final ValeraPanacheRepository valeraJpaRepository;

    public ValeraRepositoryImpl(ValeraEntityMapper mapper, ValeraPanacheRepository valeraJpaRepository) {
        this.mapper = mapper;
        this.valeraJpaRepository = valeraJpaRepository;
    }

    @Override
    public List<Valera> findAllValeras() {
        logger.debug("Obteniendo todas las valeras");
        return valeraJpaRepository.findAll().stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Valera> findById(String id) {
        logger.debug("Buscando valera por ID: {}", id);
        return valeraJpaRepository.findById(id)
                .map(mapper::toDomain);
    }

    @Override
    public Optional<Valera> findByCode(String code) {
        logger.debug("Buscando valera por código: {}", code);
        return valeraJpaRepository.findByCode(code)
                .map(mapper::toDomain);
    }

    @Override
    public List<Valera> findByCustomerDocument(String customerDocument) {
        logger.debug("Buscando valeras del cliente: {}", customerDocument);
        return valeraJpaRepository.findByCustomerDocument(customerDocument).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Valera> findByStatus(ValeraStatus status) {
        logger.debug("Buscando valeras con estado: {}", status);
        return valeraJpaRepository.findByStatus(status).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Valera> findByType(ValeraType type) {
        logger.debug("Buscando valeras de tipo: {}", type);
        return valeraJpaRepository.findByType(type).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Valera> findActiveValeras() {
        logger.debug("Buscando valeras activas");
        return valeraJpaRepository.findByStatusAndExpirationDateGreaterThanEqual(ValeraStatus.ACTIVE, LocalDate.now())
                .stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Valera> findExpiringValeras(int daysAhead) {
        LocalDate limitDate = LocalDate.now().plusDays(daysAhead);
        logger.debug("Buscando valeras que vencen antes de: {}", limitDate);

        return valeraJpaRepository.findByStatusAndExpirationDateBetween(
                ValeraStatus.ACTIVE, LocalDate.now(), limitDate)
                .stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Valera> findExpiredValeras() {
        logger.debug("Buscando valeras vencidas");
        return valeraJpaRepository.findByExpirationDateLessThanAndStatusNot(LocalDate.now(), ValeraStatus.EXPIRED)
                .stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Valera> findByDateRange(LocalDate startDate, LocalDate endDate) {
        logger.debug("Buscando valeras en rango: {} - {}", startDate, endDate);
        return valeraJpaRepository.findByPurchaseDateBetweenOrderByPurchaseDateDesc(
                startDate, endDate)
                .stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public Valera create(Valera valera) {
        logger.debug("➕ Creando nueva valera: {}", valera.getCode());

        ValeraEntity newEntity = mapper.toEntity(valera);
        valeraJpaRepository.save(newEntity);

        logger.info("✅ Valera creada: {} para cliente: {}", valera.getCode(), valera.getCustomerName());
        return mapper.toDomain(newEntity);
    }

    @Override
    @Transactional
    public Valera update(Valera valera) {
        logger.debug("🔄 Actualizando valera: {}", valera.getCode());

        ValeraEntity existingEntity = valeraJpaRepository.findById(valera.getId())
                .orElseThrow(() -> new IllegalArgumentException("Valera not found: " + valera.getId()));

        mapper.updateEntity(existingEntity, valera);
        valeraJpaRepository.save(existingEntity);
        logger.info("✅ Valera actualizada: {}", valera.getCode());
        return mapper.toDomain(existingEntity);
    }

    @Override
    @Transactional
    public void deleteById(String id) {
        logger.debug("🗑️ Eliminando valera: {}", id);
        valeraJpaRepository.deleteById(id);
        logger.info("✅ Valera {} eliminada", id);
    }

    @Override
    public boolean existsByCode(String code) {
        return valeraJpaRepository.existsByCode(code);
    }

    @Override
    public long countByStatus(ValeraStatus status) {
        return valeraJpaRepository.countByStatus(status);
    }

    @Override
    public long countByCustomerDocument(String customerDocument) {
        return valeraJpaRepository.countByCustomerDocument(customerDocument);
    }
}
