package com.suresell.mscoreapp.application.usecase;

import com.suresell.mscoreapp.application.dto.SupplyCategoryDto;
import com.suresell.mscoreapp.domain.model.SupplyCategory;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface SupplyCategoryEntityMapper {

    SupplyCategoryDto toDomain(SupplyCategory entity);

    SupplyCategory toEntity(SupplyCategoryDto domain);

    List<SupplyCategoryDto> toDomainList(List<SupplyCategory> entities);

    List<SupplyCategory> toEntityList(List<SupplyCategoryDto> domains);
}
