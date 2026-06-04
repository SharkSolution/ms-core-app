package com.suresell.mscoreapp.infrastructure.persistence;

import com.suresell.mscoreapp.domain.model.WaiterClosure;
import com.suresell.mscoreapp.domain.port.out.WaiterClosureRepository;
import com.suresell.mscoreapp.infrastructure.persistence.jpa.WaiterClosureJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class WaiterClosureRepositoryImpl implements WaiterClosureRepository {

    private final WaiterClosureJpaRepository jpaRepository;

    public WaiterClosureRepositoryImpl(WaiterClosureJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Optional<WaiterClosure> findTopByWaiterIdOrderByClosedAtDesc(String waiterId) {
        return jpaRepository.findTopByWaiterIdOrderByClosedAtDesc(waiterId);
    }

    @Override
    public List<WaiterClosure> findByWaiterIdOrderByClosedAtDesc(String waiterId) {
        return jpaRepository.findByWaiterIdOrderByClosedAtDesc(waiterId);
    }

    @Override
    public WaiterClosure save(WaiterClosure closure) {
        return jpaRepository.save(closure);
    }
}
