-- ============================================
-- V3__patients_cleanup.sql
-- ============================================

-- 1. Drop trigger for PATIENTS only (Hibernate @UpdateTimestamp takes over)
DROP TRIGGER IF EXISTS trg_patients_updated ON patients;

-- 2. Remove DEFAULTs for patient timestamps, java handles it
ALTER TABLE patients
    ALTER COLUMN created_at DROP DEFAULT,
    ALTER COLUMN updated_at DROP DEFAULT;

-- 3. Ensure NOT NULL constraints remain for data integrity
ALTER TABLE patients
    ALTER COLUMN created_at SET NOT NULL,
    ALTER COLUMN updated_at SET NOT NULL;

-- 4. Add audit indexes
CREATE INDEX IF NOT EXISTS idx_patients_created_by ON patients(created_by);
CREATE INDEX IF NOT EXISTS idx_patients_updated_by ON patients(updated_by);

ALTER TABLE patients
  ADD COLUMN IF NOT EXISTS deleted_at TIMESTAMPTZ;