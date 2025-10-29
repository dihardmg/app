-- Hapus tabel services jika ada (untuk cleanup)
DROP TABLE IF EXISTS services CASCADE;

-- Buat ulang tabel services
CREATE TABLE services (
    id BIGSERIAL PRIMARY KEY,
    service_code VARCHAR(50) NOT NULL UNIQUE,
    service_name VARCHAR(100) NOT NULL,
    service_icon VARCHAR(500),
    service_tariff DECIMAL(15,2) NOT NULL,
    active BOOLEAN DEFAULT TRUE
);

CREATE INDEX idx_services_service_code ON services (service_code);
CREATE INDEX idx_services_tariff ON services (service_tariff);

-- Insert data services
INSERT INTO services (service_code, service_name, service_icon, service_tariff,active) VALUES
('PAJAK', 'Pajak PBB', 'https://nutech-integrasi.app/dummy.jpg', 40000, TRUE),
('PLN', 'Listrik', 'https://nutech-integrasi.app/dummy.jpg', 10000, TRUE),
('PDAM', 'PDAM Berlangganan', 'https://nutech-integrasi.app/dummy.jpg', 40000, TRUE),
('PULSA', 'Pulsa', 'https://nutech-integrasi.app/dummy.jpg', 40000, TRUE),
('PGN', 'PGN Berlangganan', 'https://nutech-integrasi.app/dummy.jpg', 50000, TRUE),
('MUSIK', 'Musik Berlangganan', 'https://nutech-integrasi.app/dummy.jpg', 50000, TRUE),
('TV', 'TV Berlangganan', 'https://nutech-integrasi.app/dummy.jpg', 50000, TRUE),
('PAKET_DATA', 'Paket data', 'https://nutech-integrasi.app/dummy.jpg', 50000, TRUE),
('VOUCHER_GAME', 'Voucher Game', 'https://nutech-integrasi.app/dummy.jpg', 100000, TRUE),
('VOUCHER_MAKANAN', 'Voucher Makanan', 'https://nutech-integrasi.app/dummy.jpg', 100000, TRUE),
('QURBAN', 'Qurban', 'https://nutech-integrasi.app/dummy.jpg', 200000, TRUE),
('ZAKAT', 'Zakat', 'https://nutech-integrasi.app/dummy.jpg', 300000, TRUE);