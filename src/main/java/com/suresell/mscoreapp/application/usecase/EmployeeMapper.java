package com.suresell.mscoreapp.application.usecase;

import com.suresell.mscoreapp.application.dto.EmployeeDto;
import com.suresell.mscoreapp.domain.model.EmployeeEntity;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface EmployeeMapper {
    EmployeeDto toDto(EmployeeEntity entity);
    List<EmployeeDto> toDtoList(List<EmployeeEntity> entities);
}
