package application;

import application.club.CreateClub;
import application.club.CommuneNotInCommitteeDepartment;
import application.club.FfeClubIdAlreadyUsed;
import club.InMemoryClubRepository;
import commune.InMemoryCommunes;
import domain.club.Club;
import domain.club.vo.ClubId;
import domain.club.vo.CommitteeCode;
import domain.club.vo.FfeClubId;
import domain.club.vo.PostalAddress;
import domain.commune.CommuneCode;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class CreateClubTests {
    private static final CommuneCode ORLEANS = new CommuneCode("45234");
    private static final PostalAddress OFFICE = new PostalAddress("12 rue des Échecs", "45000", "Orléans");
    private final ClubId clubId = new ClubId(UUID.fromString("00000000-0000-0000-0000-000000000002"));
    private final InMemoryClubRepository clubs = new InMemoryClubRepository();
    private final InMemoryCommunes communes = new InMemoryCommunes();

    @Test
    void should_save_a_managed_club_with_its_information() {
        CreateClub createClub = new CreateClub(clubs, communes, () -> clubId);

        ClubId createdId = createClub.execute("U.S. Orléans.Echecs", new CommitteeCode("45"), new FfeClubId("G45001"), ORLEANS, OFFICE);

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

        createClub.execute("U.S. Orléans.Echecs", new CommitteeCode("45"), new FfeClubId("G45001"), ORLEANS, OFFICE);

        assertEquals(OFFICE, clubs.find(clubId).orElseThrow().registeredOffice());
    }

    @Test
    void should_refuse_a_club_without_its_ffe_identifier() {
        CreateClub createClub = new CreateClub(clubs, communes, () -> clubId);

        assertThrows(IllegalArgumentException.class,
                () -> createClub.execute("U.S. Orléans.Echecs", new CommitteeCode("45"), null, ORLEANS, OFFICE));

        assertTrue(clubs.find(clubId).isEmpty());
    }

    @Test
    void should_refuse_a_club_without_its_commune() {
        CreateClub createClub = new CreateClub(clubs, communes, () -> clubId);

        assertThrows(IllegalArgumentException.class,
                () -> createClub.execute("U.S. Orléans.Echecs", new CommitteeCode("45"), new FfeClubId("G45001"), null, OFFICE));

        assertTrue(clubs.find(clubId).isEmpty());
    }

    @Test
    void should_refuse_a_club_in_a_commune_outside_the_department_of_its_committee() {
        CreateClub createClub = new CreateClub(clubs, communes, () -> clubId);
        CommuneCode olivetInMayenne = InMemoryCommunes.OLIVET_IN_MAYENNE.code();

        CommuneNotInCommitteeDepartment refusal = assertThrows(CommuneNotInCommitteeDepartment.class,
                () -> createClub.execute("Olivet – La Tour prend garde", new CommitteeCode("45"), new FfeClubId("G45002"), olivetInMayenne, OFFICE));

        assertEquals(olivetInMayenne, refusal.commune());
        assertTrue(clubs.find(clubId).isEmpty());
    }

    @Test
    void should_refuse_a_second_club_with_the_same_ffe_identifier() {
        ClubId secondId = new ClubId(UUID.fromString("00000000-0000-0000-0000-000000000003"));
        new CreateClub(clubs, communes, () -> clubId).execute("U.S. Orléans.Echecs", new CommitteeCode("45"), new FfeClubId("G45001"), ORLEANS, OFFICE);
        CreateClub createClub = new CreateClub(clubs, communes, () -> secondId);

        FfeClubIdAlreadyUsed refusal = assertThrows(FfeClubIdAlreadyUsed.class,
                () -> createClub.execute("Échiquier Orléanais", new CommitteeCode("45"), new FfeClubId("g45001"), ORLEANS, OFFICE));

        assertEquals(new FfeClubId("G45001"), refusal.ffeClubId());
        assertTrue(clubs.find(secondId).isEmpty());
    }
}
