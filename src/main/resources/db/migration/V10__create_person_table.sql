CREATE TABLE person (
    id         UUID PRIMARY KEY,
    first_name TEXT NOT NULL,
    last_name  TEXT NOT NULL,
    ffe_id     TEXT UNIQUE
);
