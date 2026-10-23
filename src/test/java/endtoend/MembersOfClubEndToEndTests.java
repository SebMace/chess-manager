package endtoend;

import clubmanagement.domain.club.vo.ClubId;
import infrastructure.ChessManagerApplication;
import org.junit.jupiter.api.Test;
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

/** HTTP request → MembersOfClub → JDBC → PostgreSQL. */
@SpringBootTest(classes = ChessManagerApplication.class, webEnvironment = RANDOM_PORT)
@Testcontainers
class MembersOfClubEndToEndTests {
    @Container
    @ServiceConnection
    static final PostgreSQLContainer postgres = new PostgreSQLContainer("postgres:18");

    private final int port;
    private static int createdClubs;

    MembersOfClubEndToEndTests(@LocalServerPort int port) {
        this.port = port;
    }

    @Test
    void an_administrator_consults_the_members_of_a_club() throws Exception {
        ClubId club = createClub();
        registerMember(club, "Camille", "Martin", "K58213", "A", "2310", "\"MF\"");

        HttpResponse<String> response = send("GET", "/api/clubs/" + club.clubId() + "/members", "");

        assertEquals(200, response.statusCode());
        assertEquals("""
                [{"firstName":"Camille","lastName":"Martin","ffeId":"K58213","licenseType":"A","elo":2310,\
                "fideTitle":"MF"}]""", response.body());
    }

    @Test
    void a_member_without_a_rating_is_listed_without_one() throws Exception {
        ClubId club = createClub();
        registerMember(club, "Alex", "Durand", "K61477", "B", "null", "null");

        HttpResponse<String> response = send("GET", "/api/clubs/" + club.clubId() + "/members", "");

        assertEquals("""
                [{"firstName":"Alex","lastName":"Durand","ffeId":"K61477","licenseType":"B","elo":null,\
                "fideTitle":null}]""", response.body());
    }

    /** The rating and the title are JSON literals: a number or null, a quoted string or null. */
    private void registerMember(ClubId club, String firstName, String lastName, String ffeId, String licenseType,
                                String elo, String fideTitle) throws Exception {
        HttpResponse<String> response = send("POST", "/api/clubs/" + club.clubId() + "/members", """
                {"firstName": "%s", "lastName": "%s", "ffeId": "%s", "licenseType": "%s", "elo": %s, "fideTitle": %s}"""
                .formatted(firstName, lastName, ffeId, licenseType, elo, fideTitle));
        assertEquals(201, response.statusCode());
    }

    private ClubId createClub() throws Exception {
        createdClubs++;
        HttpResponse<String> response = send("POST", "/api/clubs", """
                {"name": "Cercle fictif %d", "committeeCode": "45", "ffeClubId": "G45%03d", "communeCode": "45234",
                 "registeredOffice": {"street": "12 rue des Échecs", "postcode": "45000", "town": "Orléans"},
                 "playingVenue": {"street": "5 rue du Roi", "postcode": "45100", "town": "Orléans"}}"""
                .formatted(createdClubs, createdClubs));
        assertEquals(201, response.statusCode());
        String location = response.headers().firstValue("Location").orElseThrow();
        return new ClubId(UUID.fromString(location.substring(location.lastIndexOf('/') + 1)));
    }

    private HttpResponse<String> send(String method, String path, String json) throws Exception {
        HttpRequest request = HttpRequest.newBuilder(URI.create("http://localhost:" + port + path))
                .header("Content-Type", "application/json")
                .method(method, HttpRequest.BodyPublishers.ofString(json))
                .build();
        return HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
    }
}
