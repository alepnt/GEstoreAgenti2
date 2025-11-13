CREATE TABLE IF NOT EXISTS contracts (
    id BIGINT IDENTITY PRIMARY KEY,
    agent_id BIGINT NOT NULL,
    customer_name NVARCHAR(255) NOT NULL,
    description NVARCHAR(1000),
    start_date DATE NOT NULL,
    end_date DATE,
    total_value DECIMAL(19, 2) NOT NULL,
    status NVARCHAR(50) NOT NULL,
    CONSTRAINT fk_contracts_agents FOREIGN KEY (agent_id) REFERENCES agents (id)
);

CREATE TABLE IF NOT EXISTS invoices (
    id BIGINT IDENTITY PRIMARY KEY,
    contract_id BIGINT,
    invoice_number NVARCHAR(100) NOT NULL,
    customer_name NVARCHAR(255) NOT NULL,
    amount DECIMAL(19, 2) NOT NULL,
    issue_date DATE NOT NULL,
    due_date DATE,
    status NVARCHAR(50) NOT NULL,
    payment_date DATE,
    notes NVARCHAR(1000),
    created_at DATETIME2 NOT NULL DEFAULT SYSUTCDATETIME(),
    updated_at DATETIME2,
    CONSTRAINT fk_invoices_contract FOREIGN KEY (contract_id) REFERENCES contracts (id)
);

CREATE UNIQUE INDEX IF NOT EXISTS uq_invoices_number ON invoices (invoice_number);

CREATE TABLE IF NOT EXISTS document_history (
    id BIGINT IDENTITY PRIMARY KEY,
    document_type NVARCHAR(50) NOT NULL,
    document_id BIGINT NOT NULL,
    action NVARCHAR(50) NOT NULL,
    description NVARCHAR(1000),
    created_at DATETIME2 NOT NULL DEFAULT SYSUTCDATETIME()
);

CREATE TABLE IF NOT EXISTS commissions (
    id BIGINT IDENTITY PRIMARY KEY,
    agent_id BIGINT NOT NULL,
    contract_id BIGINT NOT NULL,
    total_commission DECIMAL(19, 2) NOT NULL,
    paid_commission DECIMAL(19, 2) NOT NULL,
    pending_commission DECIMAL(19, 2) NOT NULL,
    last_updated DATETIME2 NOT NULL,
    CONSTRAINT fk_commission_contract FOREIGN KEY (contract_id) REFERENCES contracts (id),
    CONSTRAINT fk_commission_agent FOREIGN KEY (agent_id) REFERENCES agents (id),
    CONSTRAINT uq_commission_agent_contract UNIQUE (agent_id, contract_id)
);
