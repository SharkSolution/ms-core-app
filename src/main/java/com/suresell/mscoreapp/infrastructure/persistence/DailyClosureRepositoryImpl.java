package com.suresell.mscoreapp.infrastructure.persistence;

import com.suresell.mscoreapp.domain.model.DailyClosureEntity;
import com.suresell.mscoreapp.domain.port.out.DailyClosureRepository;
import com.suresell.mscoreapp.infrastructure.persistence.jpa.DailyClosureJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class DailyClosureRepositoryImpl implements DailyClosureRepository {

    private final DailyClosureJpaRepository jpaRepository;

    @Override
    public List<DailyClosureEntity> findAllOrderByClosingTimeDesc() {
        return jpaRepository.findAllByOrderByClosingTimeDesc();
    }

    @Override
    public Optional<DailyClosureEntity> findById(UUID id) {
        return jpaRepository.findById(id);
    }

    @Override
    public DailyClosureEntity save(DailyClosureEntity closure) {
        return jpaRepository.save(closure);
    }
}
