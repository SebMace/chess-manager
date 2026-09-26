package adapters.out.persistence;

import domain.club.Club;
import domain.club.vo.ClubId;
import domain.club.vo.CommitteeCode;
import domain.club.vo.FfeClubId;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

import javax.sql.DataSource;
import java.util.Optional;
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

        clubs.save(new Club(montargis, "Montargis", true, new CommitteeCode("45"), new FfeClubId("G45004"), "Montargis"));

        Club club = clubs.find(montargis).orElseThrow();
        assertEquals(montargis, club.id());
        assertEquals("Montargis", club.name());
        assertTrue(club.managedByApplication());
    }

    @Test
    void should_find_a_saved_club_with_its_departmental_committee() {
        JdbcClubRepository clubs = new JdbcClubRepository(JdbcClient.create(dataSource));
        ClubId orleans = new ClubId(UUID.fromString("00000000-0000-0000-0000-000000000007"));

        clubs.save(new Club(orleans, "U.S. Orléans.Echecs", true, new CommitteeCode("45"), new FfeClubId("G45005"), "Orléans"));

        assertEquals(Optional.of(new CommitteeCode("45")), clubs.find(orleans).orElseThrow().committee());
    }

    @Test
    void should_find_a_saved_club_with_its_ffe_identity() {
        JdbcClubRepository clubs = new JdbcClubRepository(JdbcClient.create(dataSource));
        ClubId orleans = new ClubId(UUID.fromString("00000000-0000-0000-0000-000000000008"));

        clubs.save(new Club(orleans, "U.S. Orléans.Echecs", true, new CommitteeCode("45"), new FfeClubId("G45001"), "Orléans"));

        Club club = clubs.find(orleans).orElseThrow();
        assertEquals(Optional.of(new FfeClubId("G45001")), club.ffeClubId());
        assertEquals(Optional.of("Orléans"), club.commune());
    }

    @Test
    void should_tell_whether_a_club_already_uses_an_ffe_identifier() {
        JdbcClubRepository clubs = new JdbcClubRepository(JdbcClient.create(dataSource));
        ClubId gien = new ClubId(UUID.fromString("00000000-0000-0000-0000-000000000009"));

        clubs.save(new Club(gien, "Echiquiers Berry-Sologne", true, new CommitteeCode("45"), new FfeClubId("G45002"), "Gien"));

        assertTrue(clubs.existsWithFfeClubId(new FfeClubId("G45002")));
        assertFalse(clubs.existsWithFfeClubId(new FfeClubId("G45999")));
    }

    @Test
    void should_refuse_to_store_two_clubs_with_the_same_ffe_identifier() {
        JdbcClubRepository clubs = new JdbcClubRepository(JdbcClient.create(dataSource));
        clubs.save(new Club(new ClubId(UUID.fromString("00000000-0000-0000-0000-000000000010")),
                "MJC Chécy", true, new CommitteeCode("45"), new FfeClubId("G45003"), "Chécy"));

        assertThrows(DataIntegrityViolationException.class, () -> clubs.save(new Club(
                new ClubId(UUID.fromString("00000000-0000-0000-0000-000000000011")),
                "Chécy Échecs", true, new CommitteeCode("45"), new FfeClubId("G45003"), "Chécy")));
    }

    @Test
    void should_find_nothing_for_an_unknown_club() {
        JdbcClubRepository clubs = new JdbcClubRepository(JdbcClient.create(dataSource));
        ClubId unknown = new ClubId(UUID.fromString("00000000-0000-0000-0000-000000000099"));

        assertTrue(clubs.find(unknown).isEmpty());
    }
}
