-- ============================================
-- V2__schema_improved.sql
-- Timestamp standardization, soft delete,
-- and authentication security improvements
-- ============================================


-- ============================================
-- 1. CONVERT TIMESTAMP → TIMESTAMPTZ
-- ============================================

-- USERS
ALTER TABLE users
  ALTER COLUMN created_at TYPE TIMESTAMPTZ USING created_at AT TIME ZONE 'UTC',
  ALTER COLUMN updated_at TYPE TIMESTAMPTZ USING updated_at AT TIME ZONE 'UTC';

-- ROLES
ALTER TABLE roles
  ALTER COLUMN created_at TYPE TIMESTAMPTZ USING created_at AT TIME ZONE 'UTC';

-- USER ROLES
ALTER TABLE user_roles
  ALTER COLUMN assigned_at TYPE TIMESTAMPTZ USING assigned_at AT TIME ZONE 'UTC';

-- PATIENTS
ALTER TABLE patients
  ALTER COLUMN created_at TYPE TIMESTAMPTZ USING created_at AT TIME ZONE 'UTC',
  ALTER COLUMN updated_at TYPE TIMESTAMPTZ USING updated_at AT TIME ZONE 'UTC';

-- FILES
ALTER TABLE files
  ALTER COLUMN created_at TYPE TIMESTAMPTZ USING created_at AT TIME ZONE 'UTC';

-- REFRESH TOKENS
ALTER TABLE refresh_tokens
  ALTER COLUMN expires_at TYPE TIMESTAMPTZ USING expires_at AT TIME ZONE 'UTC',
  ALTER COLUMN created_at TYPE TIMESTAMPTZ USING created_at AT TIME ZONE 'UTC';


-- ============================================
-- 2. SOFT DELETE & AUTH SECURITY
-- ============================================

ALTER TABLE users
  ADD COLUMN IF NOT EXISTS deleted_at TIMESTAMPTZ,
  ADD COLUMN IF NOT EXISTS failed_login_attempts INTEGER NOT NULL DEFAULT 0,
  ADD COLUMN IF NOT EXISTS account_locked_until TIMESTAMPTZ,
  ADD COLUMN IF NOT EXISTS last_login_at TIMESTAMPTZ;

ALTER TABLE patients
  ADD COLUMN IF NOT EXISTS deleted_at TIMESTAMPTZ;

ALTER TABLE files
  ADD COLUMN IF NOT EXISTS deleted_at TIMESTAMPTZ;

ALTER TABLE roles
  ADD COLUMN IF NOT EXISTS deleted_at TIMESTAMPTZ;

ALTER TABLE refresh_tokens
  ADD COLUMN IF NOT EXISTS deleted_at TIMESTAMPTZ;


-- ============================================
-- 3. PERFORMANCE INDEXES
-- ============================================

-- Active users email lookup
CREATE INDEX IF NOT EXISTS idx_users_active_email
ON users(email)
WHERE deleted_at IS NULL;

-- Refresh token lookup
CREATE INDEX IF NOT EXISTS idx_refresh_tokens_user
ON refresh_tokens(user_id);

-- Optional active-record indexes
CREATE INDEX IF NOT EXISTS idx_patients_active
ON patients(id)
WHERE deleted_at IS NULL;

CREATE INDEX IF NOT EXISTS idx_files_active
ON files(id)
WHERE deleted_at IS NULL;