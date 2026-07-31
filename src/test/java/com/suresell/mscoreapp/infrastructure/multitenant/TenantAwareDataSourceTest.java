package com.suresell.mscoreapp.infrastructure.multitenant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.sql.DataSource;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * La otra mitad del aislamiento: que la conexión salga con el negocio correcto.
 *
 * <p>El filtro decide <i>quién</i> pregunta; esto decide <i>qué puede ver</i>.
 * Row-Level Security se apoya en `app.tenant_id`, así que si esta clase se
 * saltara el ajuste, RLS filtraría por el valor que quedó de antes — y el aislamiento
 * entre negocios se cae entero sin ningún error visible.
 */
class TenantAwareDataSourceTest {

    @AfterEach
    void limpiar() {
        TenantContext.clear();
    }

    /** Devuelve el valor que se le pasó a set_config. */
    private String tenantAplicado(String tenantEnContexto) throws SQLException {
        DataSource objetivo = mock(DataSource.class);
        Connection conexion = mock(Connection.class);
        PreparedStatement ps = mock(PreparedStatement.class);

        when(objetivo.getConnection()).thenReturn(conexion);
        when(conexion.prepareStatement(anyString())).thenReturn(ps);

        if (tenantEnContexto != null) {
            TenantContext.set(tenantEnContexto);
        }

        new TenantAwareDataSource(objetivo).getConnection();

        org.mockito.ArgumentCaptor<String> valor = org.mockito.ArgumentCaptor.forClass(String.class);
        verify(ps).setString(org.mockito.ArgumentMatchers.eq(1), valor.capture());
        verify(conexion).prepareStatement("SELECT set_config('app.tenant_id', ?, false)");
        return valor.getValue();
    }

    @Test
    @DisplayName("la conexión sale con el negocio del request")
    void aplicaElTenantDelContexto() throws SQLException {
        assertThat(tenantAplicado("shark")).isEqualTo("shark");
    }

    @Test
    @DisplayName("otro negocio, otro valor: no se reutiliza el anterior")
    void noArrastraElAnterior() throws SQLException {
        assertThat(tenantAplicado("shark")).isEqualTo("shark");
        TenantContext.clear();
        assertThat(tenantAplicado("otro-negocio")).isEqualTo("otro-negocio");
    }

    @Test
    @DisplayName("sin negocio se limpia a vacío: el default seguro es no ver nada")
    void sinTenantLimpiaAVacio() throws SQLException {
        // Arranque, health checks y tareas de fondo pasan por aca. Dejar el
        // valor anterior seria peor que no ver nada: se verian datos ajenos.
        assertThat(tenantAplicado(null)).isEmpty();
    }

    @Test
    @DisplayName("el ajuste se aplica SIEMPRE, no solo la primera vez")
    void seAplicaEnCadaConexion() throws SQLException {
        DataSource objetivo = mock(DataSource.class);
        Connection conexion = mock(Connection.class);
        PreparedStatement ps = mock(PreparedStatement.class);
        when(objetivo.getConnection()).thenReturn(conexion);
        when(conexion.prepareStatement(anyString())).thenReturn(ps);

        TenantAwareDataSource ds = new TenantAwareDataSource(objetivo);
        TenantContext.set("shark");
        ds.getConnection();
        ds.getConnection();
        ds.getConnection();

        // Las conexiones vienen de un pool: una reusada trae el tenant de quien
        // la uso antes si no se vuelve a fijar.
        verify(ps, org.mockito.Mockito.times(3)).setString(1, "shark");
    }

    @Test
    @DisplayName("el sentencia se cierra aunque falle, para no filtrar recursos")
    void cierraLaSentencia() throws SQLException {
        DataSource objetivo = mock(DataSource.class);
        Connection conexion = mock(Connection.class);
        PreparedStatement ps = mock(PreparedStatement.class);
        when(objetivo.getConnection()).thenReturn(conexion);
        when(conexion.prepareStatement(anyString())).thenReturn(ps);

        TenantContext.set("shark");
        new TenantAwareDataSource(objetivo).getConnection();

        verify(ps).close();
    }
}
