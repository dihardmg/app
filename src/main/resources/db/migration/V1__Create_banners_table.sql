-- Create banners table
CREATE TABLE banners (
    id SERIAL PRIMARY KEY,
    banner_name VARCHAR(255) NOT NULL,
    banner_image VARCHAR(500) NOT NULL,
    description VARCHAR(1000) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_banners_created_at ON banners(created_at);

-- Insert banner data
INSERT INTO banners (banner_name, banner_image, description, created_at, updated_at) VALUES
('Banner 1', 'https://minio.nutech-integrasi.com/take-home-test/banner/Banner-1.png', 'Lerem Ipsum Dolor sit amet', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Banner 2', 'https://minio.nutech-integrasi.com/take-home-test/banner/Banner-2.png', 'Lerem Ipsum Dolor sit amet', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Banner 3', 'https://minio.nutech-integrasi.com/take-home-test/banner/Banner-3.png', 'Lerem Ipsum Dolor sit amet', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Banner 4', 'https://minio.nutech-integrasi.com/take-home-test/banner/Banner-4.png', 'Lerem Ipsum Dolor sit amet', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Banner 5', 'https://minio.nutech-integrasi.com/take-home-test/banner/Banner-5.png', 'Lerem Ipsum Dolor sit amet', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);