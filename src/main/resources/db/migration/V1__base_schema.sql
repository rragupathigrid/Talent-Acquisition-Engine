-- ============================================================
-- V1: Full Base Schema for Talent Acquisition Engine
-- ============================================================

-- Enable pgvector extension (needed for the pgvector image)
CREATE EXTENSION IF NOT EXISTS vector;

-- ============================================================
-- CANDIDATE SERVICE
-- ============================================================

CREATE TABLE IF NOT EXISTS external_candidate (
    candidate_id            BIGSERIAL PRIMARY KEY,
    first_name              VARCHAR(100) NOT NULL,
    last_name               VARCHAR(100) NOT NULL,
    email                   VARCHAR(255) NOT NULL UNIQUE,
    phone                   VARCHAR(20),
    email_hash              VARCHAR(64),
    phone_hash              VARCHAR(64),
    source                  VARCHAR(50),
    current_title           VARCHAR(150),
    current_company         VARCHAR(150),
    total_experience_years  INTEGER,
    summary                 TEXT,
    skills                  TEXT[],
    ai_score                INTEGER,
    gdpr_consent            BOOLEAN DEFAULT FALSE,
    gdpr_consent_at         TIMESTAMP,
    gdpr_delete_requested   BOOLEAN DEFAULT FALSE,
    gdpr_delete_requested_at TIMESTAMP,
    pii_anonymized          BOOLEAN DEFAULT FALSE,
    is_deleted              BOOLEAN DEFAULT FALSE,
    deleted_at              TIMESTAMP,
    linkedin_url            VARCHAR(500),
    portfolio_url           VARCHAR(500),
    resume_url              VARCHAR(500),
    created_at              TIMESTAMP DEFAULT NOW(),
    updated_at              TIMESTAMP DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS address (
    address_id      BIGSERIAL PRIMARY KEY,
    candidate_id    BIGINT NOT NULL REFERENCES external_candidate(candidate_id) ON DELETE CASCADE,
    street1         VARCHAR(255),
    street2         VARCHAR(255),
    city            VARCHAR(100),
    state           VARCHAR(100),
    country         VARCHAR(100),
    zip_code        BIGINT
);

CREATE TABLE IF NOT EXISTS education_detail (
    education_id        BIGSERIAL PRIMARY KEY,
    candidate_id        BIGINT NOT NULL REFERENCES external_candidate(candidate_id) ON DELETE CASCADE,
    institution         VARCHAR(255),
    degree              VARCHAR(100),
    field_of_study      VARCHAR(150),
    start_date          DATE,
    end_date            DATE,
    grade               VARCHAR(50)
);

CREATE TABLE IF NOT EXISTS experience_detail (
    experience_id   BIGSERIAL PRIMARY KEY,
    candidate_id    BIGINT NOT NULL REFERENCES external_candidate(candidate_id) ON DELETE CASCADE,
    company         VARCHAR(255),
    title           VARCHAR(150),
    start_date      DATE,
    end_date        DATE,
    current_job     BOOLEAN DEFAULT FALSE,
    description     TEXT
);

CREATE TABLE IF NOT EXISTS certification_detail (
    certification_id    BIGSERIAL PRIMARY KEY,
    candidate_id        BIGINT NOT NULL REFERENCES external_candidate(candidate_id) ON DELETE CASCADE,
    name                VARCHAR(255),
    issuing_org         VARCHAR(255),
    issue_date          DATE,
    expiry_date         DATE,
    credential_id       VARCHAR(150),
    credential_url      VARCHAR(500)
);

CREATE TABLE IF NOT EXISTS skill_detail (
    skill_id        BIGSERIAL PRIMARY KEY,
    candidate_id    BIGINT NOT NULL REFERENCES external_candidate(candidate_id) ON DELETE CASCADE,
    skill_name      VARCHAR(100) NOT NULL,
    proficiency     VARCHAR(50),
    years_of_exp    INTEGER
);

CREATE TABLE IF NOT EXISTS resume_detail (
    resume_id       BIGSERIAL PRIMARY KEY,
    candidate_id    BIGINT NOT NULL REFERENCES external_candidate(candidate_id) ON DELETE CASCADE,
    file_name       VARCHAR(255),
    file_url        VARCHAR(500),
    parsed_at       TIMESTAMP,
    parse_status    VARCHAR(50)
);

CREATE TABLE IF NOT EXISTS social_links (
    social_links_id BIGSERIAL PRIMARY KEY,
    candidate_id    BIGINT NOT NULL REFERENCES external_candidate(candidate_id) ON DELETE CASCADE,
    platform        VARCHAR(50),
    url             VARCHAR(500)
);

-- ============================================================
-- APPLICATION SERVICE
-- ============================================================

CREATE TABLE IF NOT EXISTS application (
    application_id      BIGSERIAL PRIMARY KEY,
    candidate_id        BIGINT NOT NULL REFERENCES external_candidate(candidate_id),
    demand_id           BIGINT,
    current_stage       VARCHAR(50) NOT NULL DEFAULT 'APPLIED',
    ai_score            INTEGER,
    rejection_reason    TEXT,
    applied_at          TIMESTAMP DEFAULT NOW(),
    created_at          TIMESTAMP DEFAULT NOW(),
    updated_at          TIMESTAMP DEFAULT NOW()
);

-- ============================================================
-- INTERVIEW SERVICE
-- ============================================================

CREATE TABLE IF NOT EXISTS interview (
    interview_id            BIGSERIAL PRIMARY KEY,
    application_id          BIGINT NOT NULL REFERENCES application(application_id),
    interview_type          VARCHAR(50) NOT NULL,
    status                  VARCHAR(50) NOT NULL DEFAULT 'SCHEDULED',
    scheduled_at            TIMESTAMP,
    duration_minutes        INTEGER,
    interviewer_id          BIGINT,
    meeting_link            VARCHAR(500),
    google_calendar_event_id VARCHAR(255),
    notes                   TEXT,
    feedback                TEXT,
    created_at              TIMESTAMP DEFAULT NOW(),
    updated_at              TIMESTAMP DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS scorecard (
    scorecard_id        BIGSERIAL PRIMARY KEY,
    interview_id        BIGINT NOT NULL REFERENCES interview(interview_id),
    application_id      BIGINT NOT NULL REFERENCES application(application_id),
    interviewer_id      BIGINT NOT NULL,
    score               INTEGER NOT NULL CHECK (score BETWEEN 0 AND 100),
    overall_score       INTEGER NOT NULL CHECK (overall_score BETWEEN 0 AND 100),
    competency_ratings  JSONB NOT NULL,
    strengths           TEXT NOT NULL,
    concerns            TEXT,
    comments            TEXT,
    recommendation      VARCHAR(30) NOT NULL,
    submitted_at        TIMESTAMP DEFAULT NOW(),
    CONSTRAINT interview_interviewer_unique UNIQUE (interview_id, interviewer_id)
);

-- ============================================================
-- OFFER SERVICE
-- ============================================================

CREATE TABLE IF NOT EXISTS offer (
    offer_id                BIGSERIAL PRIMARY KEY,
    application_id          BIGINT NOT NULL UNIQUE REFERENCES application(application_id),
    current_approval_step   INTEGER DEFAULT 0,
    role                    VARCHAR(100) NOT NULL,
    base_salary             NUMERIC(15, 2) NOT NULL,
    bonus                   NUMERIC(15, 2),
    equity                  NUMERIC(15, 2),
    joining_date            DATE NOT NULL,
    offer_status            VARCHAR(50) NOT NULL DEFAULT 'DRAFT',
    employment_type         VARCHAR(50) NOT NULL,
    approval_chain          JSONB,
    approved_by             VARCHAR(100),
    rejected_by             VARCHAR(100),
    rejection_reason        TEXT,
    approved_at             TIMESTAMP,
    rejected_at             TIMESTAMP,
    sent_at                 TIMESTAMP,
    signed_at               TIMESTAMP,
    expires_at              TIMESTAMP,
    docu_sign_id            VARCHAR(150),
    created_at              TIMESTAMP DEFAULT NOW(),
    updated_at              TIMESTAMP DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS offer_template (
    template_id         BIGSERIAL PRIMARY KEY,
    template_name       VARCHAR(100) NOT NULL UNIQUE,
    template_content    TEXT NOT NULL,
    active              BOOLEAN NOT NULL DEFAULT TRUE,
    created_at          TIMESTAMP DEFAULT NOW(),
    updated_at          TIMESTAMP DEFAULT NOW()
);

-- ============================================================
-- INDEXES
-- ============================================================

CREATE INDEX IF NOT EXISTS idx_candidate_email ON external_candidate(email);
CREATE INDEX IF NOT EXISTS idx_candidate_email_hash ON external_candidate(email_hash);
CREATE INDEX IF NOT EXISTS idx_candidate_phone_hash ON external_candidate(phone_hash);
CREATE INDEX IF NOT EXISTS idx_application_candidate ON application(candidate_id);
CREATE INDEX IF NOT EXISTS idx_application_demand ON application(demand_id);
CREATE INDEX IF NOT EXISTS idx_application_stage ON application(current_stage);
CREATE INDEX IF NOT EXISTS idx_interview_application ON interview(application_id);
CREATE INDEX IF NOT EXISTS idx_scorecard_interview ON scorecard(interview_id);
CREATE INDEX IF NOT EXISTS idx_offer_application ON offer(application_id);
CREATE INDEX IF NOT EXISTS idx_offer_status ON offer(offer_status);
CREATE INDEX IF NOT EXISTS idx_offer_docu_sign ON offer(docu_sign_id);
