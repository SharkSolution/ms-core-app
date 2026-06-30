-- =====================================================================
-- SureSell · ms-core-app · Migración manual (ddl-auto: none)
-- Maestra de empleados + vínculo opcional desde nómina.
-- Ejecutar en Supabase Postgres (DB compartida).
-- =====================================================================

-- 1) Maestra de empleados (salario base predefinido por modalidad)
CREATE TABLE IF NOT EXISTS employees (
    id           BIGSERIAL PRIMARY KEY,
    name         VARCHAR(255)  NOT NULL,
    document_id  VARCHAR(100),
    phone        VARCHAR(50),
    role         VARCHAR(255)  NOT NULL,
    payment_mode VARCHAR(50)   NOT NULL DEFAULT 'PER_SHIFT',  -- PER_SHIFT | PER_HOUR | WITH_BENEFITS | WITHOUT_BENEFITS
    base_salary  NUMERIC(12,2) NOT NULL DEFAULT 0,
    active       BOOLEAN       NOT NULL DEFAULT TRUE,
    created_at   TIMESTAMP     NOT NULL DEFAULT NOW(),
    updated_at   TIMESTAMP
);

-- 2) Vínculo opcional de la nómina a un empleado registrado.
--    Nullable: las nóminas con captura manual quedan con employee_id NULL.
ALTER TABLE payrolls ADD COLUMN IF NOT EXISTS employee_id BIGINT;
