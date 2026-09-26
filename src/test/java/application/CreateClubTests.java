package application;

import application.club.CreateClub;
import application.club.FfeClubIdAlreadyUsed;
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
    void should_save_a_managed_club_with_its_information() {
        CreateClub createClub = new CreateClub(clubs, () -> clubId);

        ClubId createdId = createClub.execute("U.S. Orléans.Echecs", new CommitteeCode("45"), new FfeClubId("G45001"), "Orléans");

        assertEquals(clubId, createdId);
        Club club = clubs.find(createdId).orElseThrow();
        assertEquals("U.S. Orléans.Echecs", club.name());
        assertTrue(club.managedByApplication());
        assertEquals(Optional.of(new CommitteeCode("45")), club.committee());
        assertEquals(Optional.of(new FfeClubId("G45001")), club.ffeClubId());
        assertEquals(Optional.of("Orléans"), club.commune());
    }

    @Test
    void should_refuse_a_club_without_its_ffe_identifier() {
        CreateClub createClub = new CreateClub(clubs, () -> clubId);

        assertThrows(IllegalArgumentException.class,
                () -> createClub.execute("U.S. Orléans.Echecs", new CommitteeCode("45"), null, "Orléans"));

        assertTrue(clubs.find(clubId).isEmpty());
    }

    @Test
    void should_refuse_a_club_without_its_commune() {
        CreateClub createClub = new CreateClub(clubs, () -> clubId);

        assertThrows(IllegalArgumentException.class,
                () -> createClub.execute("U.S. Orléans.Echecs", new CommitteeCode("45"), new FfeClubId("G45001"), null));

        assertTrue(clubs.find(clubId).isEmpty());
    }

    @Test
    void should_refuse_a_second_club_with_the_same_ffe_identifier() {
        ClubId secondId = new ClubId(UUID.fromString("00000000-0000-0000-0000-000000000003"));
        new CreateClub(clubs, () -> clubId).execute("U.S. Orléans.Echecs", new CommitteeCode("45"), new FfeClubId("G45001"), "Orléans");
        CreateClub createClub = new CreateClub(clubs, () -> secondId);

        FfeClubIdAlreadyUsed refusal = assertThrows(FfeClubIdAlreadyUsed.class,
                () -> createClub.execute("Échiquier Orléanais", new CommitteeCode("45"), new FfeClubId("g45001"), "Orléans"));

        assertEquals(new FfeClubId("G45001"), refusal.ffeClubId());
        assertTrue(clubs.find(secondId).isEmpty());
    }
}
