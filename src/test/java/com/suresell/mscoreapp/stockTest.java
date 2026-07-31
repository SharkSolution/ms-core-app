package com.suresell.mscoreapp;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Prueba de humo: que el contexto de Spring levante entero.
 *
 * <p><b>Necesita una base de datos real.</b> No se puede sustituir por H2: este
 * servicio aísla por negocio con Row-Level Security y cada conexión ejecuta
 * {@code set_config('app.tenant_id', ...)}, que sólo existe en PostgreSQL. Un
 * doble en memoria probaría un arranque que no es el que corre en producción.
 *
 * <p>Por eso corre sólo donde hay base configurada. En una máquina sin
 * {@code SPRING_DATASOURCE_URL} se omite en vez de fallar: venía fallando en rojo
 * de forma permanente porque apuntaba a un proyecto de Supabase que ya no existe,
 * y una suite roja de fondo esconde los fallos que sí importan.
 *
 * <p>El aislamiento por negocio en sí está cubierto sin base por
 * {@code infrastructure.multitenant.JwtTenantFilterTest} y
 * {@code TenantAwareDataSourceTest}.
 */
@SpringBootTest
@EnabledIfEnvironmentVariable(
        named = "SPRING_DATASOURCE_URL",
        matches = ".+",
        disabledReason = "Sin base configurada; el arranque real no se puede simular con H2")
class stockTest {

    @Test
    void contextLoads() {
    }

}
