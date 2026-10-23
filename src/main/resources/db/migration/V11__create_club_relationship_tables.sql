CREATE TABLE club_relationship (
    person_id UUID NOT NULL REFERENCES person (id),
    club_id   UUID NOT NULL REFERENCES club (id),
    status    TEXT NOT NULL,
    PRIMARY KEY (person_id, club_id)
);

CREATE TABLE club_relationship_license (
    person_id         UUID    NOT NULL,
    club_id           UUID    NOT NULL,
    season_begin_year INTEGER NOT NULL,
    season_end_year   INTEGER NOT NULL,
    license_type      TEXT    NOT NULL,
    PRIMARY KEY (person_id, club_id, season_begin_year, season_end_year),
    FOREIGN KEY (person_id, club_id) REFERENCES club_relationship (person_id, club_id)
);
