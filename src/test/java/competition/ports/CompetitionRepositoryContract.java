package competition.ports;

import competition.domain.CompetitionId;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

/** What every CompetitionRepository promises; each implementation runs these tests against itself. */
public abstract class CompetitionRepositoryContract {

    protected abstract CompetitionRepository competitions();

    @Test
    void should_not_find_a_competition_that_was_never_saved() {
        CompetitionRepository competitions = competitions();

        assertEquals(Optional.empty(), competitions.find(new CompetitionId(new UUID(0, 1))));
    }
}
