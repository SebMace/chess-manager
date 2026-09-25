package adapters.out.persistence;

import domain.club.Club;
import domain.club.vo.ClubId;
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

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
class JdbcClubRepositoryTests {
    @Container
    static final PostgreSQLContainer postgres = new PostgreSQLContainer("postgres:18");

    private static DataSource dataSource;
    private final ClubId montargis = new ClubId(UUID.fromString("00000000-0000-0000-0000-000000000006"));

    @BeforeAll
    static void migrateSchema() {
        dataSource = new DriverManagerDataSource(postgres.getJdbcUrl(), postgres.getUsername(), postgres.getPassword());
        Flyway.configure().dataSource(dataSource).load().migrate();
    }

    @Test
    void should_find_a_saved_club_managed_by_the_application() {
        JdbcClubRepository clubs = new JdbcClubRepository(JdbcClient.create(dataSource));

        clubs.save(new Club(montargis, "Montargis", true));

        Club club = clubs.find(montargis).orElseThrow();
        assertEquals(montargis, club.id());
        assertEquals("Montargis", club.name());
        assertTrue(club.managedByApplication());
    }

    @Test
    void should_find_nothing_for_an_unknown_club() {
        JdbcClubRepository clubs = new JdbcClubRepository(JdbcClient.create(dataSource));
        ClubId unknown = new ClubId(UUID.fromString("00000000-0000-0000-0000-000000000099"));

        assertTrue(clubs.find(unknown).isEmpty());
    }
}
