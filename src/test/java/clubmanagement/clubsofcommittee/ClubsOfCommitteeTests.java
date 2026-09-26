package clubmanagement.clubsofcommittee;

import clubmanagement.domain.club.Club;
import clubmanagement.domain.club.vo.ClubId;
import clubmanagement.domain.club.vo.CommitteeCode;
import clubmanagement.domain.club.vo.FfeClubId;
import clubmanagement.domain.club.vo.PostalAddress;
import clubmanagement.domain.commune.CommuneCode;
import clubmanagement.ports.InMemoryClubRepository;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ClubsOfCommitteeTests {
    private static final PostalAddress OFFICE = new PostalAddress("12 rue des Échecs", "45000", "Orléans");
    private final InMemoryClubRepository clubs = new InMemoryClubRepository();

    @Test
    void should_list_a_club_of_the_committee() {
        clubs.save(new Club(new ClubId(UUID.fromString("00000000-0000-0000-0000-000000000001")), "U.S. Orléans.Echecs",
                true, new CommitteeCode("45"), new FfeClubId("G45001"), new CommuneCode("45234"), OFFICE, OFFICE));

        List<Club> found = new ClubsOfCommittee(clubs).execute(new CommitteeCode("45"));

        assertEquals(List.of("U.S. Orléans.Echecs"), found.stream().map(Club::name).toList());
    }
}
