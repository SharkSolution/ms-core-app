package com.suresell.mscoreapp.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import org.hibernate.annotations.TenantId;

/**
 * Toda fila de este servicio pertenece a UN negocio. Esta clase lo hace visible
 * en el modelo, que hasta ahora no lo era: las 25 entidades no mapeaban
 * `tenant_id` y el codigo se comportaba como si la base fuera de un solo
 * cliente. De ahi salio el 500 al crear un mesero desde el panel.
 *
 * <h3>{@code @TenantId}: el filtro por negocio lo pone Hibernate</h3>
 *
 * Con esta anotacion, Hibernate anade {@code AND tenant_id = ?} al SQL de TODA
 * consulta sobre las entidades que heredan de aqui, tomando el valor de
 * {@link com.suresell.mscoreapp.infrastructure.multitenant.ResolvedorDeNegocioParaHibernate}.
 * Tambien lo escribe en cada INSERT.
 *
 * <p><b>Por que hacia falta, si ya estaba RLS.</b> Porque RLS era la UNICA linea
 * de defensa, y tiene dos formas conocidas de desaparecer sin que el codigo se
 * entere: conectarse con un rol que tenga {@code BYPASSRLS}, o una politica mal
 * escrita —que ya paso: V28 creo 17 tablas con {@code USING (true)}—. Ahora hay
 * dos capas independientes y tienen que fallar las dos para que se filtre algo.
 *
 * <p>Esta anotacion se pone UNA vez, aqui, y aplica a las 25 entidades. Es
 * deliberado: una lista de 25 anotaciones sueltas es una lista de la que algun
 * dia se olvida una, y la que se olvide no dara ningun error — simplemente
 * dejara de aislar.
 *
 * <h3>Por que ya NO es {@code insertable = false}</h3>
 *
 * Antes se mapeaba con {@code insertable = false, updatable = false} para que el
 * valor lo escribiera la BASE, con el default que dejo V32:
 *
 * <pre>DEFAULT nullif(current_setting('app.tenant_id', true), '')</pre>
 *
 * El razonamiento era: si lo escribe la base, no depende del codigo. Sigue
 * siendo un buen razonamiento, pero es incompatible con {@code @TenantId}, que
 * necesita gestionar la columna para poder filtrar por ella.
 *
 * <p><b>Que se pierde y que se gana.</b> Se pierde que Java no pueda escribir un
 * tenant equivocado. Se gana que Java filtre por tenant al LEER, que es el
 * agujero que de verdad estaba abierto: escribir mal nunca fue el problema
 * —el {@code WITH CHECK} de la politica lo rechaza—, mientras que leer de mas
 * no lo impedia nada en el codigo.
 *
 * <p>Y el riesgo de escribir en el negocio equivocado sigue cubierto por la base:
 * el {@code WITH CHECK} de las 22 tablas con politica real rechaza cualquier fila
 * cuyo {@code tenant_id} no coincida con {@code app.tenant_id}. Como los dos
 * mecanismos leen del mismo {@link com.suresell.mscoreapp.infrastructure.multitenant.TenantContext},
 * siempre coinciden; si algun dia divergieran por un bug, la fila se rechaza en
 * vez de escribirse mal.
 *
 * <p>El {@code DEFAULT} de V32 se queda en la base y no estorba: un INSERT que
 * manda la columna explicitamente lo pisa, y sigue protegiendo a cualquier otro
 * escritor que la omita.
 *
 * <p>Lo cubre {@code PanelEscribeEnElNegocioCorrectoTest} contra un Postgres
 * real, y {@code FiltroDeNegocioEnElCodigoTest} comprueba que el filtro de
 * aplicacion aisla POR SI SOLO, con la politica de base abierta.
 */
@MappedSuperclass
public abstract class EntidadDeNegocio {

    /**
     * De que negocio es esta fila. La gestiona Hibernate via {@code @TenantId}:
     * la escribe al insertar y la usa como filtro al consultar. No se asigna a
     * mano.
     */
    @TenantId
    @Column(name = "tenant_id")
    private String tenantId;

    /** De que negocio es esta fila. */
    public String getTenantId() {
        return tenantId;
    }
}
