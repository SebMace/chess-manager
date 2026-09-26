ALTER TABLE club
    ADD COLUMN registered_office_street   TEXT NOT NULL,
    ADD COLUMN registered_office_postcode TEXT NOT NULL,
    ADD COLUMN registered_office_town     TEXT NOT NULL;
