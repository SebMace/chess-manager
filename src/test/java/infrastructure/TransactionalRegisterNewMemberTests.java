package infrastructure;

import clubmanagement.domain.club.ClubRelationship;
import clubmanagement.domain.club.vo.ClubId;
import clubmanagement.domain.club.vo.Season;
import clubmanagement.domain.member.vo.FfeId;
import clubmanagement.domain.member.vo.FfeLicenseType;
import clubmanagement.domain.person.vo.PersonId;
import clubmanagement.persistence.JdbcClubRelationshipRepository;
import clubmanagement.persistence.JdbcPersonRepository;
import clubmanagement.ports.ClubRelationshipRepository;
import clubmanagement.registermember.RegisterNewMember;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.transaction.support.TransactionTemplate;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

import javax.sql.DataSource;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/** The player and their membership are registered together, or not at all. */
@Testcontainers
class TransactionalRegisterNewMemberTests {
    @Container
    static final PostgreSQLContainer postgres = new PostgreSQLContainer("postgres:18");

    private static DataSource dataSource;

    @BeforeAll
    static void migrateSchema() {
        dataSource = new DriverManagerDataSource(postgres.getJdbcUrl(), postgres.getUsername(), postgres.getPassword());
        Flyway.configure().dataSource(dataSource).load().migrate();
    }

    @Test
    void a_registration_that_fails_before_its_end_leaves_no_trace_of_the_player() {
        JdbcClient jdbc = JdbcClient.create(dataSource);
        ClubRelationshipRepository connectionLostWhileSavingTheMembership = new JdbcClubRelationshipRepository(jdbc) {
            @Override
            public void save(ClubRelationship relationship) {
                throw new IllegalStateException("The connection was lost");
            }
        };
        RegisterNewMember registerNewMember = new TransactionalRegisterNewMember(new JdbcPersonRepository(jdbc),
                connectionLostWhileSavingTheMembership, new Season(2026, 2027), () -> new PersonId(new UUID(0, 1)),
                new TransactionTemplate(new DataSourceTransactionManager(dataSource)));

        assertThrows(IllegalStateException.class, () -> registerNewMember.registerNewMember(
                new ClubId(new UUID(0, 2)), "Camille", "Martin", new FfeId("K58213"), FfeLicenseType.A,
                Optional.empty(), Optional.empty()));

        assertEquals(0, jdbc.sql("SELECT count(*) FROM person").query(Integer.class).single());
    }
}
