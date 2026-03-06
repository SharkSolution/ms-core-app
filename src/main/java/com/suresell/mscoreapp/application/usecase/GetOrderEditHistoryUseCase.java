package com.suresell.mscoreapp.application.usecase;

import com.suresell.mscoreapp.application.dto.OrderEditHistoryDto;
import com.suresell.mscoreapp.domain.model.OrderEditHistoryEntity;
import com.suresell.mscoreapp.domain.port.out.OrderEditHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GetOrderEditHistoryUseCase {

    private static final Logger logger = LoggerFactory.getLogger(GetOrderEditHistoryUseCase.class);

    private final OrderEditHistoryRepository repository;
    private final OrderEditHistoryMapper mapper;

    @Transactional(readOnly = true)
    public Page<OrderEditHistoryDto> getHistory(Long orderId, Pageable pageable) {
        logger.debug("Consultando historial de ediciones. orderId: {}, pageable: {}", orderId, pageable);
        
        Page<OrderEditHistoryEntity> entities;
        if (orderId != null) {
            entities = repository.findByOrderId(orderId, pageable);
        } else {
            entities = repository.findAll(pageable);
        }

        return entities.map(mapper::toDto);
    }
}
