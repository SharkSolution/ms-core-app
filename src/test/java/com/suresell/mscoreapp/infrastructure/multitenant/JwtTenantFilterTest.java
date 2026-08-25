package com.suresell.mscoreapp.infrastructure.multitenant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import io.jsonwebtoken.Jwts;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;
import javax.crypto.SecretKey;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

/**
 * Red de seguridad del aislamiento entre negocios.
 *
 * <p><b>Por qué existe.</b> Este servicio estuvo abierto a internet sin ninguna
 * autenticación: un GET sin cabeceras devolvía los 212 gastos del negocio. El
 * filtro que lo cerró estaba verificado sólo contra el despliegue, o sea que una
 * regresión lo habría vuelto a abrir sin que nadie se enterara.
 *
 * <p>Estos tests son esa red. El más importante es el del final: que el tenant
 * NO se quede pegado al hilo, porque el hilo vuelve al pool y atiende el request
 * del negocio siguiente.
 */
class JwtTenantFilterTest {

    private static final String SECRETO =
            "clave-de-prueba-suficientemente-larga-para-hmac-sha256-1234567890";
    private static final String OTRO_SECRETO =
            "otra-clave-distinta-igual-de-larga-para-hmac-sha256-0987654321";

    private final JwtTenantFilter filtro = new JwtTenantFilter(SECRETO);

    @AfterEach
    void limpiarContexto() {
        TenantContext.clear();
    }

    private static String token(String secreto, Map<String, Object> claims, long vigenciaMs) {
        SecretKey key = io.jsonwebtoken.security.Keys.hmacShaKeyFor(secreto.getBytes(StandardCharsets.UTF_8));
        long ahora = System.currentTimeMillis();
        return Jwts.builder()
                .claims(claims)
                .issuedAt(new Date(ahora - 1000))
                .expiration(new Date(ahora + vigenciaMs))
                .signWith(key)
                .compact();
    }

    private static String tokenValido(String tenantId) {
        return token(SECRETO, Map.of("tenant_id", tenantId, "role", "ADMIN"), 60_000);
    }

    /**
     * El context path REAL de este servicio. No es decorativo.
     *
     * <p>Hasta el 2026-08-24 este helper construía la petición con
     * {@code setRequestURI(ruta)} y nada más — o sea, con el context path vacío,
     * una forma de petición que <b>en producción no ocurre nunca</b>. El filtro
     * comparaba {@code getRequestURI()} contra {@code "/actuator"}, y en el test
     * eso daba {@code "/actuator/health"} y coincidía. En producción daba
     * {@code "/api/core/actuator/health"} y no coincidía jamás.
     *
     * <p><b>El test estuvo verde tres semanas mientras el health check devolvía
     * 401 en los dos entornos.</b> No falló el filtro por falta de pruebas:
     * falló porque la prueba reproducía una petición que no existe.
     *
     * <p>Por eso ahora se fijan las tres partes por separado, como hace Tomcat.
     */
    private static final String CONTEXTO = "/api/core";

    private static MockHttpServletRequest peticion(String ruta, String autorizacion) {
        MockHttpServletRequest req = new MockHttpServletRequest("GET", CONTEXTO + ruta);
        req.setContextPath(CONTEXTO);
        req.setServletPath(ruta);
        req.setRequestURI(CONTEXTO + ruta);
        if (autorizacion != null) {
            req.addHeader("Authorization", autorizacion);
        }
        return req;
    }

    @Nested
    @DisplayName("Sin un token válido no se pasa")
    class SinToken {

        @Test
        @DisplayName("sin cabecera Authorization: 401, y la cadena ni se ejecuta")
        void sinCabecera() throws Exception {
            MockHttpServletResponse res = new MockHttpServletResponse();
            MockFilterChain cadena = new MockFilterChain();

            filtro.doFilter(peticion("/expenses", null), res, cadena);

            assertThat(res.getStatus()).isEqualTo(401);
            assertThat(cadena.getRequest())
                    .as("la peticion no puede llegar al controlador")
                    .isNull();
        }

