package competition.competitionsofseason;

import competition.domain.Competition;
import competition.domain.Season;
import competition.ports.CompetitionRepository;

import java.text.Collator;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class CompetitionsOfSeason {
    // The primary strength ignores accents and capitals: "Élite" comes between "départementale" and "Nationale".
    private static final Comparator<Competition> BY_NAME =
            Comparator.comparing(Competition::name, frenchIgnoringAccentsAndCapitals());
    private final CompetitionRepository competitions;
    private final Season currentSeason;

    public CompetitionsOfSeason(CompetitionRepository competitions, Season currentSeason) {
        this.competitions = competitions;
        this.currentSeason = currentSeason;
    }

    public SeasonCompetitions competitionsOfCurrentSeason() {
        List<Competition> ofSeason = competitions.inSeason(currentSeason).stream()
                .sorted(BY_NAME)
                .toList();
        return new SeasonCompetitions(currentSeason, ofSeason);
    }

    private static Collator frenchIgnoringAccentsAndCapitals() {
        Collator collator = Collator.getInstance(Locale.FRENCH);
        collator.setStrength(Collator.PRIMARY);
        return collator;
    }
}
