package com.suresell.mscoreapp.infrastructure.multitenant;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.sql.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.datasource.DelegatingDataSource;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

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

    private static final Logger log = LoggerFactory.getLogger(TenantAwareDataSource.class);

    /**
     * Cada origen distinto se avisa una vez por ventana. Sin esto un scheduler
     * que corre cada pocos segundos llenaría el log él solo y taparía justo lo
     * que hay que encontrar: los orígenes poco frecuentes.
     */
    private static final Duration VENTANA_AVISO = Duration.ofMinutes(10);

    /** Origen → instante del último aviso. Acotado: hay pocos orígenes distintos. */
    private final Map<String, Instant> ultimoAviso = new ConcurrentHashMap<>();

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
        if (tenantId == null || tenantId.isBlank()) {
            avisarSinNegocio();
        }
        try (PreparedStatement ps =
                     conexion.prepareStatement("SELECT set_config('app.tenant_id', ?, false)")) {
            ps.setString(1, tenantId == null ? "" : tenantId);
            ps.execute();
        }
        return conexion;
    }

    // =====================================================================
    // Instrumentación: quién pide conexiones sin negocio en sesión
    //
    // POR QUÉ EXISTE. Hoy 17 tablas de este servicio (supplies, employees,
    // payrolls, expenses, accounts_receivable, valeras…) tienen RLS activada
    // pero con una política `USING (true)`: no aíslan nada. La migración que las
    // cierra está escrita, pero aplicarla a ciegas es peligroso, porque NINGÚN
    // repositorio de este servicio filtra por tenant — todo el aislamiento
    // dependería de que `app.tenant_id` esté fijado en TODAS las conexiones.
    //
    // Si algún camino pide conexión sin negocio en sesión, el día que se cierre
    // la política ese camino dejará de ver filas. Este aviso es la forma de
    // averiguar cuáles son ANTES, con datos y no con suposiciones: se despliega,
    // se deja correr unos días y se lee el log.
    //
    // Es solo un WARN. No cambia el comportamiento: la conexión se entrega igual
    // y `set_config` se ejecuta con cadena vacía, exactamente como antes.
    // =====================================================================

    private void avisarSinNegocio() {
        String origen = describirOrigen();
        Instant ahora = Instant.now();
        Instant previo = ultimoAviso.get(origen);
        if (previo != null && previo.isAfter(ahora.minus(VENTANA_AVISO))) {
            return;
        }
        ultimoAviso.put(origen, ahora);
        log.warn("tenant-ausente origen=\"{}\" hilo=\"{}\" "
                        + "-- conexión entregada sin app.tenant_id; "
                        + "este camino dejará de ver filas cuando se cierre la política RLS "
                        + "(ver PRE-REQUISITOS-RLS.md)",
                origen, Thread.currentThread().getName());
    }

    /**
     * Identifica de dónde viene la petición. Si hay un request HTTP en curso, su
     * método y ruta; si no —scheduler, health check, arranque— la primera clase
     * propia de la pila, que es lo que permite ponerle nombre al componente.
     */
    private String describirOrigen() {
        String http = origenHttp();
        if (http != null) {
            return http;
        }
        return StackWalker.getInstance()
                .walk(marcos -> marcos
                        .map(StackWalker.StackFrame::getClassName)
                        .filter(c -> c.startsWith("com.suresell."))
                        .filter(c -> !c.equals(TenantAwareDataSource.class.getName()))
                        .findFirst()
                        .orElse("desconocido"));
    }

    /**
     * Ruta del request en curso, o null si no hay ninguno. Se envuelve en
     * try/catch porque esto corre en el camino de CADA conexión: un fallo aquí
     * no puede tumbar una consulta. El aviso es diagnóstico, nunca crítico.
     */
    private String origenHttp() {
        try {
            RequestAttributes attrs = RequestContextHolder.getRequestAttributes();
            if (attrs instanceof ServletRequestAttributes sra) {
                var req = sra.getRequest();
                return req.getMethod() + " " + req.getRequestURI();
            }
        } catch (Exception ignorado) {
            // Sin contexto web utilizable; se cae al nombre de la clase.
        }
        return null;
    }
}