        @Test
        @DisplayName("cabecera sin el prefijo Bearer: 401")
        void sinPrefijoBearer() throws Exception {
            MockHttpServletResponse res = new MockHttpServletResponse();

            filtro.doFilter(peticion("/expenses", tokenValido("shark")), res, new MockFilterChain());

            assertThat(res.getStatus()).isEqualTo(401);
        }

        @Test
        @DisplayName("token con basura: 401")
        void tokenIlegible() throws Exception {
            MockHttpServletResponse res = new MockHttpServletResponse();

            filtro.doFilter(peticion("/expenses", "Bearer no-es-un-jwt"), res, new MockFilterChain());

            assertThat(res.getStatus()).isEqualTo(401);
        }

        @Test
        @DisplayName("token vencido: 401")
        void tokenVencido() throws Exception {
            String vencido = token(SECRETO, Map.of("tenant_id", "shark"), -60_000);
            MockHttpServletResponse res = new MockHttpServletResponse();

            filtro.doFilter(peticion("/expenses", "Bearer " + vencido), res, new MockFilterChain());

            assertThat(res.getStatus()).isEqualTo(401);
        }

        @Test
        @DisplayName("ESENCIAL: un token firmado con otra clave no sirve")
        void tokenFirmadoPorOtro() throws Exception {
            // Sin esto, cualquiera que sepa la forma del token podria fabricarse
            // uno con el tenant que quisiera y leer los datos de otro negocio.
            String falsificado = token(OTRO_SECRETO, Map.of("tenant_id", "shark"), 60_000);
            MockHttpServletResponse res = new MockHttpServletResponse();
            MockFilterChain cadena = new MockFilterChain();

            filtro.doFilter(peticion("/expenses", "Bearer " + falsificado), res, cadena);

            assertThat(res.getStatus()).isEqualTo(401);
            assertThat(cadena.getRequest()).isNull();
            assertThat(TenantContext.get()).isNull();
        }

        @Test
        @DisplayName("token con la firma recortada: 401")
        void tokenSinFirma() throws Exception {
            String sinFirma = tokenValido("shark").replaceAll("\\.[^.]+$", ".");
            MockHttpServletResponse res = new MockHttpServletResponse();

            filtro.doFilter(peticion("/expenses", "Bearer " + sinFirma), res, new MockFilterChain());

            assertThat(res.getStatus()).isEqualTo(401);
        }
    }

    @Nested
    @DisplayName("Un token válido pero sin negocio tampoco pasa")
    class SinTenant {

        @Test
        @DisplayName("token de super-admin (sin tenant_id): 403")
        void tokenSinTenant() throws Exception {
            // El super-admin del KAM tiene token valido pero no pertenece a un
            // negocio: no sabriamos de cual servirle los datos.
            String superAdmin = token(SECRETO, Map.of("role", "SUPER_ADMIN"), 60_000);
            MockHttpServletResponse res = new MockHttpServletResponse();
            MockFilterChain cadena = new MockFilterChain();

            filtro.doFilter(peticion("/expenses", "Bearer " + superAdmin), res, cadena);

            assertThat(res.getStatus()).isEqualTo(403);
            assertThat(cadena.getRequest()).isNull();
        }

        @Test
        @DisplayName("tenant_id en blanco: 403")
        void tenantEnBlanco() throws Exception {
            String enBlanco = token(SECRETO, Map.of("tenant_id", "   "), 60_000);
            MockHttpServletResponse res = new MockHttpServletResponse();

            filtro.doFilter(peticion("/expenses", "Bearer " + enBlanco), res, new MockFilterChain());

            assertThat(res.getStatus()).isEqualTo(403);
        }
    }

    @Nested
    @DisplayName("Un token válido deja pasar y fija el negocio")
    class ConTenant {

        @Test
        @DisplayName("la petición llega al controlador con el tenant puesto")
        void dejaPasar() throws Exception {
            MockHttpServletResponse res = new MockHttpServletResponse();
            String[] visto = new String[1];
            FilterChain cadena = (rq, rs) -> visto[0] = TenantContext.get();

            filtro.doFilter(peticion("/expenses", "Bearer " + tokenValido("shark")), res, cadena);

            assertThat(res.getStatus()).isEqualTo(200);
            assertThat(visto[0]).isEqualTo("shark");
        }

