CREATE TABLE club_session (
    club_id        UUID NOT NULL REFERENCES club (id),
    day_of_week    TEXT NOT NULL,
    starts_at      TIME NOT NULL,
    ends_at        TIME NOT NULL,
    activity       TEXT,
    venue_street   TEXT,
    venue_postcode TEXT,
    venue_town     TEXT
);
