package com.suresell.mscoreapp.application.usecase;

import com.suresell.mscoreapp.application.dto.CreateShoppingItemRequest;
import com.suresell.mscoreapp.application.dto.ShoppingItem;
import com.suresell.mscoreapp.application.dto.ShoppingListResponse;
import com.suresell.mscoreapp.domain.model.SupplyCategory;
import com.suresell.mscoreapp.domain.port.out.ISupplyCategoryRepository;
import com.suresell.mscoreapp.domain.port.out.ShoppingListRepository;
import com.suresell.mscoreapp.shared.enums.ShoppingItemStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Component
public class ManageShoppingListUseCase {

    private final ShoppingListRepository repository;
    private final ShoppingListMapper mapper;
    private final ISupplyCategoryRepository categoryRepository;

    public ManageShoppingListUseCase(ShoppingListRepository repository, 
                                    ShoppingListMapper mapper,
                                    ISupplyCategoryRepository categoryRepository) {
        this.repository = repository;
        this.mapper = mapper;
        this.categoryRepository = categoryRepository;
    }

    public ShoppingListResponse getActiveShoppingList() {
        List<ShoppingItem> itemsPending = repository.findByStatus(ShoppingItemStatus.PENDING);
        List<ShoppingItem> itemsPurchase = repository.findByStatus(ShoppingItemStatus.PURCHASED);

        List<ShoppingItem> itemsConcated = new ArrayList<>();
        itemsConcated.addAll(itemsPurchase);
        itemsConcated.addAll(itemsPending);
        return mapper.toResponse(itemsConcated);
    }

    public ShoppingItem addItem(CreateShoppingItemRequest request) throws Exception {
        String categoryName = "Sin categoría";
        
        if (request.getCategoryId() != null) {
            try {
                Object rawId = request.getCategoryId();
                Long id = null;
                
                if (rawId instanceof Number) {
                    id = ((Number) rawId).longValue();
                } else if (rawId instanceof String) {
                    try {
                        id = Long.parseLong((String) rawId);
                    } catch (NumberFormatException nfe) {
                        // Es un nombre de categoría directamente
                        categoryName = (String) rawId;
                    }
                }
                
                if (id != null) {
                    Optional<SupplyCategory> category = categoryRepository.findById(id);
                    if (category.isPresent()) {
                        categoryName = category.get().getName();
                    } else {
                        // Si el ID no existe, usamos el ID como nombre temporal
                        categoryName = "ID: " + id;
                    }
                }
            } catch (Exception e) {
                categoryName = String.valueOf(request.getCategoryId());
            }
        }

        ShoppingItem item = new ShoppingItem(
                request.getName(),
                categoryName,
                request.getUnit(),
                request.getCurrentStock(),
                request.getMinStock()
        );
        try{
            return repository.save(item);
        }catch (Exception e) {
            throw new IllegalArgumentException("Error al guardar el item de la lista de compras.");
        }
    }

    /**
     * Actualiza la cantidad de un item de la lista de compras.
     *
     * <h3>Por que ya no se traga el fallo</h3>
     *
     * La version anterior era:
     *
     * <pre>
     * try { repository.save(item); }
     * catch (Exception e) { System.out.println(e.getMessage()); }
     * </pre>
     *
     * Un metodo {@code void} que no relanza: si el guardado fallaba, <b>quien
     * llamo creia que se habia guardado</b> y el inventario quedaba con una
     * cantidad distinta de la que el usuario vio confirmada. Y con
     * {@code System.out} en vez de un logger, en el proveedor de despliegue
     * podia no aparecer ni el mensaje.
     *
     * <p>Era el fallback mas silencioso del inventario de
     * `discovery/FALLBACK-SILENCIOSO.md` (I1) y el mas barato de arreglar: el
     * fallo se propaga, que es lo unico que hace que el llamador —y el usuario—
     * se enteren.
     */
    public void updateItemQuantity(String itemId, BigDecimal quantity) {
        ShoppingItem item = repository.findById(itemId)
                .orElseThrow(() -> new IllegalArgumentException("Item not found"));

        item.updateQuantity(quantity);
        try {
            repository.save(item);
        } catch (Exception e) {
            // Se registra Y se relanza. El log sirve para diagnosticar; lo que
            // impide el dato equivocado es que el llamador se entere.
            log.error("Fallo al guardar la cantidad del item {} de la lista de compras", itemId, e);
            throw new IllegalStateException(
                    "No se pudo guardar la cantidad del item " + itemId, e);
        }
    }

    public void markItemAsPurchased(String itemId) {
        ShoppingItem item = repository.findById(itemId)
                .orElseThrow(() -> new IllegalArgumentException("Item not found"));

        item.markAsPurchased();
        repository.save(item);
    }

    public List<ShoppingItem> generateAutomaticList() {
        return repository.findItemsNeedingRestock();
    }

    public void removeItem(String itemId) {
        repository.deleteById(itemId);
    }
}