        @Test
        @DisplayName("se acepta tenantId en camelCase por compatibilidad")
        void aceptaCamelCase() throws Exception {
            // Regla 3 del contrato de API: no se rompe a un emisor que ya exista.
            String camel = token(SECRETO, Map.of("tenantId", "shark"), 60_000);
            MockHttpServletResponse res = new MockHttpServletResponse();
            String[] visto = new String[1];

            filtro.doFilter(peticion("/expenses", "Bearer " + camel), res,
                    (rq, rs) -> visto[0] = TenantContext.get());

            assertThat(visto[0]).isEqualTo("shark");
        }

        @Test
        @DisplayName("cada negocio ve el suyo, no el del request anterior")
        void cadaUnoElSuyo() throws Exception {
            String[] visto = new String[2];

            filtro.doFilter(peticion("/expenses", "Bearer " + tokenValido("shark")),
                    new MockHttpServletResponse(), (rq, rs) -> visto[0] = TenantContext.get());
            filtro.doFilter(peticion("/expenses", "Bearer " + tokenValido("otro-negocio")),
                    new MockHttpServletResponse(), (rq, rs) -> visto[1] = TenantContext.get());

            assertThat(visto[0]).isEqualTo("shark");
            assertThat(visto[1]).isEqualTo("otro-negocio");
        }
    }

    @Nested
    @DisplayName("El tenant NO se queda pegado al hilo")
    class LimpiezaDelHilo {

        @Test
        @DisplayName("tras responder, el hilo queda limpio")
        void limpiaAlTerminar() throws Exception {
            filtro.doFilter(peticion("/expenses", "Bearer " + tokenValido("shark")),
                    new MockHttpServletResponse(), new MockFilterChain());

            assertThat(TenantContext.get())
                    .as("el hilo vuelve al pool: si arrastra el tenant, el proximo negocio lee datos ajenos")
                    .isNull();
        }

        @Test
        @DisplayName("LO MÁS IMPORTANTE: también limpia si el controlador falla")
        void limpiaAunqueExplote() {
            FilterChain queExplota = (rq, rs) -> {
                throw new IllegalStateException("fallo del controlador");
            };

            assertThatThrownBy(() -> filtro.doFilter(
                    peticion("/expenses", "Bearer " + tokenValido("shark")),
                    new MockHttpServletResponse(), queExplota))
                    .isInstanceOf(IllegalStateException.class);

            // Sin el finally, un error 500 dejaba el tenant pegado al hilo y el
            // siguiente request de OTRO negocio heredaba la conexion con el
            // app.tenant_id equivocado.
            assertThat(TenantContext.get()).isNull();
        }

        @Test
        @DisplayName("un rechazo por 401 no deja tenant puesto")
        void noEnsuciaAlRechazar() throws Exception {
            filtro.doFilter(peticion("/expenses", null), new MockHttpServletResponse(),
                    new MockFilterChain());

            assertThat(TenantContext.get()).isNull();
        }
    }

    @Nested
    @DisplayName("Rutas que no llevan negocio")
    class RutasPublicas {

        @Test
        @DisplayName("la sonda de salud pasa sin token, CON el context path de producción")
        void saludPasa() throws Exception {
            MockHttpServletResponse res = new MockHttpServletResponse();
            MockFilterChain cadena = new MockFilterChain();

            filtro.doFilter(peticion("/actuator/health", null), res, cadena);

            assertThat(res.getStatus()).isEqualTo(200);
            assertThat(cadena.getRequest()).as("tiene que llegar al endpoint").isNotNull();
            assertThat(TenantContext.get()).as("la salud no fija negocio").isNull();
        }

