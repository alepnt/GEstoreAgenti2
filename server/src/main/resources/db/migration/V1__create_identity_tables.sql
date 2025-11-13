CREATE TABLE IF NOT EXISTS roles (
    id BIGINT IDENTITY PRIMARY KEY,
    name NVARCHAR(100) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS teams (
    id BIGINT IDENTITY PRIMARY KEY,
    name NVARCHAR(100) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS users (
    id BIGINT IDENTITY PRIMARY KEY,
    azure_id NVARCHAR(100) NOT NULL UNIQUE,
    email NVARCHAR(255) NOT NULL,
    display_name NVARCHAR(255) NOT NULL,
    password_hash NVARCHAR(255),
    role_id BIGINT NOT NULL,
    team_id BIGINT NOT NULL,
    active BIT NOT NULL DEFAULT 1,
    created_at DATETIME2 NOT NULL DEFAULT SYSUTCDATETIME(),
    CONSTRAINT fk_users_roles FOREIGN KEY (role_id) REFERENCES roles (id),
    CONSTRAINT fk_users_teams FOREIGN KEY (team_id) REFERENCES teams (id)
);

CREATE TABLE IF NOT EXISTS agents (
    id BIGINT IDENTITY PRIMARY KEY,
    user_id BIGINT NOT NULL,
    agent_code NVARCHAR(64) NOT NULL,
    team_role NVARCHAR(128),
    CONSTRAINT fk_agents_users FOREIGN KEY (user_id) REFERENCES users (id),
    CONSTRAINT uq_agents_code UNIQUE (agent_code)
);

INSERT INTO roles (name)
SELECT 'Agent'
WHERE NOT EXISTS (SELECT 1 FROM roles WHERE name = 'Agent');

INSERT INTO teams (name)
SELECT 'Vendite'
WHERE NOT EXISTS (SELECT 1 FROM teams WHERE name = 'Vendite');
