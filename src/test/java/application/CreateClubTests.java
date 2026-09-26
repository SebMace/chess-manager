package application;

import application.club.CreateClub;
import club.InMemoryClubRepository;
import domain.club.Club;
import domain.club.vo.ClubId;
import domain.club.vo.CommitteeCode;
import domain.club.vo.FfeClubId;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class CreateClubTests {
    private final ClubId clubId = new ClubId(UUID.fromString("00000000-0000-0000-0000-000000000002"));
    private final InMemoryClubRepository clubs = new InMemoryClubRepository();

    @Test
    void should_save_a_managed_club_in_its_departmental_committee() {
        CreateClub createClub = new CreateClub(clubs, () -> clubId);

        ClubId createdId = createClub.execute("U.S. Orléans.Echecs", new CommitteeCode("45"), null, null);

        assertEquals(clubId, createdId);
        Club club = clubs.find(createdId).orElseThrow();
        assertEquals("U.S. Orléans.Echecs", club.name());
        assertTrue(club.managedByApplication());
        assertEquals(Optional.of(new CommitteeCode("45")), club.committee());
    }

    @Test
    void should_save_a_club_with_its_ffe_identity() {
        CreateClub createClub = new CreateClub(clubs, () -> clubId);

        ClubId createdId = createClub.execute("U.S. Orléans.Echecs", new CommitteeCode("45"), new FfeClubId("G45001"), "Orléans");

        Club club = clubs.find(createdId).orElseThrow();
        assertEquals(Optional.of(new FfeClubId("G45001")), club.ffeClubId());
        assertEquals(Optional.of("Orléans"), club.commune());
        assertEquals(Optional.of(new CommitteeCode("45")), club.committee());
    }
}
