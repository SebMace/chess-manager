package infrastructure;

import application.club.ClubRepository;
import domain.club.vo.FfeClubId;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
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

    DevelopmentDataTests(@Autowired ClubRepository clubs) {
        this.clubs = clubs;
    }

    @Test
    void the_demonstration_clubs_exist_once_the_application_has_started() {
        assertTrue(clubs.existsWithFfeClubId(new FfeClubId("DEMO01")));
        assertTrue(clubs.existsWithFfeClubId(new FfeClubId("DEMO02")));
        assertTrue(clubs.existsWithFfeClubId(new FfeClubId("DEMO03")));
    }
}
