CREATE TABLE competition_team (
    competition_id UUID    NOT NULL REFERENCES competition (id),
    club_id        UUID    NOT NULL,
    team_number    INTEGER NOT NULL CHECK (team_number >= 1),
    PRIMARY KEY (competition_id, club_id, team_number)
);
