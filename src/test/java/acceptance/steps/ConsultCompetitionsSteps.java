package acceptance.steps;

import competition.competitionsofseason.CompetitionsOfSeason;
import competition.competitionsofseason.SeasonCompetitions;
import competition.createcompetition.CreateCompetition;
import competition.domain.Competition;
import competition.domain.CompetitionId;
import competition.domain.League;
import competition.domain.Season;
import competition.ports.InMemoryCompetitionRepository;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ConsultCompetitionsSteps {
    private final InMemoryCompetitionRepository competitions = new InMemoryCompetitionRepository();
    private SeasonCompetitions seen;

    @Given("the competition {string}, organized by the league {string} and played on {int} boards, has been created for the season {int}-{int}")
    public void theCompetitionHasBeenCreated(String name, String league, int boards, int yearBegin, int yearEnd) {
        new CreateCompetition(competitions, new Season(yearBegin, yearEnd), () -> new CompetitionId(UUID.randomUUID()))
                .createCompetition(name, new League(league), boards);
    }

    @When("the administrator consults the competitions of the season {int}-{int}")
    public void theAdministratorConsultsTheCompetitions(int yearBegin, int yearEnd) {
        seen = new CompetitionsOfSeason(competitions, new Season(yearBegin, yearEnd)).competitionsOfCurrentSeason();
    }

    @Then("the administrator sees the competition {string}, organized by the league {string} and played on {int} boards, for the season {int}-{int}")
    public void theAdministratorSeesTheCompetition(String name, String league, int boards, int yearBegin, int yearEnd) {
        assertEquals(new Season(yearBegin, yearEnd), seen.season());
        assertTrue(seen.competitions().stream().anyMatch(competition -> competition.name().equals(name)
                && competition.organizingLeague().equals(new League(league)) && competition.boards() == boards));
    }

    @Then("the administrator sees no competition for the season {int}-{int}")
    public void theAdministratorSeesNoCompetition(int yearBegin, int yearEnd) {
        assertEquals(new Season(yearBegin, yearEnd), seen.season());
        assertTrue(seen.competitions().isEmpty());
    }

    @Then("the administrator sees the competitions in this order: {string}, {string}, {string}")
    public void theAdministratorSeesTheCompetitionsInThisOrder(String first, String second, String third) {
        assertEquals(List.of(first, second, third), seen.competitions().stream().map(Competition::name).toList());
    }
}
