-- ============================================
-- V4__create_records_schema.sql
-- ============================================

-- ============================================
-- 1. RECORDS TABLE
-- ============================================

CREATE TABLE IF NOT EXISTS records (
    id UUID PRIMARY KEY,
    patient_id UUID NOT NULL,
    patient_history JSONB,
    created_by UUID NOT NULL,
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL,
    deleted_at TIMESTAMPTZ,

    CONSTRAINT fk_records_patient FOREIGN KEY (patient_id) REFERENCES patients(id) ON DELETE RESTRICT,
    CONSTRAINT fk_records_created_by FOREIGN KEY (created_by) REFERENCES users(id)
);

-- ============================================
-- 2. PERFORMANCE INDEXES
-- ============================================

CREATE INDEX IF NOT EXISTS idx_records_patient_active ON records(patient_id) WHERE deleted_at IS NULL;

CREATE INDEX IF NOT EXISTS idx_records_history_gin ON records USING GIN (patient_history);

CREATE INDEX IF NOT EXISTS idx_records_created_by ON records(created_by);