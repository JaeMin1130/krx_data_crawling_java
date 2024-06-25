CREATE TABLE stock (
    id INT AUTO_INCREMENT PRIMARY KEY,
    company VARCHAR(255) NOT NULL,
    marketCategory VARCHAR(255),
    sector VARCHAR(255),
    close VARCHAR(255),
    volume VARCHAR(255),
    tradingValue VARCHAR(255),
    marketCap VARCHAR(255),
    eps VARCHAR(255),
    pbr VARCHAR(255),
    per VARCHAR(255),
    bps VARCHAR(255),
    dps VARCHAR(255),
    dy VARCHAR(255)
);