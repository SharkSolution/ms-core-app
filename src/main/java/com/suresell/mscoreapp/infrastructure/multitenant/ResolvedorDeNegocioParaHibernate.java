package com.suresell.mscoreapp.infrastructure.multitenant;

import org.hibernate.context.spi.CurrentTenantIdentifierResolver;

/**
 * Le dice a Hibernate de qué negocio es la sesión actual, para que filtre por
 * `tenant_id` en TODA consulta de las entidades que extienden
 * {@code EntidadDeNegocio}.
 *
 * <h3>Por qué existe: defensa en profundidad</h3>
 *
 * Hasta ahora el aislamiento de este servicio dependía por completo de
 * Row-Level Security en Postgres. Eso es correcto —la frontera real debe estar
 * en la base— pero era la ÚNICA línea de defensa, y tenía dos formas conocidas
 * de desaparecer sin que nada en el código lo impidiera:
 *
 * <ol>
 *   <li>Conectarse con un rol que tenga {@code BYPASSRLS} — por ejemplo el
 *       {@code postgres.<project-ref>} de Supabase, que es justo el que usa el
 *       camino de sincronización de `ms-order-product-mt`
 *       ({@code application.yml:45} de ese repo). Un cambio de variable de
 *       entorno bastaría.</li>
 *   <li>Una política mal escrita. Ya pasó: V28 creó 17 tablas con
 *       {@code USING (true)}, que no aísla nada, y así siguen hasta que se
 *       aplique V33.</li>
 * </ol>
 *
 * Con esto, el {@code WHERE tenant_id = ?} lo pone Hibernate en el SQL. Las dos
 * capas son independientes: para que se filtre información tienen que fallar las
 * dos a la vez.
 *
 * <h3>Sin negocio en sesión devuelve un centinela, no null ni ""</h3>
 *
 * Es el mismo criterio de {@link TenantAwareDataSource}: sin negocio, cero filas.
 * Pero aquí no sirve ninguna de las dos opciones obvias:
 *
 * <ul>
 *   <li>{@code null} → Hibernate lanza
 *       {@code "SessionFactory configured for multi-tenancy, but no tenant
 *       identifier specified"} y no arranca ni el contexto.</li>
 *   <li>{@code ""} → Hibernate lo trata como ausente y lanza lo mismo.
 *       (Comprobado: es el error que dio la primera versión de esta clase.)</li>
 * </ul>
 *
 * Por eso se devuelve {@link #SIN_NEGOCIO}, un valor que ningún negocio real
 * puede tener. Las consultas salen con {@code tenant_id = '__sin_negocio__'} y
 * devuelven vacío. **Falla cerrado.**
 *
 * <p>No se elige un valor "por defecto" tipo 'shark-burger': ese error ya se
 * cometió en V28 y lo corrigió V32.
 */
public class ResolvedorDeNegocioParaHibernate
        implements CurrentTenantIdentifierResolver<String> {

    /**
     * Negocio "ninguno". Los identificadores reales son slugs
     * ({@code shark-burger}, ver {@code V4__auth.sql:14}); este no lo es.
     *
     * <p>Y aunque alguien llegara a crear un negocio con este id exacto, no
     * abriría un agujero: la segunda capa —RLS con {@code app.tenant_id}, que
     * {@link TenantAwareDataSource} fija a {@code ''} cuando no hay sesión—
     * seguiría devolviendo cero filas. Para que se filtrara algo tendrían que
     * fallar las dos capas a la vez, que es justamente lo que este diseño busca
     * hacer improbable.
     */
    static final String SIN_NEGOCIO = "__sin_negocio__";

    @Override
    public String resolveCurrentTenantIdentifier() {
        String negocio = TenantContext.get();
        return (negocio == null || negocio.isBlank()) ? SIN_NEGOCIO : negocio;
    }

    /**
     * {@code false} a propósito: Hibernate no debe reutilizar una sesión abierta
     * con otro negocio. Ponerlo en {@code true} la validaría y la reutilizaría,
     * y en un pool de hilos eso es precisamente la fuga que este mecanismo
     * existe para impedir.
     */
    @Override
    public boolean validateExistingCurrentSessions() {
        return false;
    }

}
