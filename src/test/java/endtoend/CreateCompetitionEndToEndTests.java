package endtoend;

import competition.domain.Competition;
import competition.domain.CompetitionId;
import competition.domain.League;
import competition.domain.Season;
import competition.ports.CompetitionRepository;
import infrastructure.ChessManagerApplication;
import org.junit.jupiter.api.Disabled;
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
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

/** HTTP request → CreateCompetition → JDBC → PostgreSQL. */
@Disabled("Outer loop of the REST slice: enabled when POST /api/competitions creates a competition")
@SpringBootTest(classes = ChessManagerApplication.class, webEnvironment = RANDOM_PORT)
@Testcontainers
class CreateCompetitionEndToEndTests {
    @Container
    @ServiceConnection
    static final PostgreSQLContainer postgres = new PostgreSQLContainer("postgres:18");

    private final int port;
    private final CompetitionRepository competitions;

    CreateCompetitionEndToEndTests(@LocalServerPort int port, @Autowired CompetitionRepository competitions) {
        this.port = port;
        this.competitions = competitions;
    }

    @Test
    void an_administrator_creates_a_competition_for_the_current_season() throws Exception {
        Competition competition = competitions.find(createCompetition("""
                {"name": "Nationale 4", "organizingLeague": "Centre-Val de Loire", "boards": 6}"""))
                .orElseThrow();

        assertEquals("Nationale 4", competition.name());
        assertEquals(new League("Centre-Val de Loire"), competition.organizingLeague());
        assertEquals(new Season(2026, 2027), competition.season());
        assertEquals(6, competition.boards());
    }

    private CompetitionId createCompetition(String json) throws Exception {
        HttpResponse<Void> response = post(json);

        assertEquals(201, response.statusCode());
        String location = response.headers().firstValue("Location").orElseThrow();
        assertTrue(location.contains("/api/competitions/"), location);
        return new CompetitionId(UUID.fromString(location.substring(location.lastIndexOf('/') + 1)));
    }

    private HttpResponse<Void> post(String json) throws Exception {
        HttpRequest request = HttpRequest.newBuilder(URI.create("http://localhost:" + port + "/api/competitions"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();
        return HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.discarding());
    }
}
