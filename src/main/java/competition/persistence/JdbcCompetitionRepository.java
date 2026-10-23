package competition.persistence;

import competition.domain.Competition;
import competition.domain.CompetitionId;
import competition.ports.CompetitionRepository;
import org.springframework.jdbc.core.simple.JdbcClient;

import java.util.Optional;

/** Compile-only skeleton: its behavior is driven by the contract tests of CompetitionRepository. */
public class JdbcCompetitionRepository implements CompetitionRepository {
    public JdbcCompetitionRepository(JdbcClient jdbc) {
    }

    @Override
    public Optional<Competition> find(CompetitionId competitionId) {
        throw new UnsupportedOperationException("not implemented yet");
    }

    @Override
    public void save(Competition competition) {
        throw new UnsupportedOperationException("not implemented yet");
    }
}
