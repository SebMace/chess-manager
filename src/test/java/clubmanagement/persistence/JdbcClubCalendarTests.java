package clubmanagement.persistence;

import clubmanagement.domain.club.Club;
import clubmanagement.domain.club.vo.ClubId;
import clubmanagement.domain.club.vo.CommitteeCode;
import clubmanagement.domain.club.vo.FfeClubId;
import clubmanagement.domain.club.vo.PostalAddress;
import clubmanagement.domain.commune.CommuneCode;
import clubmanagement.ports.ClubCalendar;
import clubmanagement.ports.ClubCalendarContract;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.transaction.support.TransactionTemplate;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

import javax.sql.DataSource;
import java.util.UUID;

@Testcontainers
class JdbcClubCalendarTests extends ClubCalendarContract {
    @Container
    static final PostgreSQLContainer postgres = new PostgreSQLContainer("postgres:18");

    private static final PostalAddress OFFICE = new PostalAddress("12 rue des Échecs", "45000", "Orléans");
    private static final PostalAddress VENUE = new PostalAddress("5 rue du Roi", "45100", "Orléans");
    private static DataSource dataSource;
    private static int createdClubs;

    @BeforeAll
    static void migrateSchema() {
        dataSource = new DriverManagerDataSource(postgres.getJdbcUrl(), postgres.getUsername(), postgres.getPassword());
        Flyway.configure().dataSource(dataSource).load().migrate();
    }

    @Override
    protected ClubCalendar clubCalendar() { return new JdbcClubCalendar(JdbcClient.create(dataSource),
                new TransactionTemplate(new DataSourceTransactionManager(dataSource))); }

    /** The sessions of a club refer to it, so the club is saved first, with its own FFE identifier. */
    @Override
    protected ClubId aClub() {
        ClubId club = new ClubId(new UUID(0, ++createdClubs));
        new JdbcClubRepository(JdbcClient.create(dataSource)).save(new Club(club, "U.S. Orléans.Echecs", true,
                new CommitteeCode("45"), new FfeClubId("G45%03d".formatted(createdClubs)),
                new CommuneCode("45234"), OFFICE, VENUE));
        return club;
    }
}
