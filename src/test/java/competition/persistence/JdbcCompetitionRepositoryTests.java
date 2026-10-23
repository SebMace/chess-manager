package competition.persistence;

import competition.ports.CompetitionRepository;
import competition.ports.CompetitionRepositoryContract;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

import javax.sql.DataSource;

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
    protected CompetitionRepository competitions() { return new JdbcCompetitionRepository(JdbcClient.create(dataSource)); }
}
