CREATE TABLE tariff
(
    id               UUID PRIMARY KEY             DEFAULT gen_random_uuid(),
    name             VARCHAR(255) UNIQUE NOT NULL,
    kg_rate          NUMERIC(10, 2)      NOT NULL,
    min_price        NUMERIC(10, 2)      NOT NULL,
    cubic_conversion NUMERIC(10, 2)      NOT NULL,
    fragility        NUMERIC(8, 4)       NOT NULL,
    urgency          NUMERIC(8, 4)       NOT NULL,
    active           BOOLEAN             NOT NULL DEFAULT TRUE,
    created          TIMESTAMP,
    updated          TIMESTAMP,
    deleted          BOOLEAN             NOT NULL DEFAULT FALSE
);

CREATE TABLE app_user
(
    id      UUID PRIMARY KEY             DEFAULT gen_random_uuid(),
    chat_id varchar(128) UNIQUE NOT NULL,
    created TIMESTAMP,
    updated TIMESTAMP,
    deleted BOOLEAN             NOT NULL DEFAULT FALSE
);
INSERT INTO tariff (name, kg_rate, min_price, cubic_conversion, fragility, urgency, active)
Values ('number 1', 5., 17., 5000., 20., 20., true)