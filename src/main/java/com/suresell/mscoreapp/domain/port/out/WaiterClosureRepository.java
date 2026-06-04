package com.suresell.mscoreapp.domain.port.out;

import com.suresell.mscoreapp.domain.model.WaiterClosure;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface WaiterClosureRepository {
    Optional<WaiterClosure> findTopByWaiterIdOrderByClosedAtDesc(String waiterId);
    List<WaiterClosure> findByWaiterIdOrderByClosedAtDesc(String waiterId);
    WaiterClosure save(WaiterClosure closure);
}
