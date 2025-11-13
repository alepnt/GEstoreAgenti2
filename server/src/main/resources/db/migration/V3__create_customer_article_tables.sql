IF OBJECT_ID('customers', 'U') IS NULL
BEGIN
    CREATE TABLE customers (
        id BIGINT IDENTITY PRIMARY KEY,
        name NVARCHAR(255) NOT NULL,
        vat_number NVARCHAR(50),
        tax_code NVARCHAR(50),
        email NVARCHAR(255),
        phone NVARCHAR(50),
        address NVARCHAR(500),
        created_at DATETIME2 NOT NULL DEFAULT SYSUTCDATETIME(),
        updated_at DATETIME2
    );

    CREATE UNIQUE INDEX uq_customers_vat_number ON customers (vat_number) WHERE vat_number IS NOT NULL;
    CREATE UNIQUE INDEX uq_customers_tax_code ON customers (tax_code) WHERE tax_code IS NOT NULL;
END;

IF OBJECT_ID('articles', 'U') IS NULL
BEGIN
    CREATE TABLE articles (
        id BIGINT IDENTITY PRIMARY KEY,
        code NVARCHAR(100) NOT NULL,
        name NVARCHAR(255) NOT NULL,
        description NVARCHAR(1000),
        unit_price DECIMAL(19, 4) NOT NULL,
        vat_rate DECIMAL(5, 2) NOT NULL,
        unit_of_measure NVARCHAR(50),
        created_at DATETIME2 NOT NULL DEFAULT SYSUTCDATETIME(),
        updated_at DATETIME2
    );

    CREATE UNIQUE INDEX uq_articles_code ON articles (code);
END;

IF COL_LENGTH('invoices', 'customer_id') IS NULL
BEGIN
    ALTER TABLE invoices ADD customer_id BIGINT NULL;
    ALTER TABLE invoices ADD CONSTRAINT fk_invoices_customer FOREIGN KEY (customer_id) REFERENCES customers (id);
END;

IF OBJECT_ID('invoice_lines', 'U') IS NULL
BEGIN
    CREATE TABLE invoice_lines (
        id BIGINT IDENTITY PRIMARY KEY,
        invoice_id BIGINT NOT NULL,
        article_id BIGINT,
        article_code NVARCHAR(100),
        description NVARCHAR(1000) NOT NULL,
        quantity DECIMAL(19, 4) NOT NULL,
        unit_price DECIMAL(19, 4) NOT NULL,
        vat_rate DECIMAL(5, 2) NOT NULL,
        total DECIMAL(19, 4) NOT NULL,
        CONSTRAINT fk_invoice_lines_invoice FOREIGN KEY (invoice_id) REFERENCES invoices (id) ON DELETE CASCADE,
        CONSTRAINT fk_invoice_lines_article FOREIGN KEY (article_id) REFERENCES articles (id)
    );

    CREATE INDEX ix_invoice_lines_invoice ON invoice_lines (invoice_id);
END;
