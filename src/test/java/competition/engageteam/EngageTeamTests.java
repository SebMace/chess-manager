package competition.engageteam;

import competition.domain.ClubId;
import competition.domain.Competition;
import competition.domain.CompetitionId;
import competition.domain.League;
import competition.domain.Season;
import competition.domain.Team;
import competition.ports.InMemoryCompetitionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EngageTeamTests {
    private static final CompetitionId NATIONALE_4 = new CompetitionId(new UUID(0, 1));
    private static final ClubId CAVALIERS = new ClubId(new UUID(1, 1));
    private final InMemoryCompetitionRepository competitions = new InMemoryCompetitionRepository();
    private final EngageTeam engageTeam = new EngageTeam(competitions);

    @BeforeEach
    void theCompetitionExists() {
        competitions.save(new Competition(NATIONALE_4, "Nationale 4", new League("Centre-Val de Loire"),
                new Season(2026, 2027), 6));
    }

    @Test
    void should_tell_the_team_that_the_club_has_engaged() {
        Team team = engageTeam.engageTeam(NATIONALE_4, CAVALIERS);

        assertEquals(new Team(CAVALIERS, 1), team);
    }
}
