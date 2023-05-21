CREATE TABLE IF NOT EXISTS balance_increasing_job_lock(
    id BIGSERIAL PRIMARY KEY,
    pod_name TEXT NOT NULL,
    acquired_at TIMESTAMP DEFAULT NOW(),
    last_ttl TIMESTAMP DEFAULT NOW()
);