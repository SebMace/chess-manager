ALTER TABLE club
    ADD COLUMN playing_venue_street   TEXT NOT NULL,
    ADD COLUMN playing_venue_postcode TEXT NOT NULL,
    ADD COLUMN playing_venue_town     TEXT NOT NULL;
