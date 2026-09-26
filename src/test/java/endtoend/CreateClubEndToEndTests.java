package endtoend;

import application.club.ClubRepository;
import domain.club.Club;
import domain.club.vo.ClubId;
import domain.club.vo.CommitteeCode;
import domain.club.vo.FfeClubId;
import infrastructure.ChessManagerApplication;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

/** Walking skeleton: HTTP request → CreateClub → JDBC → PostgreSQL. */
@SpringBootTest(classes = ChessManagerApplication.class, webEnvironment = RANDOM_PORT)
@Testcontainers
class CreateClubEndToEndTests {
    @Container
    @ServiceConnection
    static final PostgreSQLContainer postgres = new PostgreSQLContainer("postgres:18");

    private final int port;
    private final ClubRepository clubs;

    CreateClubEndToEndTests(@LocalServerPort int port, @Autowired ClubRepository clubs) {
        this.port = port;
        this.clubs = clubs;
    }

    @Test
    void an_administrator_creates_a_club_with_its_information() throws Exception {
        Club club = clubs.find(createClub("""
                {"name": "U.S. Orléans.Echecs", "committeeCode": "45", "ffeClubId": "G45001", "commune": "Orléans"}"""))
                .orElseThrow();

        assertEquals("U.S. Orléans.Echecs", club.name());
        assertTrue(club.managedByApplication());
        assertEquals(Optional.of(new CommitteeCode("45")), club.committee());
        assertEquals(Optional.of(new FfeClubId("G45001")), club.ffeClubId());
        assertEquals(Optional.of("Orléans"), club.commune());
    }

    @Test
    void a_club_cannot_be_created_without_its_departmental_committee() throws Exception {
        HttpResponse<Void> response = post("{\"name\": \"Montargis\"}");

        assertEquals(400, response.statusCode());
    }

    private ClubId createClub(String json) throws Exception {
        HttpResponse<Void> response = post(json);

        assertEquals(201, response.statusCode());
        String location = response.headers().firstValue("Location").orElseThrow();
        return new ClubId(UUID.fromString(location.substring(location.lastIndexOf('/') + 1)));
    }

    private HttpResponse<Void> post(String json) throws Exception {
        HttpRequest request = HttpRequest.newBuilder(URI.create("http://localhost:" + port + "/clubs"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();
        return HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.discarding());
    }
}
