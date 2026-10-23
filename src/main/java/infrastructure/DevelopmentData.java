package infrastructure;

import clubmanagement.createclub.CreateClub;
import clubmanagement.domain.club.vo.ClubId;
import clubmanagement.domain.club.vo.CommitteeCode;
import clubmanagement.domain.club.vo.FfeClubId;
import clubmanagement.domain.club.vo.PostalAddress;
import clubmanagement.domain.commune.CommuneCode;
import clubmanagement.domain.member.vo.EloRating;
import clubmanagement.domain.member.vo.FfeId;
import clubmanagement.domain.member.vo.FfeLicenseType;
import clubmanagement.domain.member.vo.FideTitle;
import clubmanagement.registermember.RegisterNewMember;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.jdbc.core.simple.JdbcClient;

import java.util.List;
import java.util.Optional;
import java.util.Random;

/**
 * Fictitious clubs and players for local development, created through the use cases so that they follow
 * the domain rules. Each start empties the tables first, so that every run begins from the same data. Entirely
 * fictitious: real club names, FFE identifiers and people are FFE or personal data. The identifiers start with
 * DEMO so that they cannot be mistaken for real ones.
 * <p>
 * Eight clubs of ten players each: an interclub competition between them takes seven rounds.
 */
class DevelopmentData implements ApplicationRunner {
    private static final CommitteeCode LOIRET = new CommitteeCode("45");
    private static final int PLAYERS_PER_CLUB = 10;
    private static final long RATING_SEED = 2026;
    private static final int MEAN_RATING = 1800;
    private static final int RATING_SPREAD = 200;
    private static final int LOWEST_RATING = 1300;
    private static final int HIGHEST_RATING = 2400;

    private record DemonstrationClub(String name, String ffeClubId, String commune, PostalAddress registeredOffice,
                                     PostalAddress playingVenue) {
        DemonstrationClub(String name, String ffeClubId, String commune, PostalAddress address) {
            this(name, ffeClubId, commune, address, address);
        }
    }

    private static final List<DemonstrationClub> CLUBS = List.of(
            new DemonstrationClub("Les Cavaliers de la Loire", "DEMO01", "45234",
                    new PostalAddress("12 rue des Échecs", "45000", "Orléans")),
            new DemonstrationClub("Échiquier du Val", "DEMO02", "45298",
                    new PostalAddress("3 allée des Pions", "45750", "Saint-Pryvé-Saint-Mesmin"),
                    new PostalAddress("8 rue des Tours", "45160", "Olivet")),
            new DemonstrationClub("Tour de Loury", "DEMO03", "45188",
                    new PostalAddress("3 place de l'Église", "45470", "Loury")),
            new DemonstrationClub("Cercle des Quatre Tours", "DEMO04", "45208",
                    new PostalAddress("2 rue des Fous", "45200", "Montargis")),
            new DemonstrationClub("Pithiviers Roque Club", "DEMO05", "45252",
                    new PostalAddress("4 place du Pion", "45300", "Pithiviers")),
            new DemonstrationClub("Les Fous de la Loire", "DEMO06", "45155",
                    new PostalAddress("6 quai des Cavaliers", "45500", "Gien")),
            new DemonstrationClub("Dame Blanche de Braye", "DEMO07", "45284",
                    new PostalAddress("9 rue de la Dame", "45800", "Saint-Jean-de-Braye")),
            new DemonstrationClub("Beaugency Échiquier Royal", "DEMO08", "45028",
                    new PostalAddress("1 rue du Roi", "45190", "Beaugency")));

    private static final List<String> FIRST_NAMES = List.of("Alice", "Antoine", "Bastien", "Chloé", "Damien",
            "Élodie", "Fabien", "Gaëlle", "Hugo", "Inès", "Julien", "Karine", "Louis", "Margaux", "Nicolas", "Océane",
            "Paul", "Quentin", "Romane", "Sébastien");
    private static final List<String> LAST_NAMES = List.of("Bernard", "Dubois", "Thomas", "Robert", "Richard", "Petit",
            "Durand", "Leroy", "Moreau", "Simon", "Laurent", "Lefebvre", "Michel", "Garcia", "David", "Bertrand", "Roux",
            "Vincent", "Fournier", "Morel");

    private final JdbcClient jdbc;
    private final CreateClub createClub;
    private final RegisterNewMember registerNewMember;

    DevelopmentData(JdbcClient jdbc, CreateClub createClub, RegisterNewMember registerNewMember) {
        this.jdbc = jdbc;
        this.createClub = createClub;
        this.registerNewMember = registerNewMember;
    }

    @Override
    public void run(ApplicationArguments arguments) {
        jdbc.sql("DELETE FROM club_relationship_license").update();
        jdbc.sql("DELETE FROM club_relationship").update();
        jdbc.sql("DELETE FROM person").update();
        jdbc.sql("DELETE FROM club_session").update();
        jdbc.sql("DELETE FROM club").update();
        jdbc.sql("DELETE FROM competition_team").update();
        jdbc.sql("DELETE FROM competition").update();
        Random ratings = new Random(RATING_SEED);
        for (int club = 0; club < CLUBS.size(); club++) {
            DemonstrationClub demonstration = CLUBS.get(club);
            ClubId id = createClub.execute(demonstration.name(), LOIRET, new FfeClubId(demonstration.ffeClubId()),
                    new CommuneCode(demonstration.commune()), demonstration.registeredOffice(),
                    demonstration.playingVenue());
            registerPlayers(id, club, ratings);
        }
    }

    /**
     * Ten players per club, mostly with an A license. Their names are picked so that no two players of
     * the whole set share one: the first name depends on the club and the player, the last name too, and two
     * players with the same first name always differ by their last name.
     */
    private void registerPlayers(ClubId club, int clubIndex, Random ratings) {
        for (int player = 0; player < PLAYERS_PER_CLUB; player++) {
            int rating = rating(ratings);
            registerNewMember.registerNewMember(club,
                    FIRST_NAMES.get((clubIndex * PLAYERS_PER_CLUB + player) % FIRST_NAMES.size()),
                    LAST_NAMES.get((player * 3 + clubIndex) % LAST_NAMES.size()),
                    new FfeId("DEMOJ%d%02d".formatted(clubIndex + 1, player + 1)),
                    player % 4 == 3 ? FfeLicenseType.B : FfeLicenseType.A,
                    Optional.of(new EloRating(rating)), title(rating));
        }
    }

    /** Only the strongest players hold a title, from the FIDE rating floor of each: none is rated high enough for GMI. */
    private static Optional<FideTitle> title(int rating) {
        if (rating >= 2400) return Optional.of(FideTitle.MI);
        if (rating >= 2300) return Optional.of(FideTitle.MF);
        if (rating >= 2200) return Optional.of(FideTitle.CM);
        return Optional.empty();
    }

    /**
     * Many players around 1800, a few much weaker or stronger, never outside 1300 to 2400. The seed is fixed
     * so that every run begins from the same ratings.
     */
    private static int rating(Random ratings) {
        long rating = Math.round(MEAN_RATING + RATING_SPREAD * ratings.nextGaussian());
        return (int) Math.max(LOWEST_RATING, Math.min(HIGHEST_RATING, rating));
    }
}
