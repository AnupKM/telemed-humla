-- ============================================
-- V1__init_schema.sql
-- Initial schema for Data System
-- ============================================

-- ============================================
-- 1. EXTENSIONS
-- ============================================

CREATE EXTENSION IF NOT EXISTS "pgcrypto";
CREATE EXTENSION IF NOT EXISTS "citext";

-- ============================================
-- 2. COMMON TRIGGER FUNCTION
-- ============================================

CREATE OR REPLACE FUNCTION set_updated_at()
RETURNS TRIGGER AS $$
BEGIN
NEW.updated_at = CURRENT_TIMESTAMP;
RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- ============================================
-- 3. USERS TABLE
-- ============================================

CREATE TABLE users (
id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
email CITEXT NOT NULL UNIQUE,
password_hash VARCHAR(255) NOT NULL,
full_name VARCHAR(255),
is_active BOOLEAN NOT NULL DEFAULT TRUE,
created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TRIGGER trg_users_updated
BEFORE UPDATE ON users
FOR EACH ROW
EXECUTE FUNCTION set_updated_at();

-- ============================================
-- 4. ROLES TABLE
-- ============================================

CREATE TABLE roles (
id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
name VARCHAR(100) NOT NULL UNIQUE,
description VARCHAR(255),
created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Seed roles safely (idempotent)

INSERT INTO roles (name, description)
VALUES
('ADMIN', 'System administrator'),
('USER', 'Standard data collector')
ON CONFLICT (name) DO NOTHING;

-- ============================================
-- 5. USER ROLES (M:N)
-- ============================================

CREATE TABLE user_roles (
user_id UUID NOT NULL,
role_id UUID NOT NULL,
assigned_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
PRIMARY KEY (user_id, role_id),
CONSTRAINT fk_user_roles_user
FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
CONSTRAINT fk_user_roles_role
FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE
);

-- ============================================
-- 6. PATIENTS TABLE
-- ============================================

CREATE TABLE patients (
id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
first_name VARCHAR(150) NOT NULL,
middle_name VARCHAR(150),
last_name VARCHAR(150) NOT NULL,

-- encrypted fields (optional PII)
email_encrypted BYTEA,
phone_encrypted BYTEA,

date_of_birth DATE,
age INTEGER,
height_cm DECIMAL(5,2),
weight_kg DECIMAL(5,2),
created_by UUID NOT NULL,
updated_by UUID,
created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
CONSTRAINT fk_patient_created_by
FOREIGN KEY (created_by) REFERENCES users(id),
CONSTRAINT fk_patient_updated_by
FOREIGN KEY (updated_by) REFERENCES users(id),
CONSTRAINT chk_patient_age
CHECK (age IS NULL OR age BETWEEN 0 AND 130),
CONSTRAINT chk_patient_height
CHECK (height_cm IS NULL OR height_cm > 0),
CONSTRAINT chk_patient_weight
CHECK (weight_kg IS NULL OR weight_kg > 0)
);

CREATE TRIGGER trg_patients_updated
BEFORE UPDATE ON patients
FOR EACH ROW
EXECUTE FUNCTION set_updated_at();

-- ============================================
-- 7. FILES TABLE
-- ============================================
CREATE TABLE files (
id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
patient_id UUID NOT NULL,
file_name VARCHAR(255),
file_type VARCHAR(100),
file_size BIGINT,
storage_url TEXT NOT NULL,
uploaded_by UUID,
created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
CONSTRAINT fk_files_patient
FOREIGN KEY (patient_id) REFERENCES patients(id) ON DELETE CASCADE,
CONSTRAINT fk_files_user
FOREIGN KEY (uploaded_by) REFERENCES users(id)
);

-- ============================================
-- 8. REFRESH TOKENS
-- ============================================
CREATE TABLE refresh_tokens (
id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
user_id UUID NOT NULL,
token_hash VARCHAR(255) NOT NULL,
expires_at TIMESTAMP NOT NULL,
revoked BOOLEAN NOT NULL DEFAULT FALSE,
created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
CONSTRAINT fk_refresh_user
FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- ============================================
-- 9. PERFORMANCE INDEXES
-- ============================================

CREATE INDEX idx_patient_created_by ON patients(created_by);
CREATE INDEX idx_files_patient ON files(patient_id);
CREATE INDEX idx_refresh_user ON refresh_tokens(user_id);