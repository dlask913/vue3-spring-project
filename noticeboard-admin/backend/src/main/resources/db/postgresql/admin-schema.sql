-- Apply after noticeboard-service/init-postgresql.sql, including to existing PostgreSQL databases.
-- Existing roles and notice visibility values are preserved.
BEGIN;

ALTER TABLE members
    ADD COLUMN IF NOT EXISTS user_role VARCHAR(20) NOT NULL DEFAULT 'ROLE_USER';

ALTER TABLE contents
    ADD COLUMN IF NOT EXISTS hide_yn VARCHAR(1) NOT NULL DEFAULT 'N';

COMMIT;