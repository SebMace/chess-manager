package infrastructure;

import clubmanagement.domain.club.Club;
import clubmanagement.domain.club.vo.ClubId;
import clubmanagement.domain.club.vo.Session;
import clubmanagement.ports.ClubCalendar;
import clubmanagement.ports.ClubRepository;
import clubmanagement.createclub.CreateClub;
import competition.competitionsofseason.CompetitionsOfSeason;
import competition.createcompetition.CreateCompetition;
import competition.domain.Competition;
import competition.domain.CompetitionId;
import competition.domain.League;
import competition.domain.Team;
import competition.engageteam.EngageTeam;
import clubmanagement.domain.club.vo.CommitteeCode;
import clubmanagement.domain.club.vo.FfeClubId;
import clubmanagement.domain.club.vo.PostalAddress;
import clubmanagement.domain.commune.CommuneCode;
import clubmanagement.domain.member.vo.FfeId;
import clubmanagement.domain.member.vo.FfeLicenseType;
import clubmanagement.domain.member.vo.FideTitle;
import clubmanagement.membersofclub.MembersOfClub;
import clubmanagement.ports.ClubMember;
import clubmanagement.registermember.RegisterNewMember;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

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
    private final ClubCalendar clubCalendar;
    private final RegisterNewMember registerNewMember;
    private final MembersOfClub membersOfClub;
    private final CreateCompetition createCompetition;
    private final EngageTeam engageTeam;
    private final CompetitionsOfSeason competitionsOfSeason;

    /** Eight clubs, so that an interclub competition can be played in seven rounds. */
    private static final int CLUBS = 8;
    private static final int PLAYERS_PER_CLUB = 10;

    DevelopmentDataTests(@Autowired ClubRepository clubs, @Autowired CreateClub createClub,
                         @Autowired DevelopmentData developmentData, @Autowired JdbcClient jdbc,
                         @Autowired ClubCalendar clubCalendar, @Autowired RegisterNewMember registerNewMember,
                         @Autowired MembersOfClub membersOfClub, @Autowired CreateCompetition createCompetition,
                         @Autowired EngageTeam engageTeam, @Autowired CompetitionsOfSeason competitionsOfSeason) {
        this.createCompetition = createCompetition;
        this.engageTeam = engageTeam;
        this.competitionsOfSeason = competitionsOfSeason;
        this.clubs = clubs;
        this.createClub = createClub;
        this.developmentData = developmentData;
        this.jdbc = jdbc;
        this.clubCalendar = clubCalendar;
        this.registerNewMember = registerNewMember;
        this.membersOfClub = membersOfClub;
    }

    @Test
    void the_demonstration_clubs_exist_once_the_application_has_started() {
        for (int club = 1; club <= CLUBS; club++) {
            assertTrue(clubs.existsWithFfeClubId(new FfeClubId("DEMO%02d".formatted(club))), "DEMO%02d".formatted(club));
        }
    }

    @Test
    void each_demonstration_club_has_ten_members() {
        developmentData.run(null);

        List<Club> demonstrationClubs = clubs.inCommittee(new CommitteeCode("45"));

        assertEquals(CLUBS, demonstrationClubs.size());
        for (Club club : demonstrationClubs) {
            assertEquals(PLAYERS_PER_CLUB, membersOfClub.membersOf(club.id()).size(), club.name());
        }
    }

    @Test
    void no_two_demonstration_players_share_a_name() {
        developmentData.run(null);

        List<String> names = demonstrationMembers().stream()
                .map(member -> member.firstName() + " " + member.lastName())
                .toList();

        assertEquals(CLUBS * PLAYERS_PER_CLUB, names.size());
        assertEquals(names.size(), Set.copyOf(names).size());
    }

    @Test
    void every_demonstration_player_is_rated_between_1300_and_2400() {
        developmentData.run(null);

        List<Integer> ratings = demonstrationMembers().stream()
                .map(member -> member.rating().orElseThrow().rating())
                .toList();

        assertEquals(CLUBS * PLAYERS_PER_CLUB, ratings.size());
        assertTrue(ratings.stream().allMatch(rating -> rating >= 1300 && rating <= 2400), ratings.toString());
    }

    @Test
    void most_demonstration_players_are_rated_around_1800() {
        developmentData.run(null);

        long around1800 = demonstrationMembers().stream()
                .filter(member -> Math.abs(member.rating().orElseThrow().rating() - 1800) <= 200)
                .count();

        assertTrue(around1800 * 2 > CLUBS * PLAYERS_PER_CLUB, "players within 200 points of 1800: " + around1800);
    }

    @Test
    void some_demonstration_players_hold_a_fide_title() {
        developmentData.run(null);

        assertTrue(demonstrationMembers().stream().anyMatch(member -> member.title().isPresent()));
    }

    @Test
    void a_demonstration_player_holds_a_fide_title_only_if_their_rating_reaches_its_floor() {
        developmentData.run(null);

        for (ClubMember member : demonstrationMembers()) {
            member.title().ifPresent(title -> assertTrue(
                    member.rating().orElseThrow().rating() >= ratingFloor(title),
                    member.firstName() + " " + member.lastName() + ", " + title + ", rated "
                            + member.rating().orElseThrow().rating()));
        }
    }

    /** The lowest FIDE rating a player needs before they can hold the title. */
    private static int ratingFloor(FideTitle title) {
        return switch (title) {
            case CM -> 2200;
            case MF -> 2300;
            case MI -> 2400;
            case GMI -> 2500;
        };
    }

    private List<ClubMember> demonstrationMembers() {
        return clubs.inCommittee(new CommitteeCode("45")).stream()
                .flatMap(club -> membersOfClub.membersOf(club.id()).stream())
                .toList();
    }

    @Test
    void each_start_begins_again_from_the_demonstration_clubs_only() {
        PostalAddress montargis = new PostalAddress("1 rue du Marché", "45200", "Montargis");
        createClub.execute("Club ajouté pendant une démonstration", new CommitteeCode("45"), new FfeClubId("G45999"),
                new CommuneCode("45208"), montargis, montargis);

        developmentData.run(null);

        assertEquals(CLUBS, jdbc.sql("SELECT count(*) FROM club").query(Integer.class).single());
        assertFalse(clubs.existsWithFfeClubId(new FfeClubId("G45999")));
    }

    @Test
    void each_start_begins_again_even_when_a_club_has_opening_hours() {
        PostalAddress montargis = new PostalAddress("1 rue du Marché", "45200", "Montargis");
        ClubId club = createClub.execute("Club ouvert pendant une démonstration", new CommitteeCode("45"),
                new FfeClubId("G45998"), new CommuneCode("45208"), montargis, montargis);
        clubCalendar.defineOpeningHours(club, Set.of(new Session(DayOfWeek.FRIDAY, LocalTime.of(20, 0), LocalTime.of(22, 0))));

        developmentData.run(null);

        assertEquals(CLUBS, jdbc.sql("SELECT count(*) FROM club").query(Integer.class).single());
        assertEquals(0, jdbc.sql("SELECT count(*) FROM club_session").query(Integer.class).single());
    }

    @Test
    void each_start_begins_again_even_when_a_club_has_members() {
        PostalAddress montargis = new PostalAddress("1 rue du Marché", "45200", "Montargis");
        ClubId club = createClub.execute("Club avec des membres pendant une démonstration", new CommitteeCode("45"),
                new FfeClubId("G45997"), new CommuneCode("45208"), montargis, montargis);
        registerNewMember.registerNewMember(club, "Camille", "Martin", new FfeId("K58213"), FfeLicenseType.A,
                Optional.empty(), Optional.empty());

        developmentData.run(null);

        assertEquals(CLUBS, jdbc.sql("SELECT count(*) FROM club").query(Integer.class).single());
        assertEquals(CLUBS * PLAYERS_PER_CLUB, jdbc.sql("SELECT count(*) FROM person").query(Integer.class).single());
        assertEquals(0, jdbc.sql("SELECT count(*) FROM person WHERE ffe_id = 'K58213'").query(Integer.class).single());
    }

    @Test
    void each_start_begins_again_even_when_a_competition_has_engaged_teams() {
        CompetitionId competition = createCompetition.createCompetition("Nationale 4 engagée pendant une démonstration",
                new League("Centre-Val de Loire"), 6);
        engageTeam.engageTeam(competition, new competition.domain.ClubId(UUID.randomUUID()));

        developmentData.run(null);

        assertEquals(0, jdbc.sql("SELECT count(*) FROM competition_team WHERE competition_id = :competition")
                .param("competition", competition.competitionId()).query(Integer.class).single());
    }

    @Test
    void each_start_begins_again_even_when_a_competition_exists() {
        createCompetition.createCompetition("Nationale 4 ajoutée pendant une démonstration",
                new League("Centre-Val de Loire"), 6);

        developmentData.run(null);

        assertEquals(List.of("Nationale 4"), jdbc.sql("SELECT name FROM competition").query(String.class).list());
    }

    @Test
    void the_nationale_4_of_the_season_has_one_team_of_each_demonstration_club() {
        developmentData.run(null);

        List<Competition> competitions = competitionsOfSeason.competitionsOfCurrentSeason().competitions();
        assertEquals(List.of("Nationale 4"), competitions.stream().map(Competition::name).toList());
        List<Team> teams = competitions.get(0).teams();
        Set<UUID> demonstrationClubs = clubs.inCommittee(new CommitteeCode("45")).stream()
                .map(club -> club.id().clubId()).collect(Collectors.toSet());
        assertEquals(demonstrationClubs, teams.stream().map(team -> team.club().clubId()).collect(Collectors.toSet()));
        assertEquals(CLUBS, teams.size());
        assertTrue(teams.stream().allMatch(team -> team.number() == 1));
    }
}
