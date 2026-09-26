package clubmanagement.clubsofcommittee;

import clubmanagement.domain.club.Club;
import clubmanagement.domain.club.vo.ClubId;
import clubmanagement.domain.club.vo.CommitteeCode;
import clubmanagement.domain.club.vo.FfeClubId;
import clubmanagement.domain.club.vo.PostalAddress;
import clubmanagement.ports.InMemoryClubRepository;
import clubmanagement.ports.InMemoryCommunes;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ClubsOfCommitteeTests {
    private static final PostalAddress OFFICE = new PostalAddress("12 rue des Échecs", "45000", "Orléans");
    private final InMemoryClubRepository clubs = new InMemoryClubRepository();
    private final ClubsOfCommittee clubsOfCommittee = new ClubsOfCommittee(clubs, new InMemoryCommunes());

    @Test
    void should_list_a_club_of_the_committee() {
        clubs.save(new Club(new ClubId(UUID.fromString("00000000-0000-0000-0000-000000000001")), "U.S. Orléans.Echecs",
                true, new CommitteeCode("45"), new FfeClubId("G45001"), InMemoryCommunes.ORLEANS.code(), OFFICE, OFFICE));

        List<ClubOfCommittee> found = clubsOfCommittee.execute(new CommitteeCode("45"));

        assertEquals(List.of("U.S. Orléans.Echecs"), found.stream().map(ClubOfCommittee::name).toList());
    }

    @Test
    void should_show_the_commune_and_the_ffe_identifier_of_a_club() {
        clubs.save(new Club(new ClubId(UUID.fromString("00000000-0000-0000-0000-000000000002")), "Cercle fictif d'Olivet",
                true, new CommitteeCode("45"), new FfeClubId("G45998"), InMemoryCommunes.OLIVET.code(), OFFICE, OFFICE));

        List<ClubOfCommittee> found = clubsOfCommittee.execute(new CommitteeCode("45"));

        assertEquals(List.of(new ClubOfCommittee("Cercle fictif d'Olivet", "Olivet", new FfeClubId("G45998"))), found);
    }

    @Test
    void should_list_the_clubs_in_the_order_of_their_names_whatever_their_accents_and_capitals() {
        save("00000000-0000-0000-0000-000000000003", "U.S. Orléans.Echecs", "G45001");
        save("00000000-0000-0000-0000-000000000004", "échiquier fictif d'Olivet", "G45998");
        save("00000000-0000-0000-0000-000000000005", "Cercle fictif de Montargis", "G45997");

        List<ClubOfCommittee> found = clubsOfCommittee.execute(new CommitteeCode("45"));

        assertEquals(List.of("Cercle fictif de Montargis", "échiquier fictif d'Olivet", "U.S. Orléans.Echecs"),
                found.stream().map(ClubOfCommittee::name).toList());
    }

    private void save(String id, String name, String ffeClubId) {
        clubs.save(new Club(new ClubId(UUID.fromString(id)), name, true, new CommitteeCode("45"),
                new FfeClubId(ffeClubId), InMemoryCommunes.ORLEANS.code(), OFFICE, OFFICE));
    }
}
