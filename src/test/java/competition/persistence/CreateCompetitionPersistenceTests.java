package competition.persistence;

import competition.createcompetition.CreateCompetition;
import competition.domain.Competition;
import competition.domain.CompetitionId;
import competition.domain.Season;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

import javax.sql.DataSource;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

/** A competition created by an administrator is still there when it is read again from PostgreSQL. */
@Testcontainers
class CreateCompetitionPersistenceTests {
    @Container
    static final PostgreSQLContainer postgres = new PostgreSQLContainer("postgres:18");

    private static final Season SEASON = new Season(2026, 2027);
    private static DataSource dataSource;

    @BeforeAll
    static void migrateSchema() {
        dataSource = new DriverManagerDataSource(postgres.getJdbcUrl(), postgres.getUsername(), postgres.getPassword());
        Flyway.configure().dataSource(dataSource).load().migrate();
    }

    @Test
    void should_find_again_a_competition_created_by_an_administrator() {
        CompetitionId id = new CompetitionId(new UUID(0, 1));
        CreateCompetition createCompetition = new CreateCompetition(
                new JdbcCompetitionRepository(JdbcClient.create(dataSource)), SEASON, () -> id);

        createCompetition.createCompetition("Nationale 4 Centre-Val de Loire", 6);

        Competition found = new JdbcCompetitionRepository(JdbcClient.create(dataSource)).find(id).orElseThrow();
        assertEquals("Nationale 4 Centre-Val de Loire", found.name());
        assertEquals(SEASON, found.season());
        assertEquals(6, found.boards());
    }
}
