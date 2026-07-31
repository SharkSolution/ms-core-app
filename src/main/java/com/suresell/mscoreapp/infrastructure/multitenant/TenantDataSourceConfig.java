package com.suresell.mscoreapp.infrastructure.multitenant;

import javax.sql.DataSource;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.Configuration;

/**
 * Envuelve el DataSource principal en un {@link TenantAwareDataSource}, para que
 * TODA consulta del servicio salga con su `app.tenant_id` fijado — sin tener que
 * tocar ni un repositorio.
 */
@Configuration
public class TenantDataSourceConfig implements BeanPostProcessor {

    @Override
    public Object postProcessAfterInitialization(Object bean, String nombre) {
        if (bean instanceof DataSource ds && !(bean instanceof TenantAwareDataSource)) {
            return new TenantAwareDataSource(ds);
        }
        return bean;
    }
}
