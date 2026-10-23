CREATE TABLE competition (
    id                UUID    PRIMARY KEY,
    name              TEXT    NOT NULL,
    season_begin_year INTEGER NOT NULL,
    season_end_year   INTEGER NOT NULL,
    boards            INTEGER NOT NULL
);
