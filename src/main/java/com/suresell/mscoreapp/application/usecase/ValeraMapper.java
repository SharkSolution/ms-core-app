package com.suresell.mscoreapp.application.usecase;

import com.suresell.mscoreapp.application.dto.Valera;
import com.suresell.mscoreapp.application.dto.CustomerValerasResponse;
import com.suresell.mscoreapp.application.dto.ValeraDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface ValeraMapper {

    @Mapping(target = "type", source = "type.displayName")
    @Mapping(target = "typeDescription", source = "type.displayName")
    @Mapping(target = "status", source = "status.displayName")
    @Mapping(target = "statusDescription", source = "status.displayName")
    @Mapping(target = "purchaseDate", source = "purchaseDate", dateFormat = "yyyy-MM-dd")
    @Mapping(target = "expirationDate", source = "expirationDate", dateFormat = "yyyy-MM-dd")
    @Mapping(target = "usedMeals", expression = "java(valera.getUsedMeals())")
    @Mapping(target = "utilizationPercentage", expression = "java(valera.getUtilizationPercentage())")
    @Mapping(target = "savingsAmount", expression = "java(valera.getSavingsAmount())")
    @Mapping(target = "savingsPercentage", expression = "java(valera.getSavingsPercentage())")
    @Mapping(target = "canUse", expression = "java(valera.canUse())")
    @Mapping(target = "expired", expression = "java(valera.isExpired())")
    @Mapping(target = "daysUntilExpiration", expression = "java(calculateDaysUntilExpiration(valera.getExpirationDate()))")
    ValeraDto toDto(Valera valera);

    List<ValeraDto> toDto(List<Valera> valeras);

    default CustomerValerasResponse toCustomerResponse(List<Valera> valeras, String customerDocument) {
        CustomerValerasResponse response = new CustomerValerasResponse();
        response.setCustomerDocument(customerDocument);

        if (!valeras.isEmpty()) {
            response.setCustomerName(valeras.get(0).getCustomerName());
        }

        response.setValeras(toDto(valeras));
        response.setTotalValeras(valeras.size());
        response.setActiveValeras((int) valeras.stream()
                .filter(v -> v.getStatus().name().equals("ACTIVE"))
                .count());

        response.setTotalSpent(valeras.stream()
                .map(Valera::getFinalPrice)
                .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add));

        response.setTotalSavings(valeras.stream()
                .map(Valera::getDiscount)
                .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add));

        return response;
    }

    default int calculateDaysUntilExpiration(LocalDate expirationDate) {
        LocalDate today = LocalDate.now();
        if (expirationDate.isBefore(today)) {
            return 0; // Ya expiró
        }
        return (int) ChronoUnit.DAYS.between(today, expirationDate);
    }
}
