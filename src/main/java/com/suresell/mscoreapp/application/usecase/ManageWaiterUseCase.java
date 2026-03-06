package com.suresell.mscoreapp.application.usecase;

import com.suresell.mscoreapp.application.dto.CreateWaiterRequest;
import com.suresell.mscoreapp.application.dto.WaiterDto;
import com.suresell.mscoreapp.domain.model.WaiterEntity;
import com.suresell.mscoreapp.domain.port.out.WaiterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ManageWaiterUseCase {

    private final WaiterRepository repository;
    private final WaiterMapper mapper;

    @Transactional(readOnly = true)
    public List<WaiterDto> getAllWaiters() {
        return mapper.toDtoList(repository.findAll());
    }

    @Transactional(readOnly = true)
    public List<WaiterDto> getActiveWaiters() {
        return mapper.toDtoList(repository.findByActive(true));
    }

    @Transactional
    public WaiterDto addWaiter(CreateWaiterRequest request) {
        WaiterEntity entity = new WaiterEntity();
        entity.setName(request.getName());
        entity.setActive(true);
        return mapper.toDto(repository.save(entity));
    }

    @Transactional
    public WaiterDto updateWaiterStatus(Long id, boolean active) {
        WaiterEntity entity = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Mesero no encontrado: " + id));
        entity.setActive(active);
        return mapper.toDto(repository.save(entity));
    }

    @Transactional
    public void removeWaiter(Long id) {
        repository.deleteById(id);
    }
}
