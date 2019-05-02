DROP SCHEMA IF EXISTS transactional_messaging;
CREATE SCHEMA transactional_messaging;

CREATE TABLE transactional_messaging.outbox
(
    id        VARCHAR PRIMARY KEY NOT NULL,
    sender    VARCHAR(128)        NOT NULL,
    recipient VARCHAR(128)        NOT NULL,
    timestamp TIMESTAMP           NOT NULL,
    headers   TEXT,
    payload   TEXT
);
