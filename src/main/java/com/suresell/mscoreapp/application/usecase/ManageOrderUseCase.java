package com.suresell.mscoreapp.application.usecase;

import com.suresell.mscoreapp.application.dto.OrderItemDto;
import com.suresell.mscoreapp.application.dto.OrderResponse;
import com.suresell.mscoreapp.domain.model.MenuProductEntity;
import com.suresell.mscoreapp.domain.model.Order;
import com.suresell.mscoreapp.domain.port.out.MenuProductRepository;
import com.suresell.mscoreapp.domain.port.out.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ManageOrderUseCase {

    private final OrderRepository orderRepository;
    private final MenuProductRepository productRepository;
    private final OrderMapper orderMapper;

    @Transactional(readOnly = true)
    public Page<OrderResponse> getOrders(Long idOrder, String pagerColor, String pagerNumber, Pageable pageable) {
        Specification<Order> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            
            if (idOrder != null) {
                predicates.add(cb.equal(root.get("idOrder"), idOrder));
            }
            if (pagerColor != null && !pagerColor.trim().isEmpty() && !pagerColor.equalsIgnoreCase("Cualquiera")) {
                predicates.add(cb.equal(cb.lower(root.get("pagerColor")), pagerColor.toLowerCase()));
            }
            if (pagerNumber != null && !pagerNumber.trim().isEmpty()) {
                predicates.add(cb.equal(root.get("pagerNumber"), pagerNumber));
            }
            
            return cb.and(predicates.toArray(new Predicate[0]));
        };
        
        Page<OrderResponse> responsePage = orderRepository.findAll(spec, pageable).map(orderMapper::toResponse);
        
        // Cache temporal de nombres de productos para esta página
        Map<String, String> productNames = new HashMap<>();
        
        responsePage.forEach(order -> {
            if (order.getItems() != null) {
                order.getItems().forEach(item -> {
                    String id = item.getProductId();
                    if (id != null) {
                        String name = productNames.computeIfAbsent(id, pid -> 
                            productRepository.findById(pid)
                                .map(MenuProductEntity::getName)
                                .orElse("Producto desconocido")
                        );
                        item.setProductName(name);
                    }
                });
            }
        });
        
        return responsePage;
    }
}
