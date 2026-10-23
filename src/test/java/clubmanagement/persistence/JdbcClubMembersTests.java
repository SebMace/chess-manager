package clubmanagement.persistence;

import clubmanagement.domain.club.Club;
import clubmanagement.domain.club.ClubRelationship;
import clubmanagement.domain.club.vo.ClubId;
import clubmanagement.domain.club.vo.CommitteeCode;
import clubmanagement.domain.club.vo.FfeClubId;
import clubmanagement.domain.club.vo.PostalAddress;
import clubmanagement.domain.club.vo.Season;
import clubmanagement.domain.commune.CommuneCode;
import clubmanagement.domain.member.vo.EloRating;
import clubmanagement.domain.member.vo.FfeId;
import clubmanagement.domain.member.vo.FfeLicenseType;
import clubmanagement.domain.member.vo.FideTitle;
import clubmanagement.domain.person.Person;
import clubmanagement.domain.person.vo.PersonId;
import clubmanagement.ports.ClubMember;
import clubmanagement.ports.ClubMembers;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

import javax.sql.DataSource;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Testcontainers
class JdbcClubMembersTests {
    @Container
    static final PostgreSQLContainer postgres = new PostgreSQLContainer("postgres:18");

    private static final PostalAddress OFFICE = new PostalAddress("12 rue des Échecs", "45000", "Orléans");
    private static final PostalAddress VENUE = new PostalAddress("5 rue du Roi", "45100", "Orléans");
    private static final Season SEASON = new Season(2026, 2027);
    private static DataSource dataSource;
    private static int created;

    @BeforeAll
    static void migrateSchema() {
        dataSource = new DriverManagerDataSource(postgres.getJdbcUrl(), postgres.getUsername(), postgres.getPassword());
        Flyway.configure().dataSource(dataSource).load().migrate();
    }

    @Test
    void should_list_the_members_of_a_club_for_a_season() {
        ClubId club = aClub();
        register("Camille", "Martin", "K58213", FfeLicenseType.A, club, SEASON);

        List<ClubMember> members = clubMembers().ofClub(club, SEASON);

        assertEquals(List.of(unrated("Camille", "Martin", "K58213", FfeLicenseType.A)), members);
    }

    @Test
    void should_list_the_rating_of_a_rated_member() {
        ClubId club = aClub();
        registerRated("Camille", "Martin", "K44441", FfeLicenseType.A, club, SEASON, new EloRating(1850));

        List<ClubMember> members = clubMembers().ofClub(club, SEASON);

        assertEquals(List.of(new ClubMember("Camille", "Martin", new FfeId("K44441"), FfeLicenseType.A,
                Optional.of(new EloRating(1850)), Optional.empty())), members);
    }

    @Test
    void should_list_the_fide_title_of_a_titled_member() {
        ClubId club = aClub();
        save(Person.registeredLicensedPlayer(new PersonId(new UUID(0, ++created)), "Maxime", "Roux",
                new FfeId("K70002"), Optional.of(new EloRating(2310)), Optional.of(FideTitle.MF)),
                FfeLicenseType.A, club, SEASON);

        List<ClubMember> members = clubMembers().ofClub(club, SEASON);

        assertEquals(List.of(new ClubMember("Maxime", "Roux", new FfeId("K70002"), FfeLicenseType.A,
                Optional.of(new EloRating(2310)), Optional.of(FideTitle.MF))), members);
    }

    @Test
    void should_not_list_the_members_of_another_club() {
        ClubId orleans = aClub();
        ClubId olivet = aClub();
        register("Camille", "Martin", "K11111", FfeLicenseType.A, orleans, SEASON);
        register("Alex", "Durand", "K11112", FfeLicenseType.B, olivet, SEASON);

        List<ClubMember> members = clubMembers().ofClub(orleans, SEASON);

        assertEquals(List.of(unrated("Camille", "Martin", "K11111", FfeLicenseType.A)), members);
    }

    @Test
    void should_not_list_a_player_whose_license_is_for_another_season() {
        ClubId club = aClub();
        register("Camille", "Martin", "K22221", FfeLicenseType.A, club, SEASON);
        register("Alex", "Durand", "K22222", FfeLicenseType.B, club, new Season(2025, 2026));

        List<ClubMember> members = clubMembers().ofClub(club, SEASON);

        assertEquals(List.of(unrated("Camille", "Martin", "K22221", FfeLicenseType.A)), members);
    }

    @Test
    void should_list_the_members_by_last_name_then_first_name() {
        ClubId club = aClub();
        register("Léa", "Martin", "K33333", FfeLicenseType.A, club, SEASON);
        register("Camille", "Martin", "K33331", FfeLicenseType.A, club, SEASON);
        register("Alex", "Durand", "K33332", FfeLicenseType.B, club, SEASON);

        List<ClubMember> members = clubMembers().ofClub(club, SEASON);

        assertEquals(List.of(
                unrated("Alex", "Durand", "K33332", FfeLicenseType.B),
                unrated("Camille", "Martin", "K33331", FfeLicenseType.A),
                unrated("Léa", "Martin", "K33333", FfeLicenseType.A)), members);
    }

    private ClubMembers clubMembers() { return new JdbcClubMembers(JdbcClient.create(dataSource)); }

    /** Members refer to their club, so the club is saved first, with its own FFE identifier. */
    private ClubId aClub() {
        ClubId club = new ClubId(new UUID(0, ++created));
        new JdbcClubRepository(JdbcClient.create(dataSource)).save(new Club(club, "U.S. Orléans.Echecs", true,
                new CommitteeCode("45"), new FfeClubId("G45%03d".formatted(created)),
                new CommuneCode("45234"), OFFICE, VENUE));
        return club;
    }

    private void register(String firstName, String lastName, String ffeId, FfeLicenseType license, ClubId club,
                          Season season) {
        save(Person.licensedPlayer(new PersonId(new UUID(0, ++created)), firstName, lastName, new FfeId(ffeId)),
                license, club, season);
    }

    private void registerRated(String firstName, String lastName, String ffeId, FfeLicenseType license, ClubId club,
                               Season season, EloRating rating) {
        save(Person.registeredLicensedPlayer(new PersonId(new UUID(0, ++created)), firstName, lastName,
                new FfeId(ffeId), Optional.of(rating), Optional.empty()), license, club, season);
    }

    private void save(Person player, FfeLicenseType license, ClubId club, Season season) {
        new JdbcPersonRepository(JdbcClient.create(dataSource)).save(player);
        new JdbcClubRelationshipRepository(JdbcClient.create(dataSource))
                .save(ClubRelationship.membershipOf(player, club, license, season));
    }

    private static ClubMember unrated(String firstName, String lastName, String ffeId, FfeLicenseType license) {
        return new ClubMember(firstName, lastName, new FfeId(ffeId), license, Optional.empty(), Optional.empty());
    }
}
