package com.suresell.mscoreapp.infrastructure.persistence;

import com.suresell.mscoreapp.domain.model.WaiterEntity;
import com.suresell.mscoreapp.infrastructure.persistence.jpa.WaiterJpaRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
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
 * de las 25 entidades de este servicio mapea `tenant_id`, asi que sus INSERT
 * omiten la columna; sin un valor por defecto quedaba NULL, el {@code WITH
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
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
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
    WaiterJpaRepository waiters;

    @Autowired
    EntityManager em;

    /** Lo que hace TenantAwareDataSource al entregar una conexion. */
    private void negocioEnSesion(String tenantId) {
        em.createNativeQuery("SELECT set_config('app.tenant_id', :t, true)")
                .setParameter("t", tenantId)
                .getSingleResult();
    }

    private String tenantDe(Long id) {
        return (String) em.createNativeQuery("SELECT tenant_id FROM waiters WHERE id = :id")
                .setParameter("id", id)
                .getSingleResult();
    }

    @Test
    @DisplayName("crear un mesero desde el panel entra, y queda en el negocio de la sesion")
    void elMeseroQuedaEnElNegocioDeLaSesion() {
        negocioEnSesion("shark-burger");

        WaiterEntity angie = new WaiterEntity();
        angie.setName("Angie");
        angie.setActive(true);
        WaiterEntity guardada = waiters.saveAndFlush(angie);

        assertEquals("shark-burger", tenantDe(guardada.getId()),
                "Es el 500 que veia el usuario al crear un mesero");
        assertEquals("shark-burger", guardada.getTenantId(),
                "Y el codigo puede LEER de quien es la fila sin volver a la base: "
                        + "sin @Generated(INSERT) esto seria null y el mapeo, decorativo");
    }

    @Test
    @DisplayName("el mismo codigo sirve para CUALQUIER negocio, no solo para uno")
    void sirveParaCualquierNegocio() {
        // Esto es lo que el default quemado 'shark-burger' hacia imposible: para
        // otro negocio, el tenant no coincidia con el default y el INSERT moria.
        negocioEnSesion("pizzeria-nueva");

        WaiterEntity w = new WaiterEntity();
        w.setName("Carlos");
        w.setActive(true);
        WaiterEntity guardada = waiters.saveAndFlush(w);

        assertEquals("pizzeria-nueva", tenantDe(guardada.getId()),
                "El panel estaba roto de fabrica para todo cliente que no fuera el primero");
    }

    @Test
    @DisplayName("sin negocio en sesion NO se escribe a ciegas")
    void sinNegocioEnSesionNoSeEscribe() {
        // Cadena vacia, que es lo que deja TenantAwareDataSource cuando no hay
        // tenant en contexto — no la deja sin fijar. Sin el `nullif` de V32 la
        // fila entraria con tenant_id = '' porque '' = '' es cierto.
        negocioEnSesion("");

        WaiterEntity huerfana = new WaiterEntity();
        huerfana.setName("__sonda__");
        huerfana.setActive(true);

        Exception e = assertThrows(Exception.class, () -> waiters.saveAndFlush(huerfana),
                "Una fila sin negocio no puede entrar: no seria de nadie");
        assertTrue(causaRaiz(e).toLowerCase().matches(".*(row-level security|null value|not-null).*"),
                "Debe rechazarse por RLS o por NOT NULL, no por otra cosa: " + causaRaiz(e));
    }

    @Test
    @DisplayName("un negocio no ve los meseros de otro")
    void unNegocioNoVeLosDeOtro() {
        negocioEnSesion("negocio-a");
        WaiterEntity a = new WaiterEntity();
        a.setName("De A");
        a.setActive(true);
        waiters.saveAndFlush(a);
        em.clear();

        negocioEnSesion("negocio-b");
        assertTrue(waiters.findAll().isEmpty(),
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
