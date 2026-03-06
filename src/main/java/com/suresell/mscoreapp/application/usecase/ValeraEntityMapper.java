package com.suresell.mscoreapp.application.usecase;

import com.suresell.mscoreapp.application.dto.Valera;
import com.suresell.mscoreapp.domain.model.ValeraEntity;
import org.mapstruct.*;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface ValeraEntityMapper {

    Valera toDomain(ValeraEntity entity);

    ValeraEntity toEntity(Valera domain);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", expression = "java(java.time.LocalDateTime.now())")
    void updateEntity(@MappingTarget ValeraEntity entity, Valera domain);
}
