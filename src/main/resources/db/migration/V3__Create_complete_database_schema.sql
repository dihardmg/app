-- Create users table
CREATE TABLE users (
                       id SERIAL PRIMARY KEY,
                       email VARCHAR(255) NOT NULL UNIQUE,
                       password VARCHAR(255) NOT NULL,
                       first_name VARCHAR(100) NOT NULL,
                       last_name VARCHAR(100) NOT NULL,
                       profile_image VARCHAR(500),
                       created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                       updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_created_at ON users(created_at);

-- Create balances table
CREATE TABLE balances (
                          id SERIAL PRIMARY KEY,
                          user_id INTEGER NOT NULL UNIQUE,
                          balance DECIMAL(15,2) NOT NULL DEFAULT 0.00,
                          created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                          updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                          CONSTRAINT fk_balances_user_id FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE INDEX idx_balance_user_id ON balances(user_id);
CREATE INDEX idx_balance_amount ON balances(balance);

-- Create transactions table
CREATE TABLE transactions (
                              id SERIAL PRIMARY KEY,
                              user_id INTEGER NOT NULL,
                              invoice_number VARCHAR(50) NOT NULL UNIQUE,
                              transaction_type VARCHAR(20) NOT NULL,
                              service_code VARCHAR(50),
                              description VARCHAR(200),
                              total_amount DECIMAL(15,2) NOT NULL,
                              created_on TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                              CONSTRAINT fk_transactions_user_id FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE INDEX idx_transactions_user_id ON transactions(user_id);
CREATE INDEX idx_transactions_created_on ON transactions(created_on);
CREATE INDEX idx_transactions_invoice_number ON transactions(invoice_number);
CREATE INDEX idx_transactions_type ON transactions(transaction_type);
