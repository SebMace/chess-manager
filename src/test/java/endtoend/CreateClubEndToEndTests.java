package endtoend;

import application.club.ClubRepository;
import domain.club.Club;
import domain.club.vo.ClubId;
import domain.club.vo.CommitteeCode;
import domain.club.vo.FfeClubId;
import domain.club.vo.PostalAddress;
import domain.commune.CommuneCode;
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
                {"name": "U.S. Orléans.Echecs", "committeeCode": "45", "ffeClubId": "G45001", "communeCode": "45234", "registeredOffice": {"street": "12 rue des Échecs", "postcode": "45000", "town": "Orléans"}}"""))
                .orElseThrow();

        assertEquals("U.S. Orléans.Echecs", club.name());
        assertTrue(club.managedByApplication());
        assertEquals(Optional.of(new CommitteeCode("45")), club.committee());
        assertEquals(Optional.of(new FfeClubId("G45001")), club.ffeClubId());
        assertEquals(new CommuneCode("45234"), club.commune());
        assertEquals(new PostalAddress("12 rue des Échecs", "45000", "Orléans"), club.registeredOffice());
    }

    @Test
    void a_club_cannot_be_created_without_its_departmental_committee() throws Exception {
        HttpResponse<Void> response = post("{\"name\": \"Montargis\"}");

        assertEquals(400, response.statusCode());
    }

    @Test
    void a_club_cannot_be_created_without_its_ffe_identifier() throws Exception {
        HttpResponse<Void> response = post("""
                {"name": "U.S. Orléans.Echecs", "committeeCode": "45", "communeCode": "45234", "registeredOffice": {"street": "12 rue des Échecs", "postcode": "45000", "town": "Orléans"}}""");

        assertEquals(400, response.statusCode());
    }

    @Test
    void a_club_cannot_be_created_with_a_blank_ffe_identifier() throws Exception {
        HttpResponse<Void> response = post("""
                {"name": "U.S. Orléans.Echecs", "committeeCode": "45", "ffeClubId": "   ", "communeCode": "45234", "registeredOffice": {"street": "12 rue des Échecs", "postcode": "45000", "town": "Orléans"}}""");

        assertEquals(400, response.statusCode());
    }

    @Test
    void a_club_cannot_be_created_without_its_commune() throws Exception {
        HttpResponse<Void> response = post("""
                {"name": "Échiquier Orléanais", "committeeCode": "45", "ffeClubId": "G45006"}""");

        assertEquals(400, response.statusCode());
    }

    @Test
    void a_club_cannot_be_created_in_a_commune_outside_the_department_of_its_committee() throws Exception {
        HttpResponse<Void> response = post("""
                {"name": "Olivet – La Tour prend garde", "committeeCode": "45", "ffeClubId": "G45007", "communeCode": "53169", "registeredOffice": {"street": "12 rue des Échecs", "postcode": "45000", "town": "Orléans"}}""");

        assertEquals(400, response.statusCode());
    }

    @Test
    void a_club_cannot_be_created_without_its_registered_office() throws Exception {
        HttpResponse<Void> response = post("""
                {"name": "Loury Échecs", "committeeCode": "45", "ffeClubId": "G45008", "communeCode": "45188"}""");

        assertEquals(400, response.statusCode());
    }

    @Test
    void a_club_cannot_be_created_with_an_incomplete_registered_office() throws Exception {
        HttpResponse<Void> response = post("""
                {"name": "Loury Échecs", "committeeCode": "45", "ffeClubId": "G45009", "communeCode": "45188",
                 "registeredOffice": {"street": "3 place de l'Église", "postcode": "45470", "town": " "}}""");

        assertEquals(400, response.statusCode());
    }

    @Test
    void a_club_cannot_be_created_with_a_registered_office_postcode_that_is_not_five_digits() throws Exception {
        HttpResponse<Void> response = post("""
                {"name": "Loury Échecs", "committeeCode": "45", "ffeClubId": "G45010", "communeCode": "45188",
                 "registeredOffice": {"street": "3 place de l'Église", "postcode": "4547", "town": "Loury"}}""");

        assertEquals(400, response.statusCode());
    }

    @Test
    void a_second_club_cannot_use_the_same_ffe_identifier() throws Exception {
        createClub("""
                {"name": "Echiquier du Gâtinais", "committeeCode": "45", "ffeClubId": "G45100", "communeCode": "45208", "registeredOffice": {"street": "12 rue des Échecs", "postcode": "45000", "town": "Orléans"}}""");

        HttpResponse<Void> response = post("""
                {"name": "Gâtinais Échecs", "committeeCode": "45", "ffeClubId": "g45100", "communeCode": "45208", "registeredOffice": {"street": "12 rue des Échecs", "postcode": "45000", "town": "Orléans"}}""");

        assertEquals(409, response.statusCode());
    }

    @Test
    void the_communes_of_the_department_of_its_committee_are_offered_for_a_club() throws Exception {
        HttpRequest request = HttpRequest.newBuilder(URI.create("http://localhost:" + port + "/communes?committee=45")).GET().build();

        HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertTrue(response.body().contains("{\"code\":\"45232\",\"name\":\"Olivet\"}"), response.body());
        assertFalse(response.body().contains("53169"));
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
