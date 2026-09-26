package clubmanagement.createclub;

import clubmanagement.ports.InMemoryClubRepository;
import clubmanagement.ports.InMemoryCommunes;
import clubmanagement.domain.club.Club;
import clubmanagement.domain.club.vo.ClubId;
import clubmanagement.domain.club.vo.CommitteeCode;
import clubmanagement.domain.club.vo.FfeClubId;
import clubmanagement.domain.club.vo.PostalAddress;
import clubmanagement.domain.commune.CommuneCode;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class CreateClubTests {
    private static final CommuneCode ORLEANS = new CommuneCode("45234");
    private static final PostalAddress OFFICE = new PostalAddress("12 rue des Échecs", "45000", "Orléans");
    private static final PostalAddress VENUE = new PostalAddress("5 rue du Roi", "45100", "Orléans");
    private final ClubId clubId = new ClubId(UUID.fromString("00000000-0000-0000-0000-000000000002"));
    private final InMemoryClubRepository clubs = new InMemoryClubRepository();
    private final InMemoryCommunes communes = new InMemoryCommunes();

    @Test
    void should_save_a_managed_club_with_its_information() {
        CreateClub createClub = new CreateClub(clubs, communes, () -> clubId);

        ClubId createdId = createClub.execute("U.S. Orléans.Echecs", new CommitteeCode("45"), new FfeClubId("G45001"), ORLEANS, OFFICE, VENUE);

        assertEquals(clubId, createdId);
        Club club = clubs.find(createdId).orElseThrow();
        assertEquals("U.S. Orléans.Echecs", club.name());
        assertTrue(club.managedByApplication());
        assertEquals(Optional.of(new CommitteeCode("45")), club.committee());
        assertEquals(Optional.of(new FfeClubId("G45001")), club.ffeClubId());
        assertEquals(ORLEANS, club.commune());
    }

    @Test
    void should_save_the_registered_office_of_the_club() {
        CreateClub createClub = new CreateClub(clubs, communes, () -> clubId);

        createClub.execute("U.S. Orléans.Echecs", new CommitteeCode("45"), new FfeClubId("G45001"), ORLEANS, OFFICE, VENUE);

        assertEquals(OFFICE, clubs.find(clubId).orElseThrow().registeredOffice());
    }

    @Test
    void should_save_the_playing_venue_of_the_club() {
        CreateClub createClub = new CreateClub(clubs, communes, () -> clubId);

        createClub.execute("U.S. Orléans.Echecs", new CommitteeCode("45"), new FfeClubId("G45001"), ORLEANS, OFFICE, VENUE);

        assertEquals(VENUE, clubs.find(clubId).orElseThrow().playingVenue());
    }

    @Test
    void should_refuse_a_club_without_its_ffe_identifier() {
        CreateClub createClub = new CreateClub(clubs, communes, () -> clubId);

        assertThrows(IllegalArgumentException.class,
                () -> createClub.execute("U.S. Orléans.Echecs", new CommitteeCode("45"), null, ORLEANS, OFFICE, VENUE));

        assertTrue(clubs.find(clubId).isEmpty());
    }

    @Test
    void should_refuse_a_club_without_its_commune() {
        CreateClub createClub = new CreateClub(clubs, communes, () -> clubId);

        assertThrows(IllegalArgumentException.class,
                () -> createClub.execute("U.S. Orléans.Echecs", new CommitteeCode("45"), new FfeClubId("G45001"), null, OFFICE, VENUE));

        assertTrue(clubs.find(clubId).isEmpty());
    }

    @Test
    void should_refuse_a_club_in_a_commune_outside_the_department_of_its_committee() {
        CreateClub createClub = new CreateClub(clubs, communes, () -> clubId);
        CommuneCode olivetInMayenne = InMemoryCommunes.OLIVET_IN_MAYENNE.code();

        CommuneNotInCommitteeDepartment refusal = assertThrows(CommuneNotInCommitteeDepartment.class,
                () -> createClub.execute("Olivet – La Tour prend garde", new CommitteeCode("45"), new FfeClubId("G45002"), olivetInMayenne, OFFICE, VENUE));

        assertEquals(olivetInMayenne, refusal.commune());
        assertTrue(clubs.find(clubId).isEmpty());
    }

    @Test
    void should_refuse_a_second_club_with_the_same_ffe_identifier() {
        ClubId secondId = new ClubId(UUID.fromString("00000000-0000-0000-0000-000000000003"));
        new CreateClub(clubs, communes, () -> clubId).execute("U.S. Orléans.Echecs", new CommitteeCode("45"), new FfeClubId("G45001"), ORLEANS, OFFICE, VENUE);
        CreateClub createClub = new CreateClub(clubs, communes, () -> secondId);

        FfeClubIdAlreadyUsed refusal = assertThrows(FfeClubIdAlreadyUsed.class,
                () -> createClub.execute("Échiquier Orléanais", new CommitteeCode("45"), new FfeClubId("g45001"), ORLEANS, OFFICE, VENUE));

        assertEquals(new FfeClubId("G45001"), refusal.ffeClubId());
        assertTrue(clubs.find(secondId).isEmpty());
    }
}
