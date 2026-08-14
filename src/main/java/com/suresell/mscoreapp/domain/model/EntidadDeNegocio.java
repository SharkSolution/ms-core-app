package com.suresell.mscoreapp.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import org.hibernate.annotations.Generated;
import org.hibernate.generator.EventType;

/**
 * Toda fila de este servicio pertenece a UN negocio. Esta clase lo hace visible
 * en el modelo, que hasta ahora no lo era: las 25 entidades no mapeaban
 * `tenant_id` y el codigo se comportaba como si la base fuera de un solo
 * cliente. De ahi salio el 500 al crear un mesero desde el panel.
 *
 * <h3>Por que es de SOLO LECTURA</h3>
 *
 * El campo se mapea con {@code insertable = false, updatable = false} a
 * proposito. Quien escribe `tenant_id` es la BASE, con el valor por defecto que
 * dejo V32:
 *
 * <pre>DEFAULT nullif(current_setting('app.tenant_id', true), '')</pre>
 *
 * <p>Que lo escriba la base y no Java no es comodidad, es donde tiene que
 * estar: RLS es la frontera real justamente porque no depende del codigo de la
 * aplicacion. Si Java tambien lo escribiera habria dos fuentes de verdad para
 * el mismo dato, y la que manda seguiria siendo la de abajo. Ademas, un INSERT
 * que mande el tenant explicitamente PISA el default: bastaria un bug en un
 * listener para escribir en el negocio equivocado, que es exactamente lo que
 * RLS existe para impedir.
 *
 * <p>Con {@code insertable = false} el INSERT sigue omitiendo la columna —igual
 * que hoy, sin cambiar el comportamiento— pero el codigo ya puede LEER de quien
 * es cada fila.
 *
 * <p>Lo cubre {@code PanelEscribeEnElNegocioCorrectoTest} contra un Postgres
 * real: escribe, escribe en el negocio correcto, sirve para cualquier negocio
 * (no solo el primero) y sin negocio en sesion no escribe nada.
 */
@MappedSuperclass
public abstract class EntidadDeNegocio {

    /**
     * El {@code @Generated(INSERT)} no es adorno: sin el, con
     * {@code insertable = false} Hibernate nunca vuelve a leer la columna y
     * {@code getTenantId()} devuelve null justo despues de guardar, que es
     * cuando mas se necesita. Con el, la relee del INSERT y el valor esta
     * disponible enseguida.
     */
    @Generated(event = EventType.INSERT)
    @Column(name = "tenant_id", insertable = false, updatable = false)
    private String tenantId;

    /** De que negocio es esta fila. La escribe la base; aca solo se lee. */
    public String getTenantId() {
        return tenantId;
    }
}
