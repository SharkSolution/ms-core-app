package com.suresell.mscoreapp.application.usecase;

import com.suresell.mscoreapp.application.dto.ClosureResponse;
import com.suresell.mscoreapp.domain.model.DailyClosureEntity;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface DailyClosureMapper {
    ClosureResponse toResponse(DailyClosureEntity entity);
    List<ClosureResponse> toResponseList(List<DailyClosureEntity> entities);
}
