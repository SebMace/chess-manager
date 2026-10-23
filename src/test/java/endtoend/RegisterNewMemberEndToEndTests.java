package endtoend;

import clubmanagement.domain.club.vo.ClubId;
import infrastructure.ChessManagerApplication;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

/** HTTP request → RegisterNewMember → JDBC → PostgreSQL. */
@SpringBootTest(classes = ChessManagerApplication.class, webEnvironment = RANDOM_PORT)
@Testcontainers
class RegisterNewMemberEndToEndTests {
    @Container
    @ServiceConnection
    static final PostgreSQLContainer postgres = new PostgreSQLContainer("postgres:18");

    private final int port;
    private final JdbcClient jdbc;
    private static int createdClubs;

    RegisterNewMemberEndToEndTests(@LocalServerPort int port, @Autowired JdbcClient jdbc) {
        this.port = port;
        this.jdbc = jdbc;
    }

    @Test
    void an_administrator_registers_a_licensed_player_as_a_member_of_a_club() throws Exception {
        ClubId club = createClub();

        HttpResponse<String> response = send("POST", "/api/clubs/" + club.clubId() + "/members", """
                {"firstName": "Camille", "lastName": "Martin", "ffeId": "K58213", "licenseType": "A", "elo": 2310,
                 "fideTitle": "MF"}""");

        assertEquals(201, response.statusCode());
        assertEquals(List.of(Map.of("first_name", "Camille", "last_name", "Martin", "ffe_id", "K58213",
                        "elo", 2310, "fide_title", "MF", "license_type", "A")),
                jdbc.sql("""
                        select p.first_name, p.last_name, p.ffe_id, p.elo, p.fide_title, l.license_type
                        from person p join club_relationship_license l on l.person_id = p.id
                        where l.club_id = ?""").param(club.clubId()).query().listOfRows());
    }

    @Test
    void a_player_without_a_rating_can_be_registered_as_a_member_of_a_club() throws Exception {
        ClubId club = createClub();

        HttpResponse<String> response = send("POST", "/api/clubs/" + club.clubId() + "/members", """
                {"firstName": "Alex", "lastName": "Durand", "ffeId": "K61477", "licenseType": "B", "elo": null}""");

        assertEquals(201, response.statusCode());
        List<Map<String, Object>> registered = jdbc.sql("""
                select p.elo from person p join club_relationship_license l on l.person_id = p.id
                where l.club_id = ?""").param(club.clubId()).query().listOfRows();
        assertEquals(1, registered.size());
        assertNull(registered.get(0).get("elo"));
    }

    @Test
    void an_unknown_fide_title_is_refused() throws Exception {
        ClubId club = createClub();

        HttpResponse<String> response = send("POST", "/api/clubs/" + club.clubId() + "/members", """
                {"firstName": "Alex", "lastName": "Durand", "ffeId": "K61479", "licenseType": "B", "fideTitle": "XX"}""");

        assertEquals(400, response.statusCode());
    }

    @Test
    void a_negative_rating_is_refused() throws Exception {
        ClubId club = createClub();

        HttpResponse<String> response = send("POST", "/api/clubs/" + club.clubId() + "/members", """
                {"firstName": "Alex", "lastName": "Durand", "ffeId": "K61478", "licenseType": "B", "elo": -1}""");

        assertEquals(400, response.statusCode());
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
