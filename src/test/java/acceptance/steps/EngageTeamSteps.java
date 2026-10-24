package acceptance.steps;

import acceptance.support.CreatedCompetitions;
import competition.domain.ClubId;
import competition.domain.Competition;
import competition.domain.Team;
import competition.engageteam.EngageTeam;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class EngageTeamSteps {
    private final CreatedCompetitions created;
    private final Map<String, ClubId> clubs = new HashMap<>();

    public EngageTeamSteps(CreatedCompetitions created) {
        this.created = created;
    }

    @When("the administrator engages a team of the club {string} in the competition {string}")
    public void theAdministratorEngagesATeam(String club, String competition) {
        new EngageTeam(created.repository()).engageTeam(created.idOf(competition), clubNamed(club));
    }

    @Then("the club {string} has the team {int} engaged in the competition {string}")
    public void theClubHasTheTeamEngaged(String club, int number, String competition) {
        Competition engaged = created.repository().find(created.idOf(competition)).orElseThrow();
        assertTrue(engaged.teams().contains(new Team(clubNamed(club), number)));
    }

    /** The Competition context knows a club by its identifier only: the scenario gives each name its own. */
    private ClubId clubNamed(String name) {
        return clubs.computeIfAbsent(name, ignored -> new ClubId(UUID.randomUUID()));
    }
}
