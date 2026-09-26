package adapters.out.persistence;

import domain.club.Club;
import domain.club.vo.ClubId;
import domain.club.vo.CommitteeCode;
import domain.club.vo.FfeClubId;
import domain.club.vo.PostalAddress;
import domain.commune.CommuneCode;
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

    private static final PostalAddress OFFICE = new PostalAddress("12 rue des Échecs", "45000", "Orléans");
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

        clubs.save(new Club(montargis, "Montargis", true, new CommitteeCode("45"), new FfeClubId("G45004"), new CommuneCode("45208"), OFFICE));

        Club club = clubs.find(montargis).orElseThrow();
        assertEquals(montargis, club.id());
        assertEquals("Montargis", club.name());
        assertTrue(club.managedByApplication());
    }

    @Test
    void should_find_a_saved_club_with_its_departmental_committee() {
        JdbcClubRepository clubs = new JdbcClubRepository(JdbcClient.create(dataSource));
        ClubId orleans = new ClubId(UUID.fromString("00000000-0000-0000-0000-000000000007"));

        clubs.save(new Club(orleans, "U.S. Orléans.Echecs", true, new CommitteeCode("45"), new FfeClubId("G45005"), new CommuneCode("45234"), OFFICE));

        assertEquals(Optional.of(new CommitteeCode("45")), clubs.find(orleans).orElseThrow().committee());
    }

    @Test
    void should_find_a_saved_club_with_its_ffe_identity() {
        JdbcClubRepository clubs = new JdbcClubRepository(JdbcClient.create(dataSource));
        ClubId orleans = new ClubId(UUID.fromString("00000000-0000-0000-0000-000000000008"));

        clubs.save(new Club(orleans, "U.S. Orléans.Echecs", true, new CommitteeCode("45"), new FfeClubId("G45001"), new CommuneCode("45234"), OFFICE));

        Club club = clubs.find(orleans).orElseThrow();
        assertEquals(Optional.of(new FfeClubId("G45001")), club.ffeClubId());
        assertEquals(new CommuneCode("45234"), club.commune());
    }

    @Test
    void should_find_a_saved_club_with_its_registered_office() {
        JdbcClubRepository clubs = new JdbcClubRepository(JdbcClient.create(dataSource));
        ClubId loury = new ClubId(UUID.fromString("00000000-0000-0000-0000-000000000013"));
        PostalAddress office = new PostalAddress("3 place de l'Église", "45470", "Loury");

        clubs.save(new Club(loury, "Loury Échecs", true, new CommitteeCode("45"), new FfeClubId("G45008"), new CommuneCode("45188"), office));

        assertEquals(office, clubs.find(loury).orElseThrow().registeredOffice());
    }

    @Test
    void should_tell_whether_a_club_already_uses_an_ffe_identifier() {
        JdbcClubRepository clubs = new JdbcClubRepository(JdbcClient.create(dataSource));
        ClubId gien = new ClubId(UUID.fromString("00000000-0000-0000-0000-000000000009"));

        clubs.save(new Club(gien, "Echiquiers Berry-Sologne", true, new CommitteeCode("45"), new FfeClubId("G45002"), new CommuneCode("45155"), OFFICE));

        assertTrue(clubs.existsWithFfeClubId(new FfeClubId("G45002")));
        assertFalse(clubs.existsWithFfeClubId(new FfeClubId("G45999")));
    }

    @Test
    void should_refuse_to_store_two_clubs_with_the_same_ffe_identifier() {
        JdbcClubRepository clubs = new JdbcClubRepository(JdbcClient.create(dataSource));
        clubs.save(new Club(new ClubId(UUID.fromString("00000000-0000-0000-0000-000000000010")),
                "MJC Chécy", true, new CommitteeCode("45"), new FfeClubId("G45003"), new CommuneCode("45089"), OFFICE));

        assertThrows(DataIntegrityViolationException.class, () -> clubs.save(new Club(
                new ClubId(UUID.fromString("00000000-0000-0000-0000-000000000011")),
                "Chécy Échecs", true, new CommitteeCode("45"), new FfeClubId("G45003"), new CommuneCode("45089"), OFFICE)));
    }

    @Test
    void should_refuse_to_store_a_club_without_its_commune() {
        JdbcClient jdbc = JdbcClient.create(dataSource);

        assertThrows(DataIntegrityViolationException.class, () -> jdbc.sql("""
                        INSERT INTO club (id, name, managed_by_application, committee_code, ffe_club_id,
                                          registered_office_street, registered_office_postcode, registered_office_town)
                        VALUES ('00000000-0000-0000-0000-000000000012', 'Cercle d''Échecs de Pithiviers', TRUE, '45', 'G45007',
                                '1 rue de la Gare', '45300', 'Pithiviers')""")
                .update());
    }

    @Test
    void should_find_nothing_for_an_unknown_club() {
        JdbcClubRepository clubs = new JdbcClubRepository(JdbcClient.create(dataSource));
        ClubId unknown = new ClubId(UUID.fromString("00000000-0000-0000-0000-000000000099"));

        assertTrue(clubs.find(unknown).isEmpty());
    }
}
