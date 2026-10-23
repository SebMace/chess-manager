package competition.competitionsofseason.rest;

import competition.competitionsofseason.CompetitionsOfSeason;
import competition.competitionsofseason.SeasonCompetitions;
import competition.domain.Competition;
import competition.domain.Season;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class CompetitionsOfSeasonController {
    private final CompetitionsOfSeason competitionsOfSeason;

    public CompetitionsOfSeasonController(CompetitionsOfSeason competitionsOfSeason) {
        this.competitionsOfSeason = competitionsOfSeason;
    }

    @GetMapping("/competitions")
    public SeasonCompetitionsResponse ofCurrentSeason() {
        return SeasonCompetitionsResponse.of(competitionsOfSeason.competitionsOfCurrentSeason());
    }

    /** The season comes with the list: a season without any competition is still shown as that season. */
    public record SeasonCompetitionsResponse(SeasonResponse season, List<CompetitionResponse> competitions) {
        static SeasonCompetitionsResponse of(SeasonCompetitions seen) {
            return new SeasonCompetitionsResponse(SeasonResponse.of(seen.season()),
                    seen.competitions().stream().map(CompetitionResponse::of).toList());
        }
    }

    public record SeasonResponse(int yearBegin, int yearEnd) {
        static SeasonResponse of(Season season) {
            return new SeasonResponse(season.yearBegin(), season.yearEnd());
        }
    }

    public record CompetitionResponse(String name, String organizingLeague, int boards) {
        static CompetitionResponse of(Competition competition) {
            return new CompetitionResponse(competition.name(), competition.organizingLeague().name(),
                    competition.boards());
        }
    }
}
