-- Hapus tabel services jika ada (untuk cleanup)
DROP TABLE IF EXISTS services CASCADE;

-- Buat ulang tabel services
CREATE TABLE services (
                          id BIGSERIAL PRIMARY KEY,
                          service_code VARCHAR(50) NOT NULL UNIQUE,
                          service_name VARCHAR(100) NOT NULL,
                          service_icon VARCHAR(500),
                          service_tariff DECIMAL(19,2) NOT NULL,
                          active BOOLEAN DEFAULT TRUE,
                          created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                          updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_services_service_code ON services (service_code);
CREATE INDEX idx_services_tariff ON services (service_tariff);

-- Insert data services
INSERT INTO services (service_code, service_name, service_icon, service_tariff,active) VALUES
                                                                                           ('PAJAK', 'Pajak PBB', 'https://nutech-integrasi.app/dummy.jpg', 40.00, TRUE),
                                                                                           ('PLN', 'Listrik Prabayar', 'https://nutech-integrasi.app/dummy.jpg', 10.00, TRUE),
                                                                                           ('PDAM', 'PDAM Berlangganan', 'https://nutech-integrasi.app/dummy.jpg', 40.00, TRUE),
                                                                                           ('PULSA', 'Pulsa Indosat', 'https://nutech-integrasi.app/dummy.jpg', 40.00, TRUE),
                                                                                           ('PGN', 'PGN Berlangganan', 'https://nutech-integrasi.app/dummy.jpg', 50.00, TRUE),
                                                                                           ('MUSIK', 'Musik Berlangganan', 'https://nutech-integrasi.app/dummy.jpg', 50.00, TRUE),
                                                                                           ('TV', 'TV Berlangganan', 'https://nutech-integrasi.app/dummy.jpg', 50.00, TRUE),
                                                                                           ('PAKET_DATA', 'Paket data', 'https://nutech-integrasi.app/dummy.jpg', 50.00, TRUE),
                                                                                           ('VOUCHER_GAME', 'Voucher Game', 'https://nutech-integrasi.app/dummy.jpg', 50.00, TRUE),
                                                                                           ('VOUCHER_MAKANAN', 'Voucher Makanan', 'https://nutech-integrasi.app/dummy.jpg', 100.00, TRUE),
                                                                                           ('QURBAN', 'Qurban', 'https://nutech-integrasi.app/dummy.jpg', 200.00, TRUE),
                                                                                           ('ZAKAT', 'Zakat', 'https://nutech-integrasi.app/dummy.jpg', 300.00, TRUE);