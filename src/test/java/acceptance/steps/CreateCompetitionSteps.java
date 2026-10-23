package acceptance.steps;

import competition.createcompetition.CreateCompetition;
import competition.domain.Competition;
import competition.domain.CompetitionId;
import competition.domain.Season;
import competition.ports.InMemoryCompetitionRepository;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CreateCompetitionSteps {
    private static final CompetitionId NEW_COMPETITION_ID = new CompetitionId(new UUID(0, 1));
    private final InMemoryCompetitionRepository competitions = new InMemoryCompetitionRepository();
    private Season currentSeason;
    private CompetitionId createdCompetition;

    @Given("the current season is {int}-{int}")
    public void theCurrentSeasonIs(int yearBegin, int yearEnd) {
        currentSeason = new Season(yearBegin, yearEnd);
    }

    @When("an administrator creates the competition {string} played on {int} boards")
    public void administratorCreatesTheCompetition(String name, int boards) {
        CreateCompetition createCompetition = new CreateCompetition(competitions, currentSeason, () -> NEW_COMPETITION_ID);
        createdCompetition = createCompetition.createCompetition(name, boards);
    }

    @Then("the administrator is told that the competition {string} has been created for the season {int}-{int}")
    public void administratorIsToldThatTheCompetitionHasBeenCreated(String name, int yearBegin, int yearEnd) {
        Competition competition = competitions.find(createdCompetition).orElseThrow();
        assertEquals(name, competition.name());
        assertEquals(new Season(yearBegin, yearEnd), competition.season());
    }

    @Then("the competition {string} is played on {int} boards")
    public void competitionIsPlayedOn(String name, int boards) {
        Competition competition = competitions.find(createdCompetition).orElseThrow();
        assertEquals(name, competition.name());
        assertEquals(boards, competition.boards());
    }
}
