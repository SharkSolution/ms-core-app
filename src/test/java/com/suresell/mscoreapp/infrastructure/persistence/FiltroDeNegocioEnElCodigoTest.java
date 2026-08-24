package com.suresell.mscoreapp.infrastructure.persistence;

import com.suresell.mscoreapp.domain.model.WaiterEntity;
import com.suresell.mscoreapp.infrastructure.multitenant.TenantContext;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import java.util.List;
import java.util.function.Function;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
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
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * EL FILTRO POR NEGOCIO DEL CÓDIGO AÍSLA POR SÍ SOLO, SIN AYUDA DE LA BASE.
 *
 * <h3>Por qué este test es distinto de todos los demás</h3>
 *
 * Corre contra un esquema con la política de RLS <b>abierta a propósito</b>
 * ({@code esquema-sin-rls-real.sql}: {@code USING (true) WITH CHECK (true)}),
 * que es exactamente como están hoy las 17 tablas de administración que creó
 * {@code V28:150-164} — {@code supplies}, {@code employees}, {@code payrolls},
 * {@code expenses}, {@code valeras}, {@code accounts_receivable} y once más.
 *
 * <p>Contra una tabla con RLS real, un test de aislamiento pasa aunque el filtro
 * de aplicación no exista: estaría midiendo la política de Postgres, no el
 * código. Con la política abierta, lo único que puede aislar es el
 * {@code WHERE tenant_id = ?} que Hibernate añade por {@code @TenantId}.
 *
 * <p><b>Si alguien quita el {@code @TenantId} de {@code EntidadDeNegocio} o la
 * propiedad {@code hibernate.tenant_identifier_resolver} del
 * {@code application.yml}, este test se pone rojo y ningún otro lo hace.</b> Esa
 * es toda su razón de ser: que la defensa en profundidad no pueda desaparecer en
 * silencio.
 *
 * <h3>De qué protege en la práctica</h3>
 *
 * De las dos formas conocidas en que RLS deja de aislar sin que el código se
 * entere: conectarse con un rol que tenga {@code BYPASSRLS} —el
 * {@code postgres.<project-ref>} de Supabase lo tiene, y es el que usa el camino
 * de sincronización de `ms-order-product-mt`— y una política mal escrita, que ya
 * ocurrió y sigue vigente en 17 tablas.
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Transactional(propagation = Propagation.NOT_SUPPORTED)
@Testcontainers
class FiltroDeNegocioEnElCodigoTest {

    @Container
    static final PostgreSQLContainer<?> PG = new PostgreSQLContainer<>("postgres:16-alpine")
            .withInitScript("esquema-sin-rls-real.sql");

    @DynamicPropertySource
    static void props(DynamicPropertyRegistry r) {
        r.add("spring.datasource.url", PG::getJdbcUrl);
        r.add("spring.datasource.username", () -> "app_user");
        r.add("spring.datasource.password", () -> "app_pw");
        r.add("spring.jpa.hibernate.ddl-auto", () -> "none");
    }

    @Autowired
    EntityManagerFactory emf;

