package acceptance.steps;

import acceptance.support.CreatedCompetitions;
import competition.competitionsofseason.CompetitionsOfSeason;
import competition.competitionsofseason.SeasonCompetitions;
import competition.domain.Competition;
import competition.domain.League;
import competition.domain.Season;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ConsultCompetitionsSteps {
    private final CreatedCompetitions created;
    private SeasonCompetitions seen;

    public ConsultCompetitionsSteps(CreatedCompetitions created) {
        this.created = created;
    }

    @When("the administrator consults the competitions of the season {int}-{int}")
    public void theAdministratorConsultsTheCompetitions(int yearBegin, int yearEnd) {
        seen = new CompetitionsOfSeason(created.repository(), new Season(yearBegin, yearEnd)).competitionsOfCurrentSeason();
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
