package com.suresell.mscoreapp.application.usecase;

import com.suresell.mscoreapp.application.dto.Valera;
import com.suresell.mscoreapp.shared.enums.ValeraStatus;
import com.suresell.mscoreapp.shared.enums.ValeraType;
import com.suresell.mscoreapp.domain.port.out.ValeraRepository;
import com.suresell.mscoreapp.application.dto.CreateValeraRequest;
import com.suresell.mscoreapp.application.dto.UseMealRequest;
import com.suresell.mscoreapp.application.dto.CustomerValerasResponse;
import com.suresell.mscoreapp.application.dto.ValeraStatsResponse;
import com.suresell.mscoreapp.application.dto.ValeraDto;
import com.suresell.mscoreapp.application.usecase.ValeraMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Component
public class ManageValeraUseCase {

    private static final Logger logger = LoggerFactory.getLogger(ManageValeraUseCase.class);

    private final ValeraRepository repository;
    private final ValeraMapper mapper;

    public ManageValeraUseCase(ValeraRepository repository, ValeraMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Transactional
    public Valera createValera(CreateValeraRequest request) {
        logger.info("Creando valera para cliente: {}", request.getCustomerName());

        ValeraType type = ValeraType.valueOf(request.getType().toUpperCase());

        Valera valera = Valera.createNew(
                request.getCustomerName(),
                request.getCustomerDocument(),
                request.getCustomerPhone(),
                type,
                request.getTotalMeals(),
                request.getUnitPrice(),
                request.getValidityDays(),
                request.getDiscountPercentage()
        );

        valera.setNotes(request.getNotes());

        return repository.create(valera);
    }

    @Transactional
    public Valera useMeal(UseMealRequest request) {
        logger.info("Usando comida de valera: {}", request.getValeraCode());

        Valera valera = repository.findByCode(request.getValeraCode())
                .orElseThrow(() -> new IllegalArgumentException("Valera no encontrada: " + request.getValeraCode()));

        valera.useMeal();

        if (request.getUsageNotes() != null) {
            String currentNotes = valera.getNotes() != null ? valera.getNotes() + " | " : "";
            valera.setNotes(currentNotes + "Uso: " + request.getUsageNotes());
        }

        return repository.update(valera);
    }

    public ValeraDto getValeraByCode(String code) {
        logger.debug("Consultando valera: {}", code);

        Valera valera = repository.findByCode(code)
                .orElseThrow(() -> new IllegalArgumentException("Valera no encontrada: " + code));

        return mapper.toDto(valera);
    }

    public CustomerValerasResponse getCustomerValeras(String customerDocument) {
        logger.debug("Consultando valeras del cliente: {}", customerDocument);

        List<Valera> valeras = repository.findByCustomerDocument(customerDocument);
        return mapper.toCustomerResponse(valeras, customerDocument);
    }

    public List<ValeraDto> getActiveValeras() {
        logger.debug("Consultando valeras activas");
        List<Valera> valeras = repository.findActiveValeras();
        return mapper.toDto(valeras);
    }

    public List<ValeraDto> getExpiringValeras(int daysAhead) {
        logger.debug("Consultando valeras que vencen en {} días", daysAhead);
        List<Valera> valeras = repository.findExpiringValeras(daysAhead);
        return mapper.toDto(valeras);
    }

    @Transactional
    public void expireOldValeras() {
        logger.info("Proceso de expiración automática de valeras");

        List<Valera> expiredValeras = repository.findExpiredValeras();

        for (Valera valera : expiredValeras) {
            valera.expire();
            repository.update(valera);
            logger.info("Valera {} marcada como expirada", valera.getCode());
        }

        logger.info("Proceso completado: {} valeras expiradas", expiredValeras.size());
    }

    @Transactional
    public Valera suspendValera(String valeraId, String reason) {
        logger.info("Suspendiendo valera: {}", valeraId);
        Valera valera = repository.findById(valeraId)
                .orElseThrow(() -> new IllegalArgumentException("Valera no encontrada: " + valeraId));
        valera.suspend(reason);
        return repository.update(valera);
    }

    @Transactional
    public Valera reactivateValera(String valeraId) {
        logger.info("Reactivando valera: {}", valeraId);

        Valera valera = repository.findById(valeraId)
                .orElseThrow(() -> new IllegalArgumentException("Valera no encontrada: " + valeraId));

        valera.reactivate();
        return repository.update(valera);
    }

    @Transactional
    public Valera cancelValera(String valeraId, String reason) {
        logger.info("Cancelando valera: {}", valeraId);

        Valera valera = repository.findById(valeraId)
                .orElseThrow(() -> new IllegalArgumentException("Valera no encontrada: " + valeraId));

        valera.cancel(reason);
        return repository.update(valera);
    }

    public ValeraStatsResponse getValeraStatistics() {
        logger.debug("Generando estadísticas de valeras");

        // Obtener todas las valeras para calcular estadísticas
        List<Valera> allValeras = repository.findAllValeras();

        ValeraStatsResponse stats = new ValeraStatsResponse();
        stats.setTotalValeras(allValeras.size());
        stats.setActiveValeras(repository.countByStatus(ValeraStatus.ACTIVE));
        stats.setUsedValeras(repository.countByStatus(ValeraStatus.USED));
        stats.setExpiredValeras(repository.countByStatus(ValeraStatus.EXPIRED));
        stats.setSuspendedValeras(repository.countByStatus(ValeraStatus.SUSPENDED));

        // Calcular estadísticas financieras
        stats.setTotalSales(calculateTotalSales(allValeras));
        stats.setTotalDiscounts(calculateTotalDiscounts(allValeras));
        stats.setAverageValeraValue(calculateAverageValue(allValeras));
        stats.setTotalOutstandingValue(calculateOutstandingValue(allValeras));

        return stats;
    }

    private BigDecimal calculateTotalSales(List<Valera> valeras) {
        return valeras.stream()
                .map(Valera::getFinalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal calculateTotalDiscounts(List<Valera> valeras) {
        return valeras.stream()
                .map(Valera::getDiscount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal calculateAverageValue(List<Valera> valeras) {
        if (valeras.isEmpty()) return BigDecimal.ZERO;
        BigDecimal total = calculateTotalSales(valeras);
        return total.divide(BigDecimal.valueOf(valeras.size()), 2, java.math.RoundingMode.HALF_UP);
    }

    private BigDecimal calculateOutstandingValue(List<Valera> valeras) {
        return valeras.stream()
                .filter(v -> v.getStatus() == ValeraStatus.ACTIVE)
                .map(v -> v.getUnitPrice().multiply(BigDecimal.valueOf(v.getRemainingMeals())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public List<ValeraDto> searchValeras(String searchTerm) {
        logger.debug("🔍 Buscando valeras con término: {}", searchTerm);

        // Buscar por código, nombre o documento
        List<Valera> results = repository.findAllValeras().stream()
                .filter(v ->
                        v.getCode().toLowerCase().contains(searchTerm.toLowerCase()) ||
                                v.getCustomerName().toLowerCase().contains(searchTerm.toLowerCase()) ||
                                v.getCustomerDocument().contains(searchTerm)
                )
                .collect(java.util.stream.Collectors.toList());

        return mapper.toDto(results);
    }

    public List<ValeraDto> getValerasByDateRange(LocalDate startDate, LocalDate endDate) {
        logger.debug("📅 Consultando valeras en rango: {} - {}", startDate, endDate);

        List<Valera> valeras = repository.findByDateRange(startDate, endDate);
        return mapper.toDto(valeras);
    }

    public List<ValeraDto> getValerasByType(ValeraType type) {
        logger.debug("🏷️ Consultando valeras de tipo: {}", type);

        List<Valera> valeras = repository.findByType(type);
        return mapper.toDto(valeras);
    }

    public List<ValeraDto> getValerasByStatus(ValeraStatus status) {
        logger.debug("📋 Consultando valeras con estado: {}", status);

        List<Valera> valeras = repository.findByStatus(status);
        return mapper.toDto(valeras);
    }

    @Transactional
    public void deleteValera(String valeraId) {
        logger.info("🗑️ Eliminando valera: {}", valeraId);

        // Verificar que existe antes de eliminar
        repository.findById(valeraId)
                .orElseThrow(() -> new IllegalArgumentException("Valera no encontrada: " + valeraId));

        repository.deleteById(valeraId);
    }

    public boolean validateValeraCode(String code) {
        logger.debug("✅ Validando código de valera: {}", code);
        return repository.existsByCode(code);
    }

    public long getCustomerValeraCount(String customerDocument) {
        logger.debug("🔢 Contando valeras del cliente: {}", customerDocument);
        return repository.countByCustomerDocument(customerDocument);
    }
}