    /**
     * Sin transacción de test no hay rollback automático, así que cada caso
     * empieza con la tabla vacía. Se borra con SQL nativo y no con la entidad:
     * un {@code DELETE} por JPA lo filtraría {@code @TenantId} y solo borraría
     * las filas de un negocio, que es justo lo contrario de lo que hace falta.
     */
    @BeforeEach
    void tablaVacia() {
        TenantContext.set("limpieza");
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            em.createNativeQuery("DELETE FROM waiters").executeUpdate();
            em.getTransaction().commit();
        } finally {
            em.close();
            TenantContext.clear();
        }
    }

    @AfterEach
    void limpiarContexto() {
        TenantContext.clear();
    }

    /**
     * Igual que en producción, pero SIN fijar {@code app.tenant_id}: aquí la
     * política es abierta y no lo mira. Lo único en juego es el contexto de
     * negocio que lee el resolvedor de Hibernate al abrir la sesión.
     */
    private <T> T comoElNegocio(String tenantId, Function<EntityManager, T> accion) {
        TenantContext.set(tenantId);
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
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

    private Long crearMesero(String tenantId, String nombre) {
        return comoElNegocio(tenantId, em -> {
            WaiterEntity w = new WaiterEntity();
            w.setName(nombre);
            w.setActive(true);
            em.persist(w);
            em.flush();
            return w.getId();
        });
    }

    private List<WaiterEntity> meserosQueVe(String tenantId) {
        return comoElNegocio(tenantId, em -> em
                .createQuery("SELECT w FROM WaiterEntity w", WaiterEntity.class)
                .getResultList());
    }

    // =====================================================================

    @Test
    @DisplayName("con la política ABIERTA, un negocio sigue sin ver los datos de otro")
    void elCodigoAislaAunqueLaBaseNoLoHaga() {
        crearMesero("negocio-a", "Angie de A");
        crearMesero("negocio-b", "Bruno de B");
        crearMesero("negocio-b", "Bea de B");

        List<WaiterEntity> deA = meserosQueVe("negocio-a");
        List<WaiterEntity> deB = meserosQueVe("negocio-b");

        assertEquals(1, deA.size(), "A debe ver solo el suyo");
        assertEquals("Angie de A", deA.get(0).getName());

        assertEquals(2, deB.size(), "B debe ver solo los suyos");
        assertTrue(deB.stream().allMatch(w -> "negocio-b".equals(w.getTenantId())));

        // La comprobación que da sentido a todo el archivo: la base NO está
        // filtrando. Si el código no filtrara, cada uno habría visto los tres.
        long totalReal = comoElNegocio("negocio-a", em -> ((Number) em
                .createNativeQuery("SELECT count(*) FROM waiters")
                .getSingleResult()).longValue());
        assertEquals(3, totalReal,
                "La politica esta abierta y en la tabla hay 3 filas: si este numero "
                        + "fuera 1, el aislamiento lo estaria haciendo la base y el test "
                        + "no probaria nada del codigo");
    }

    @Test
    @DisplayName("un negocio no puede leer por id una fila de otro")
    void tampocoPorIdDirecto() {
        Long idDeA = crearMesero("negocio-a", "Angie de A");

        WaiterEntity vistoPorB = comoElNegocio("negocio-b",
                em -> em.find(WaiterEntity.class, idDeA));

        assertTrue(vistoPorB == null,
                "Conocer el id de una fila ajena no puede bastar para leerla");
    }

    @Test
    @DisplayName("sin negocio en sesión no se ve nada, ni siquiera con la política abierta")
    void sinNegocioNoSeVeNada() {
        crearMesero("negocio-a", "Angie de A");
        crearMesero("negocio-b", "Bruno de B");

        // TenantContext vacío: el resolvedor devuelve '__sin_negocio__', que no
        // es de nadie. Falla cerrado.
        TenantContext.clear();
        EntityManager em = emf.createEntityManager();
        try {
            List<WaiterEntity> todos = em
                    .createQuery("SELECT w FROM WaiterEntity w", WaiterEntity.class)
                    .getResultList();
            assertTrue(todos.isEmpty(),
                    "Sin negocio en sesion el default seguro es no ver nada, nunca ver lo de otro");
        } finally {
            em.close();
        }
    }

    @Test
    @DisplayName("el INSERT escribe el negocio de la sesión, no el de otro")
    void alEscribirTambienFiltra() {
        Long id = crearMesero("negocio-c", "Carla de C");

        String tenantEnLaFila = comoElNegocio("negocio-c", em -> (String) em
                .createNativeQuery("SELECT tenant_id FROM waiters WHERE id = :id")
                .setParameter("id", id)
                .getSingleResult());

        // Con la politica abierta el WITH CHECK no protege: si Hibernate
        // escribiera mal el tenant, la fila entraria igual. Por eso se comprueba.
        assertEquals("negocio-c", tenantEnLaFila);
    }
}
