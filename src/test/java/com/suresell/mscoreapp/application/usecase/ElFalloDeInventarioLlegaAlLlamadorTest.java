package com.suresell.mscoreapp.application.usecase;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.suresell.mscoreapp.application.dto.ShoppingItem;
import com.suresell.mscoreapp.domain.port.out.ISupplyCategoryRepository;
import com.suresell.mscoreapp.domain.port.out.ShoppingListRepository;
import java.math.BigDecimal;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Si no se puede guardar una cantidad de inventario, <b>quien llamó tiene que
 * enterarse</b>.
 *
 * <h3>El defecto que fija esta prueba</h3>
 *
 * {@code updateItemQuantity} era un método {@code void} con esto dentro:
 *
 * <pre>
 * try { repository.save(item); }
 * catch (Exception e) { System.out.println(e.getMessage()); }
 * </pre>
 *
 * Si el guardado fallaba, el método volvía normal y el llamador creía que se
 * había guardado. El inventario quedaba con una cantidad distinta de la que el
 * usuario vio confirmada, y ni siquiera había logger: {@code System.out} puede
 * no aparecer en el proveedor de despliegue.
 *
 * <p>Era el caso I1 de `discovery/FALLBACK-SILENCIOSO.md`, el más silencioso del
 * inventario entero.
 */
class ElFalloDeInventarioLlegaAlLlamadorTest {

    private ShoppingListRepository repositorio;
    private ManageShoppingListUseCase casoDeUso;

    @BeforeEach
    void preparar() {
        repositorio = mock(ShoppingListRepository.class);
        casoDeUso = new ManageShoppingListUseCase(
                repositorio, mock(ShoppingListMapper.class), mock(ISupplyCategoryRepository.class));
    }

    private ShoppingItem item() {
        ShoppingItem i = new ShoppingItem("Pan", "Panaderia", "und", BigDecimal.ONE, BigDecimal.ONE);
        i.setId("item-1");
        return i;
    }

    @Test
    @DisplayName("si el guardado falla, la excepción LLEGA al llamador")
    void elFalloSePropaga() {
        when(repositorio.findById("item-1")).thenReturn(Optional.of(item()));
        when(repositorio.save(any())).thenThrow(new RuntimeException("la base no responde"));

        // Antes esto no lanzaba nada y el llamador seguia como si todo hubiera
        // ido bien. Ese es exactamente el bug.
        assertThatThrownBy(() -> casoDeUso.updateItemQuantity("item-1", new BigDecimal("5")))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("item-1")
                .hasRootCauseMessage("la base no responde");
    }

    @Test
    @DisplayName("el motivo original no se pierde: viaja como causa")
    void conservaLaCausa() {
        when(repositorio.findById("item-1")).thenReturn(Optional.of(item()));
        when(repositorio.save(any())).thenThrow(new IllegalArgumentException("cantidad negativa"));

        assertThatThrownBy(() -> casoDeUso.updateItemQuantity("item-1", new BigDecimal("-1")))
                .hasRootCauseInstanceOf(IllegalArgumentException.class)
                .hasRootCauseMessage("cantidad negativa");
    }

    @Test
    @DisplayName("cuando el guardado funciona, no lanza nada")
    void elCaminoFelizSigueIgual() {
        when(repositorio.findById("item-1")).thenReturn(Optional.of(item()));
        when(repositorio.save(any())).thenAnswer(inv -> inv.getArgument(0));

        assertThatCode(() -> casoDeUso.updateItemQuantity("item-1", new BigDecimal("5")))
                .doesNotThrowAnyException();
        verify(repositorio).save(any());
    }

    @Test
    @DisplayName("un item que no existe sigue fallando como antes")
    void itemInexistente() {
        when(repositorio.findById("fantasma")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> casoDeUso.updateItemQuantity("fantasma", BigDecimal.ONE))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("not found");
    }
}
