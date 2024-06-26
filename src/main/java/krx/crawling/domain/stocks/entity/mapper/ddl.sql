CREATE TABLE stock (
    id INT PRIMARY KEY AUTO_INCREMENT,
    company VARCHAR(255) NOT NULL,          -- 종목명
    marketcategory VARCHAR(255),           -- 시장구분
    sector VARCHAR(255),                    -- 소속부
    marketcap VARCHAR(255),                -- 시가총액
    volume VARCHAR(255),                    -- 거래량
    tradingvalue VARCHAR(255),             -- 거래대금
    close VARCHAR(255),                     -- 종가
    eps VARCHAR(255),                       -- 주당순이익
    per VARCHAR(255),                       -- 주가수익비율
    bps VARCHAR(255),                       -- 주당순자산가치
    pbr VARCHAR(255),                       -- 주가순자산비율
    dps VARCHAR(255),                       -- 주당배당금
    dy VARCHAR(255),                        -- 배당수익률
    date DATE NOT NULL
);
