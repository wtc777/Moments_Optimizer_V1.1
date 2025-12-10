CREATE TABLE users (
    id VARCHAR(64) NOT NULL PRIMARY KEY,
    phone VARCHAR(32) NOT NULL UNIQUE,
    nickname VARCHAR(128) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    role VARCHAR(32) NOT NULL DEFAULT 'normal',
    credits INT NOT NULL DEFAULT 5,
    created_at DATETIME(3) NOT NULL,
    updated_at DATETIME(3) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE activation_codes (
    id VARCHAR(64) NOT NULL PRIMARY KEY,
    code VARCHAR(64) NOT NULL UNIQUE,
    batch_id VARCHAR(64),
    total_uses INT NOT NULL,
    used_uses INT NOT NULL DEFAULT 0,
    status VARCHAR(32) NOT NULL DEFAULT 'unused',
    expired_at DATETIME(3),
    created_by VARCHAR(64),
    created_at DATETIME(3) NOT NULL,
    updated_at DATETIME(3) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE activation_logs (
    id VARCHAR(64) NOT NULL PRIMARY KEY,
    user_id VARCHAR(64) NOT NULL,
    code_id VARCHAR(64) NOT NULL,
    added_uses INT NOT NULL,
    created_at DATETIME(3) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE INDEX idx_activation_logs_user_created ON activation_logs (user_id, created_at);
CREATE INDEX idx_activation_logs_code ON activation_logs (code_id);

CREATE TABLE analysis_logs (
    id VARCHAR(64) NOT NULL PRIMARY KEY,
    user_id VARCHAR(64) NOT NULL,
    type VARCHAR(64) NOT NULL,
    created_at DATETIME(3) NOT NULL,
    duration_ms INT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE INDEX idx_analysis_logs_user_created ON analysis_logs (user_id, created_at);

CREATE TABLE analysis_history (
    id VARCHAR(64) NOT NULL PRIMARY KEY,
    user_id VARCHAR(64) NOT NULL,
    created_at DATETIME(3) NOT NULL,
    image_path TEXT,
    input_text LONGTEXT,
    output_text LONGTEXT,
    duration_ms INT,
    input_tokens INT,
    output_tokens INT,
    total_tokens INT,
    model_name VARCHAR(128),
    success TINYINT(1) NOT NULL DEFAULT 1,
    error_message TEXT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE INDEX idx_analysis_history_user_created ON analysis_history (user_id, created_at);

CREATE TABLE tasks (
    id VARCHAR(64) NOT NULL PRIMARY KEY,
    type VARCHAR(64) NOT NULL,
    status VARCHAR(16) NOT NULL,
    payload_json LONGTEXT,
    result_json LONGTEXT,
    error_message TEXT,
    created_at DATETIME(3) NOT NULL,
    updated_at DATETIME(3) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE INDEX idx_tasks_status_created ON tasks (status, created_at);

CREATE TABLE task_steps (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    task_id VARCHAR(64) NOT NULL,
    step_order INT NOT NULL,
    step_key VARCHAR(64) NOT NULL,
    step_label VARCHAR(128) NOT NULL,
    status VARCHAR(16) NOT NULL,
    started_at DATETIME(3),
    finished_at DATETIME(3),
    extra_json LONGTEXT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE INDEX idx_task_steps_task ON task_steps (task_id, step_order);
