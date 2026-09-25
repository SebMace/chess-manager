CREATE TABLE club (
    id                     UUID    PRIMARY KEY,
    name                   TEXT    NOT NULL,
    managed_by_application BOOLEAN NOT NULL
);
