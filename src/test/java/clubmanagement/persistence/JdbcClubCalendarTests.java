package clubmanagement.persistence;

import clubmanagement.domain.club.Club;
import clubmanagement.domain.club.vo.ClubId;
import clubmanagement.domain.club.vo.CommitteeCode;
import clubmanagement.domain.club.vo.FfeClubId;
import clubmanagement.domain.club.vo.PostalAddress;
import clubmanagement.domain.club.vo.Session;
import clubmanagement.domain.commune.CommuneCode;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

import javax.sql.DataSource;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Testcontainers
class JdbcClubCalendarTests {
    @Container
    static final PostgreSQLContainer postgres = new PostgreSQLContainer("postgres:18");

    private static final PostalAddress OFFICE = new PostalAddress("12 rue des Échecs", "45000", "Orléans");
    private static final PostalAddress VENUE = new PostalAddress("5 rue du Roi", "45100", "Orléans");
    private static DataSource dataSource;

    @BeforeAll
    static void migrateSchema() {
        dataSource = new DriverManagerDataSource(postgres.getJdbcUrl(), postgres.getUsername(), postgres.getPassword());
        Flyway.configure().dataSource(dataSource).load().migrate();
    }

    @Test
    void should_read_the_session_defined_for_a_club() {
        ClubId orleans = savedClub("00000000-0000-0000-0000-000000000001", "G45001");
        JdbcClubCalendar clubCalendar = new JdbcClubCalendar(JdbcClient.create(dataSource));
        Session friday = new Session(DayOfWeek.FRIDAY, LocalTime.of(20, 0), LocalTime.of(22, 0));

        clubCalendar.defineOpeningHours(orleans, Set.of(friday));

        assertEquals(Set.of(friday), clubCalendar.openingHoursOf(orleans));
    }

    private static ClubId savedClub(String id, String ffeClubId) {
        ClubId club = new ClubId(UUID.fromString(id));
        new JdbcClubRepository(JdbcClient.create(dataSource)).save(new Club(club, "U.S. Orléans.Echecs", true,
                new CommitteeCode("45"), new FfeClubId(ffeClubId), new CommuneCode("45234"), OFFICE, VENUE));
        return club;
    }
}
