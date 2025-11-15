CREATE TABLE IF NOT EXISTS notifications (
    id BIGINT IDENTITY PRIMARY KEY,
    user_id BIGINT NULL,
    team_id BIGINT NULL,
    title NVARCHAR(255) NOT NULL,
    message NVARCHAR(MAX) NOT NULL,
    is_read BIT NOT NULL DEFAULT 0,
    created_at DATETIME2 NOT NULL DEFAULT SYSUTCDATETIME()
);

CREATE TABLE IF NOT EXISTS notification_subscriptions (
    id BIGINT IDENTITY PRIMARY KEY,
    user_id BIGINT NOT NULL,
    channel NVARCHAR(128) NOT NULL,
    created_at DATETIME2 NOT NULL DEFAULT SYSUTCDATETIME()
);

CREATE TABLE IF NOT EXISTS messages (
    id BIGINT IDENTITY PRIMARY KEY,
    conversation_id NVARCHAR(128) NOT NULL,
    sender_id BIGINT NOT NULL,
    team_id BIGINT NULL,
    body NVARCHAR(MAX) NOT NULL,
    created_at DATETIME2 NOT NULL DEFAULT SYSUTCDATETIME()
);

IF NOT EXISTS (SELECT name FROM sys.indexes WHERE name = 'idx_notifications_user')
    CREATE INDEX idx_notifications_user ON notifications(user_id, created_at DESC);

IF NOT EXISTS (SELECT name FROM sys.indexes WHERE name = 'idx_notifications_team')
    CREATE INDEX idx_notifications_team ON notifications(team_id, created_at DESC);

IF NOT EXISTS (SELECT name FROM sys.indexes WHERE name = 'idx_messages_conversation')
    CREATE INDEX idx_messages_conversation ON messages(conversation_id, created_at);
