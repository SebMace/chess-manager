package competition.domain;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CompetitionTests {
    private static final CompetitionId ID = new CompetitionId(new UUID(0, 1));
    private static final League LEAGUE = new League("Centre-Val de Loire");
    private static final Season SEASON = new Season(2026, 2027);
    private static final ClubId CAVALIERS = new ClubId(new UUID(1, 1));
    private static final ClubId PITHIVIERS = new ClubId(new UUID(1, 5));

    @ParameterizedTest
    @ValueSource(ints = {0, -1})
    void should_refuse_a_competition_without_board(int boards) {
        assertThrows(CompetitionNeedsAtLeastOneBoard.class,
                () -> new Competition(ID, "Nationale 4", LEAGUE, SEASON, boards));
    }

    @Test
    void should_accept_a_competition_with_a_single_board() {
        assertEquals(1, new Competition(ID, "Nationale 4", LEAGUE, SEASON, 1).boards());
    }

    @Test
    void should_number_the_first_team_of_a_club_1() {
        Competition competition = new Competition(ID, "Nationale 4", LEAGUE, SEASON, 6);

        Team team = competition.engageTeamOf(CAVALIERS);

        assertEquals(new Team(CAVALIERS, 1), team);
        assertEquals(List.of(team), competition.teams());
    }

    @Test
    void should_number_the_second_team_of_a_club_2() {
        Competition competition = new Competition(ID, "Nationale 4", LEAGUE, SEASON, 6);
        Team first = competition.engageTeamOf(CAVALIERS);

        Team second = competition.engageTeamOf(CAVALIERS);

        assertEquals(new Team(CAVALIERS, 2), second);
        assertEquals(List.of(first, second), competition.teams());
    }

    @Test
    void should_number_the_teams_of_each_club_from_1() {
        Competition competition = new Competition(ID, "Nationale 4", LEAGUE, SEASON, 6);
        competition.engageTeamOf(CAVALIERS);
        competition.engageTeamOf(CAVALIERS);

        Team firstOfPithiviers = competition.engageTeamOf(PITHIVIERS);

        assertEquals(new Team(PITHIVIERS, 1), firstOfPithiviers);
    }
}
