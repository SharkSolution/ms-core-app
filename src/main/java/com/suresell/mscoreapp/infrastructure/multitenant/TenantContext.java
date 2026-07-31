package com.suresell.mscoreapp.infrastructure.multitenant;

/**
 * Tenant del request en curso. Lo fija {@link JwtTenantFilter} desde el JWT y lo
 * consume {@link TenantAwareDataSource} al entregar cada conexión.
 *
 * <p>ThreadLocal porque Tomcat atiende un request por hilo. Se limpia SIEMPRE en
 * el {@code finally} del filtro: sin eso, un hilo reutilizado del pool
 * arrastraría el tenant del request anterior — que es exactamente la fuga que
 * este mecanismo existe para impedir.
 */
public final class TenantContext {

    private static final ThreadLocal<String> ACTUAL = new ThreadLocal<>();

    private TenantContext() {
    }

    public static void set(String tenantId) {
        ACTUAL.set(tenantId);
    }

    public static String get() {
        return ACTUAL.get();
    }

    public static void clear() {
        ACTUAL.remove();
    }
}
