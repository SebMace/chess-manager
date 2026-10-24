package competition.persistence;

import competition.domain.ClubId;
import competition.domain.Competition;
import competition.domain.CompetitionId;
import competition.domain.League;
import competition.domain.Season;
import competition.domain.Team;
import competition.ports.CompetitionRepository;
import competition.ports.CompetitionRepositoryContract;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.transaction.support.TransactionTemplate;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

import javax.sql.DataSource;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Testcontainers
class JdbcCompetitionRepositoryTests extends CompetitionRepositoryContract {
    @Container
    static final PostgreSQLContainer postgres = new PostgreSQLContainer("postgres:18");

    private static DataSource dataSource;

    @BeforeAll
    static void migrateSchema() {
        dataSource = new DriverManagerDataSource(postgres.getJdbcUrl(), postgres.getUsername(), postgres.getPassword());
        Flyway.configure().dataSource(dataSource).load().migrate();
    }

    @Override
    protected CompetitionRepository competitions() {
        return new JdbcCompetitionRepository(JdbcClient.create(dataSource),
                new TransactionTemplate(new DataSourceTransactionManager(dataSource)));
    }

    @Test
    void should_not_save_a_competition_whose_teams_cannot_all_be_saved() {
        CompetitionRepository competitions = competitions();
        CompetitionId id = new CompetitionId(new UUID(0, 11));
        Team team = new Team(new ClubId(new UUID(1, 1)), 1);
        // The same team twice breaks the primary key of the teams, after the competition itself was inserted.
        Competition competition = Competition.reconstitute(id, "Nationale 4", new League("Centre-Val de Loire"),
                new Season(2026, 2027), 6, List.of(team, team));

        assertThrows(DuplicateKeyException.class, () -> competitions.save(competition));

        assertEquals(Optional.empty(), competitions.find(id));
    }
}
