package endtoend;

import clubmanagement.domain.club.vo.Activity;
import clubmanagement.domain.club.vo.ClubId;
import clubmanagement.domain.club.vo.PostalAddress;
import clubmanagement.domain.club.vo.Session;
import clubmanagement.domain.club.vo.Venue;
import clubmanagement.ports.ClubCalendar;
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
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

/** HTTP request → DefineOpeningHours or OpeningHoursOfClub → JDBC → PostgreSQL. */
@SpringBootTest(classes = ChessManagerApplication.class, webEnvironment = RANDOM_PORT)
@Testcontainers
class OpeningHoursEndToEndTests {
    @Container
    @ServiceConnection
    static final PostgreSQLContainer postgres = new PostgreSQLContainer("postgres:18");

    private final int port;
    private final ClubCalendar clubCalendar;
    private static int createdClubs;

    OpeningHoursEndToEndTests(@LocalServerPort int port, @Autowired ClubCalendar clubCalendar) {
        this.port = port;
        this.clubCalendar = clubCalendar;
    }

    @Test
    void an_administrator_defines_the_opening_hours_of_a_club() throws Exception {
        ClubId club = createClub();

        HttpResponse<String> response = send("PUT", "/api/clubs/" + club.clubId() + "/opening-hours", """
                [{"day": "FRIDAY", "from": "20:00", "to": "22:00", "activity": "free play"},
                 {"day": "MONDAY", "from": "20:00", "to": "22:00", "activity": "children's lessons",
                  "venue": {"street": "3 rue de l'École", "postcode": "45000", "town": "Orléans"}}]""");

        assertEquals(204, response.statusCode());
        assertEquals(Set.of(
                        new Session(DayOfWeek.FRIDAY, LocalTime.of(20, 0), LocalTime.of(22, 0),
                                Optional.of(new Activity("free play"))),
                        new Session(DayOfWeek.MONDAY, LocalTime.of(20, 0), LocalTime.of(22, 0),
                                Optional.of(new Activity("children's lessons")),
                                new Venue.Address(new PostalAddress("3 rue de l'École", "45000", "Orléans")))),
                clubCalendar.openingHoursOf(club));
    }

    @Test
    void an_administrator_consults_the_opening_hours_of_a_club() throws Exception {
        ClubId club = createClub();
        clubCalendar.defineOpeningHours(club, Set.of(new Session(DayOfWeek.SATURDAY, LocalTime.of(15, 0),
                LocalTime.of(17, 0), Optional.of(new Activity("adult lessons")),
                new Venue.Address(new PostalAddress("3 rue de l'École", "45000", "Orléans")))));

        HttpResponse<String> response = HttpClient.newHttpClient().send(HttpRequest.newBuilder(
                URI.create("http://localhost:" + port + "/api/clubs/" + club.clubId() + "/opening-hours")).GET().build(),
                HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertEquals("""
                [{"day":"SATURDAY","from":"15:00","to":"17:00","activity":"adult lessons",\
                "venue":{"street":"3 rue de l'École","postcode":"45000","town":"Orléans"}}]""", response.body());
    }

    @Test
    void a_session_at_the_playing_venue_of_the_club_has_no_address() throws Exception {
        ClubId club = createClub();
        clubCalendar.defineOpeningHours(club, Set.of(new Session(DayOfWeek.FRIDAY, LocalTime.of(20, 0), LocalTime.of(22, 0))));

        HttpResponse<String> response = HttpClient.newHttpClient().send(HttpRequest.newBuilder(
                URI.create("http://localhost:" + port + "/api/clubs/" + club.clubId() + "/opening-hours")).GET().build(),
                HttpResponse.BodyHandlers.ofString());

        assertEquals("""
                [{"day":"FRIDAY","from":"20:00","to":"22:00","activity":null,"venue":null}]""", response.body());
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
