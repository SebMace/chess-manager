package infrastructure;

import application.club.ClubRepository;
import application.club.CreateClub;
import clubmanagement.domain.club.vo.CommitteeCode;
import clubmanagement.domain.club.vo.FfeClubId;
import clubmanagement.domain.club.vo.PostalAddress;
import clubmanagement.domain.commune.CommuneCode;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.NONE;

/** The development profile starts with a minimal, fictitious set of clubs. */
@SpringBootTest(classes = ChessManagerApplication.class, webEnvironment = NONE)
@ActiveProfiles("dev")
@Testcontainers
class DevelopmentDataTests {
    @Container
    @ServiceConnection
    static final PostgreSQLContainer postgres = new PostgreSQLContainer("postgres:18");

    private final ClubRepository clubs;
    private final CreateClub createClub;
    private final DevelopmentData developmentData;
    private final JdbcClient jdbc;

    DevelopmentDataTests(@Autowired ClubRepository clubs, @Autowired CreateClub createClub,
                         @Autowired DevelopmentData developmentData, @Autowired JdbcClient jdbc) {
        this.clubs = clubs;
        this.createClub = createClub;
        this.developmentData = developmentData;
        this.jdbc = jdbc;
    }

    @Test
    void the_demonstration_clubs_exist_once_the_application_has_started() {
        assertTrue(clubs.existsWithFfeClubId(new FfeClubId("DEMO01")));
        assertTrue(clubs.existsWithFfeClubId(new FfeClubId("DEMO02")));
        assertTrue(clubs.existsWithFfeClubId(new FfeClubId("DEMO03")));
    }

    @Test
    void each_start_begins_again_from_the_demonstration_clubs_only() {
        PostalAddress montargis = new PostalAddress("1 rue du Marché", "45200", "Montargis");
        createClub.execute("Club ajouté pendant une démonstration", new CommitteeCode("45"), new FfeClubId("G45999"),
                new CommuneCode("45208"), montargis, montargis);

        developmentData.run(null);

        assertEquals(3, jdbc.sql("SELECT count(*) FROM club").query(Integer.class).single());
        assertFalse(clubs.existsWithFfeClubId(new FfeClubId("G45999")));
    }
}
