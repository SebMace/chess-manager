package clubmanagement.persistence;

import clubmanagement.domain.club.Club;
import clubmanagement.domain.club.vo.ClubId;
import clubmanagement.domain.club.vo.CommitteeCode;
import clubmanagement.domain.club.vo.FfeClubId;
import clubmanagement.domain.club.vo.PostalAddress;
import clubmanagement.domain.commune.CommuneCode;
import clubmanagement.domain.member.vo.FfeId;
import clubmanagement.domain.person.Person;
import clubmanagement.domain.person.vo.PersonId;
import clubmanagement.ports.ClubRelationshipRepository;
import clubmanagement.ports.ClubRelationshipRepositoryContract;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

import javax.sql.DataSource;
import java.util.UUID;

@Testcontainers
class JdbcClubRelationshipRepositoryTests extends ClubRelationshipRepositoryContract {
    @Container
    static final PostgreSQLContainer postgres = new PostgreSQLContainer("postgres:18");

    private static final PostalAddress OFFICE = new PostalAddress("12 rue des Échecs", "45000", "Orléans");
    private static final PostalAddress VENUE = new PostalAddress("5 rue du Roi", "45100", "Orléans");
    private static DataSource dataSource;
    private static int created;

    @BeforeAll
    static void migrateSchema() {
        dataSource = new DriverManagerDataSource(postgres.getJdbcUrl(), postgres.getUsername(), postgres.getPassword());
        Flyway.configure().dataSource(dataSource).load().migrate();
    }

    @Override
    protected ClubRelationshipRepository relationships() {
        return new JdbcClubRelationshipRepository(JdbcClient.create(dataSource));
    }

    /** A relationship refers to its club, so the club is saved first, with its own FFE identifier. */
    @Override
    protected ClubId aClub() {
        ClubId club = new ClubId(new UUID(0, ++created));
        new JdbcClubRepository(JdbcClient.create(dataSource)).save(new Club(club, "U.S. Orléans.Echecs", true,
                new CommitteeCode("45"), new FfeClubId("G45%03d".formatted(created)),
                new CommuneCode("45234"), OFFICE, VENUE));
        return club;
    }

    /** A relationship refers to its person, so the person is saved first, with their own FFE identifier. */
    @Override
    protected Person aLicensedPlayer() {
        Person player = Person.licensedPlayer(new PersonId(new UUID(0, ++created)), "Camille", "Martin",
                new FfeId("K%05d".formatted(created)));
        new JdbcPersonRepository(JdbcClient.create(dataSource)).save(player);
        return player;
    }
}
