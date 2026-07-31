package com.suresell.mscoreapp.infrastructure.multitenant;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.sql.DataSource;
import org.springframework.jdbc.datasource.DelegatingDataSource;

/**
 * Fija `app.tenant_id` en cada conexión que entrega, tomándolo del
 * {@link TenantContext}. Es la pieza que hace que Row-Level Security aísle por
 * negocio.
 *
 * <p>Mismo mecanismo que ya usa `ms-order-product`, a propósito: dos servicios
 * que comparten base deben aislar igual, o el más débil define la seguridad
 * real del sistema.
 *
 * <p><b>Sin tenant se limpia a ''</b> (arranque, health checks, tareas de
 * fondo): RLS no devuelve ninguna fila. El default seguro es no ver nada, nunca
 * ver lo de otro.
 *
 * <p><b>Pooling</b>: la variable de sesión sobrevive entre transacciones solo en
 * conexión directa o pooler en modo *session*. Este servicio usa el puerto 5432
 * (session). Con el 6543 (transaction) el ajuste se perdería entre consultas y
 * RLS dejaría de ver datos.
 */
public class TenantAwareDataSource extends DelegatingDataSource {

    public TenantAwareDataSource(DataSource objetivo) {
        super(objetivo);
    }

    @Override
    public Connection getConnection() throws SQLException {
        return aplicarTenant(super.getConnection());
    }

    @Override
    public Connection getConnection(String usuario, String clave) throws SQLException {
        return aplicarTenant(super.getConnection(usuario, clave));
    }

    private Connection aplicarTenant(Connection conexion) throws SQLException {
        String tenantId = TenantContext.get();
        try (PreparedStatement ps =
                     conexion.prepareStatement("SELECT set_config('app.tenant_id', ?, false)")) {
            ps.setString(1, tenantId == null ? "" : tenantId);
            ps.execute();
        }
        return conexion;
    }
}
