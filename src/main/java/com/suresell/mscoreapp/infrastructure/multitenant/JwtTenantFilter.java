package com.suresell.mscoreapp.infrastructure.multitenant;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import javax.crypto.SecretKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Exige un JWT válido y deja el tenant en {@link TenantContext}.
 *
 * <p><b>Por qué existe.</b> Hasta ahora este servicio no pedía autenticación:
 * cualquiera con la URL podía leer los gastos, la nómina y las ventas del
 * negocio desde internet (verificado: 212 gastos con un GET sin cabeceras).
 * Además, al no saber quién preguntaba, no había forma de servir a más de un
 * negocio.
 *
 * <p>El token es el MISMO que emite `ms-order-product` al iniciar sesión en el
 * panel, así que no hay un segundo sistema de credenciales que mantener.
 */
@Component
public class JwtTenantFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(JwtTenantFilter.class);

    private final SecretKey key;

    public JwtTenantFilter(@Value("${security.jwt.secret:}") String secret) {
        if (secret == null || secret.isBlank()) {
            throw new IllegalStateException(
                    "Falta security.jwt.secret (variable JWT_SECRET). Sin ella este servicio "
                            + "no puede validar tokens y quedaria abierto a internet.");
        }
        this.key = io.jsonwebtoken.security.Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    /** Rutas que no llevan tenant: salud y documentación. */
    private static boolean esPublica(String ruta) {
        return ruta.startsWith("/actuator")
                || ruta.startsWith("/swagger-ui")
                || ruta.startsWith("/v3/api-docs");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
            throws ServletException, IOException {

        if (esPublica(req.getRequestURI()) || "OPTIONS".equalsIgnoreCase(req.getMethod())) {
            chain.doFilter(req, res);
            return;
        }

        String cabecera = req.getHeader("Authorization");
        if (cabecera == null || !cabecera.startsWith("Bearer ")) {
            res.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Falta el token de sesion");
            return;
        }

        String tenantId;
        try {
            Claims claims = Jwts.parser().verifyWith(key).build()
                    .parseSignedClaims(cabecera.substring(7).trim())
                    .getPayload();
            // El emisor usa `tenant_id`. Se acepta tambien `tenantId` por si
            // alguna version del token lo escribe en camelCase.
            tenantId = claims.get("tenant_id", String.class);
            if (tenantId == null) {
                tenantId = claims.get("tenantId", String.class);
            }
        } catch (Exception e) {
            log.debug("Token rechazado: {}", e.getMessage());
            res.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Sesion invalida o vencida");
            return;
        }

        if (tenantId == null || tenantId.isBlank()) {
            // Un token sin tenant (super-admin del KAM) no puede leer datos de
            // negocio: no sabriamos de cual.
            res.sendError(HttpServletResponse.SC_FORBIDDEN, "El token no identifica un negocio");
            return;
        }

        try {
            TenantContext.set(tenantId);
            chain.doFilter(req, res);
        } finally {
            // Imprescindible: el hilo vuelve al pool y sin esto arrastraria el
            // tenant al siguiente request.
            TenantContext.clear();
        }
    }
}
