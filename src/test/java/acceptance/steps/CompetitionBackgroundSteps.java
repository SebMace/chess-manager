package acceptance.steps;

import acceptance.support.CreatedCompetitions;
import competition.createcompetition.CreateCompetition;
import competition.domain.CompetitionId;
import competition.domain.League;
import competition.domain.Season;
import io.cucumber.java.en.Given;

import java.util.UUID;

/** The competitions that exist before the scenarios of the Competition context begin. */
public class CompetitionBackgroundSteps {
    private final CreatedCompetitions created;

    public CompetitionBackgroundSteps(CreatedCompetitions created) {
        this.created = created;
    }

    @Given("the competition {string}, organized by the league {string} and played on {int} boards, has been created for the season {int}-{int}")
    public void theCompetitionHasBeenCreated(String name, String league, int boards, int yearBegin, int yearEnd) {
        CreateCompetition createCompetition = new CreateCompetition(created.repository(), new Season(yearBegin, yearEnd),
                () -> new CompetitionId(UUID.randomUUID()));
        created.add(name, createCompetition.createCompetition(name, new League(league), boards));
    }
}