        @Test
        @DisplayName("el resto de actuator sigue cerrado: la exención NO se derrama")
        void actuatorNoSeDerrama() throws Exception {
            // El corazón de este arreglo. La versión anterior eximía /actuator
            // ENTERO; si al corregir la comparación se hubiera dejado así, este
            // servicio habría publicado en internet las variables de entorno y un
            // volcado de memoria de la nómina y la cartera.
            //
            // Hoy solo `health` está expuesto en application.yml, así que estas
            // rutas darían 404 aunque el filtro las dejara pasar. Eso es una
            // coincidencia de configuración, no una defensa: `include` es una
            // línea que alguien puede ampliar sin mirar este fichero.
            for (String ruta : new String[] {
                "/actuator/env", "/actuator/beans", "/actuator/configprops",
                "/actuator/heapdump", "/actuator/loggers", "/actuator/threaddump",
                "/actuator/metrics", "/actuator/mappings"
            }) {
                MockHttpServletResponse res = new MockHttpServletResponse();
                MockFilterChain cadena = new MockFilterChain();

                filtro.doFilter(peticion(ruta, null), res, cadena);

                assertThat(res.getStatus()).as(ruta).isEqualTo(401);
                assertThat(cadena.getRequest()).as(ruta + " no puede llegar al endpoint").isNull();
            }
        }

        @Test
        @DisplayName("una ruta que EMPIEZA por la de salud tampoco pasa")
        void prefijoDeSaludNoBasta() throws Exception {
            // La exención es coincidencia exacta, no `startsWith`. Sin esto,
            // `/actuator/healthcheck-interno` o `/actuator/health/../env` serían
            // públicas por parecerse.
            for (String ruta : new String[] {
                "/actuator/health-detallado", "/actuator/healthz", "/actuator/health/db"
            }) {
                MockHttpServletResponse res = new MockHttpServletResponse();

                filtro.doFilter(peticion(ruta, null), res, new MockFilterChain());

                assertThat(res.getStatus()).as(ruta).isEqualTo(401);
            }
        }

        @Test
        @DisplayName("swagger y la especificación OpenAPI quedan cerradas")
        void documentacionCerrada() throws Exception {
            // Decisión consciente, no un olvido: llevan cerradas desde el
            // 2026-07-30 por el mismo fallo y nadie las echó en falta. Publicar
            // el mapa de endpoints de este servicio no tiene contrapartida.
            for (String ruta : new String[] {
                "/swagger-ui/index.html", "/swagger-ui.html", "/v3/api-docs"
            }) {
                MockHttpServletResponse res = new MockHttpServletResponse();

                filtro.doFilter(peticion(ruta, null), res, new MockFilterChain());

                assertThat(res.getStatus()).as(ruta).isEqualTo(401);
            }
        }

        @Test
        @DisplayName("el preflight CORS pasa sin token")
        void preflightPasa() throws Exception {
            MockHttpServletRequest req = new MockHttpServletRequest("OPTIONS", CONTEXTO + "/expenses");
            req.setContextPath(CONTEXTO);
            req.setServletPath("/expenses");
            req.setRequestURI(CONTEXTO + "/expenses");
            MockFilterChain cadena = new MockFilterChain();

            filtro.doFilter(req, new MockHttpServletResponse(), cadena);

            assertThat(cadena.getRequest()).isNotNull();
        }

        @Test
        @DisplayName("una ruta de datos NUNCA es pública, aunque se parezca")
        void datosNoSonPublicos() throws Exception {
            for (String ruta : new String[] {"/expenses", "/payrolls", "/valeras", "/supplies"}) {
                MockHttpServletResponse res = new MockHttpServletResponse();

                filtro.doFilter(peticion(ruta, null), res, new MockFilterChain());

                assertThat(res.getStatus()).as(ruta).isEqualTo(401);
            }
        }
    }

    @Nested
    @DisplayName("Arranque")
    class Arranque {

        @Test
        @DisplayName("sin JWT_SECRET el servicio se niega a arrancar")
        void exigeSecreto() {
            // Preferimos que no arranque a que arranque abierto a internet.
            assertThatThrownBy(() -> new JwtTenantFilter(""))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("JWT_SECRET");

            assertThatThrownBy(() -> new JwtTenantFilter(null))
                    .isInstanceOf(IllegalStateException.class);
        }
    }
}
