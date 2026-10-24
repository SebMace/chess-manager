package endtoend;

import competition.domain.Competition;
import competition.domain.CompetitionId;
import competition.domain.League;
import competition.domain.Season;
import competition.ports.CompetitionRepository;
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
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

/** HTTP request → CompetitionsOfSeason → JDBC → PostgreSQL. */
@SpringBootTest(classes = ChessManagerApplication.class, webEnvironment = RANDOM_PORT)
@Testcontainers
class ConsultCompetitionsEndToEndTests {
    @Container
    @ServiceConnection
    static final PostgreSQLContainer postgres = new PostgreSQLContainer("postgres:18");

    private final int port;
    private final CompetitionRepository competitions;

    ConsultCompetitionsEndToEndTests(@LocalServerPort int port, @Autowired CompetitionRepository competitions) {
        this.port = port;
        this.competitions = competitions;
    }

    @Test
    void an_administrator_consults_the_competitions_of_the_current_season() throws Exception {
        competitions.save(new Competition(new CompetitionId(UUID.randomUUID()), "Nationale 4",
                new League("Centre-Val de Loire"), new Season(2025, 2026), 6));
        createCompetition("""
                {"name": "Régionale 1", "organizingLeague": "Centre-Val de Loire", "boards": 4}""");
        createCompetition("""
                {"name": "Nationale 4", "organizingLeague": "Centre-Val de Loire", "boards": 6}""");

        HttpResponse<String> response = get("/api/competitions");

        assertEquals(200, response.statusCode());
        assertEquals("""
                {"season":{"yearBegin":2026,"yearEnd":2027},"competitions":[\
                {"name":"Nationale 4","organizingLeague":"Centre-Val de Loire","boards":6},\
                {"name":"Régionale 1","organizingLeague":"Centre-Val de Loire","boards":4}]}""", response.body());
    }

    private void createCompetition(String json) throws Exception {
        HttpRequest request = HttpRequest.newBuilder(URI.create("http://localhost:" + port + "/api/competitions"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();
        HttpResponse<Void> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.discarding());

        assertEquals(201, response.statusCode());
    }

    private HttpResponse<String> get(String path) throws Exception {
        HttpRequest request = HttpRequest.newBuilder(URI.create("http://localhost:" + port + path)).GET().build();
        return HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
    }
}
