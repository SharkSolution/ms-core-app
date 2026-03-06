package com.suresell.mscoreapp.domain.port.out;

import com.suresell.mscoreapp.domain.model.DailyClosureEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DailyClosureRepository {
    List<DailyClosureEntity> findAllOrderByClosingTimeDesc();
    Optional<DailyClosureEntity> findById(UUID id);
    DailyClosureEntity save(DailyClosureEntity closure);
}
