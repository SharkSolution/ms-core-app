package com.suresell.mscoreapp.infrastructure.persistence;

import com.suresell.mscoreapp.domain.model.WaiterEntity;
import com.suresell.mscoreapp.infrastructure.multitenant.TenantContext;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import java.util.List;
import java.util.function.Function;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * QUE EL PANEL PUEDA ESCRIBIR, Y EN EL NEGOCIO CORRECTO.
 *
 * <p>Contexto: crear un mesero desde el panel web devolvia <b>500</b>. Ninguna
 * de las 25 entidades de este servicio mapeaba `tenant_id`, asi que sus INSERT
 * omitian la columna; sin un valor por defecto quedaba NULL, el {@code WITH
 * CHECK} de la politica daba falso y la fila se rechazaba. <b>Leer funcionaba,
 * escribir nunca.</b> Peor: 17 tablas traian {@code DEFAULT 'shark-burger'}
 * quemado, o sea que para cualquier cliente nuevo el panel nacia roto.
 *
 * <p>Nada de eso se podia ver antes de produccion: este servicio corre con
 * {@code ddl-auto: none} contra una base compartida con RLS y no tenia una sola
 * prueba con base. Un mock o un H2 no tienen politicas, ni defaults, ni roles —
 * justo las tres cosas que fallaban. De ahi que esta prueba levante un Postgres
 * real y se conecte como {@code app_user}, que es el rol con el que corre el
 * servicio y al que RLS si le aplica.
 *
 * <p>Lo que fija: el INSERT del panel entra, y entra en el negocio de la sesion
 * y no en otro. Y sin negocio en sesion, no entra.
 *
 * <h3>Por que cada caso abre su propia sesion</h3>
 *
 * Al introducir {@code @TenantId} (defensa en profundidad, ver
 * {@code EntidadDeNegocio}), Hibernate resuelve el negocio <b>una sola vez, al
 * abrir la sesion</b>, y ya no lo vuelve a mirar. Eso es correcto y es lo que se
 * quiere: en produccion una peticion HTTP es un negocio, y
 * {@code JwtTenantFilter} fija el contexto antes de que empiece la transaccion.
 *
 * <p>Pero significa que <b>no se puede cambiar de negocio a mitad de una
 * sesion</b>. La version anterior de esta prueba lo hacia —una unica transaccion
 * de {@code @DataJpaTest} con {@code set_config} por medio— y por eso hubo que
 * reescribir la MECANICA, no las afirmaciones: las cuatro que comprobaba se
 * siguen comprobando igual.
 *
 * <p>{@link #enElNegocio} reproduce la secuencia real: contexto -> conexion con
 * {@code app.tenant_id} -> sesion. Las dos capas salen del mismo sitio, asi que
 * siempre coinciden; si divergieran, la de abajo rechaza la fila.
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Transactional(propagation = Propagation.NOT_SUPPORTED)
@Testcontainers
class PanelEscribeEnElNegocioCorrectoTest {

    @Container
    static final PostgreSQLContainer<?> PG = new PostgreSQLContainer<>("postgres:16-alpine")
            .withInitScript("esquema-waiters-con-rls.sql");

    @DynamicPropertySource
    static void props(DynamicPropertyRegistry r) {
        r.add("spring.datasource.url", PG::getJdbcUrl);
        r.add("spring.datasource.username", () -> "app_user");
        r.add("spring.datasource.password", () -> "app_pw");
        r.add("spring.jpa.hibernate.ddl-auto", () -> "none");
    }

    @Autowired
    EntityManagerFactory emf;

    @AfterEach
    void limpiarContexto() {
        // El ThreadLocal sobrevive al test y JUnit reutiliza el hilo. Sin esto,
        // un caso arrastraria el negocio del anterior — que es exactamente la
        // fuga que TenantContext.clear() existe para impedir en produccion.
        TenantContext.clear();
    }

    /**
     * Ejecuta algo como lo haria una peticion del panel de ese negocio, en su
     * propia sesion y su propia transaccion.
     *
     * <p>El orden importa y es el mismo que en produccion:
     * <ol>
     *   <li>{@code TenantContext} — lo que hace {@code JwtTenantFilter} con el
     *       JWT. De aqui lee el resolvedor de Hibernate al abrir la sesion.</li>
     *   <li>{@code app.tenant_id} en la conexion — lo que hace
     *       {@code TenantAwareDataSource}. De aqui lee la politica de RLS. Se
     *       fija a mano porque {@code @DataJpaTest} no carga el
     *       {@code BeanPostProcessor} que envuelve el DataSource.</li>
     * </ol>
     */
    private <T> T enElNegocio(String tenantId, Function<EntityManager, T> accion) {
        TenantContext.set(tenantId);
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            em.createNativeQuery("SELECT set_config('app.tenant_id', :t, true)")
                    .setParameter("t", tenantId)
                    .getSingleResult();
            T resultado = accion.apply(em);
            em.getTransaction().commit();
            return resultado;
        } catch (RuntimeException e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    /** Lee el tenant real de la fila, saltandose el mapeo de la entidad. */
    private String tenantDe(String tenantId, Long id) {
        return enElNegocio(tenantId, em -> (String) em
                .createNativeQuery("SELECT tenant_id FROM waiters WHERE id = :id")
                .setParameter("id", id)
                .getSingleResult());
    }

    private WaiterEntity nuevoMesero(String nombre) {
        WaiterEntity w = new WaiterEntity();
        w.setName(nombre);
        w.setActive(true);
        return w;
    }

    @Test
    @DisplayName("crear un mesero desde el panel entra, y queda en el negocio de la sesion")
    void elMeseroQuedaEnElNegocioDeLaSesion() {
        WaiterEntity guardada = enElNegocio("shark-burger", em -> {
            WaiterEntity angie = nuevoMesero("Angie");
            em.persist(angie);
            em.flush();
            return angie;
        });

        assertEquals("shark-burger", tenantDe("shark-burger", guardada.getId()),
                "Es el 500 que veia el usuario al crear un mesero");
        assertEquals("shark-burger", guardada.getTenantId(),
                "Y el codigo puede LEER de quien es la fila: con @TenantId el valor "
                        + "lo pone Hibernate al insertar, asi que esta disponible enseguida");
    }

    @Test
    @DisplayName("el mismo codigo sirve para CUALQUIER negocio, no solo para uno")
    void sirveParaCualquierNegocio() {
        // Esto es lo que el default quemado 'shark-burger' hacia imposible: para
        // otro negocio, el tenant no coincidia con el default y el INSERT moria.
        WaiterEntity guardada = enElNegocio("pizzeria-nueva", em -> {
            WaiterEntity carlos = nuevoMesero("Carlos");
            em.persist(carlos);
            em.flush();
            return carlos;
        });

        assertEquals("pizzeria-nueva", guardada.getTenantId(),
                "El panel estaba roto de fabrica para todo cliente que no fuera el primero");
        assertEquals("pizzeria-nueva", tenantDe("pizzeria-nueva", guardada.getId()));
    }

    @Test
    @DisplayName("sin negocio en sesion NO se escribe a ciegas")
    void sinNegocioEnSesionNoSeEscribe() {
        // Cadena vacia, que es lo que deja TenantAwareDataSource cuando no hay
        // tenant en contexto — no la deja sin fijar. Sin el `nullif` de V32 la
        // fila entraria con tenant_id = '' porque '' = '' es cierto.
        //
        // Con @TenantId hay ademas una segunda barrera: el resolvedor devuelve
        // '__sin_negocio__', que tampoco coincide con app.tenant_id, asi que la
        // politica la rechaza igual. Dos capas, el mismo resultado.
        Exception e = assertThrows(Exception.class,
                () -> enElNegocio("", em -> {
                    em.persist(nuevoMesero("__sonda__"));
                    em.flush();
                    return null;
                }),
                "Una fila sin negocio no puede entrar: no seria de nadie");

        assertTrue(causaRaiz(e).toLowerCase().matches(".*(row-level security|null value|not-null).*"),
                "Debe rechazarse por RLS o por NOT NULL, no por otra cosa: " + causaRaiz(e));
    }

    @Test
    @DisplayName("un negocio no ve los meseros de otro")
    void unNegocioNoVeLosDeOtro() {
        enElNegocio("negocio-a", em -> {
            em.persist(nuevoMesero("De A"));
            em.flush();
            return null;
        });

        List<WaiterEntity> vistosPorB = enElNegocio("negocio-b", em -> em
                .createQuery("SELECT w FROM WaiterEntity w", WaiterEntity.class)
                .getResultList());

        assertTrue(vistosPorB.isEmpty(),
                "El negocio B no puede ver, ni contar, los meseros de A");
    }

    private static String causaRaiz(Throwable t) {
        Throwable c = t;
        while (c.getCause() != null) {
            c = c.getCause();
        }
        return String.valueOf(c.getMessage());
    }
}
