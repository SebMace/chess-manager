package club;

import clubmanagement.domain.club.Club;
import clubmanagement.domain.club.vo.ClubId;
import clubmanagement.domain.club.vo.PostalAddress;
import clubmanagement.domain.commune.CommuneCode;
import org.junit.jupiter.api.Test;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;

class ClubTests {
    private static final CommuneCode ORLEANS = new CommuneCode("45234");
    private static final PostalAddress OFFICE = new PostalAddress("12 rue des Échecs", "45000", "Orléans");

    @Test
    void should_identify_a_club_independently_of_its_name() {
        ClubId id = new ClubId(UUID.fromString("00000000-0000-0000-0000-000000000002"));
        Club club = new Club(id, "Orléans", ORLEANS, OFFICE, OFFICE);
        assertEquals(id, club.id());
        assertEquals("Orléans", club.name());
        assertEquals(club, new Club(id, "Orléans Échecs", ORLEANS, OFFICE, OFFICE));
        assertNotEquals(club, new Club(new ClubId(UUID.fromString("00000000-0000-0000-0000-000000000003")), "Orléans", ORLEANS, OFFICE, OFFICE));
    }
    @Test
    void should_reject_a_club_without_an_identity() {
        assertThrows(IllegalArgumentException.class, () -> new Club(null, "Orléans", ORLEANS, OFFICE, OFFICE));
    }
    @Test
    void should_reject_a_club_without_its_commune() {
        ClubId id = new ClubId(UUID.fromString("00000000-0000-0000-0000-000000000002"));
        assertThrows(IllegalArgumentException.class, () -> new Club(id, "Orléans", null, OFFICE, OFFICE));
    }

    @Test
    void should_reject_a_club_without_its_registered_office() {
        ClubId id = new ClubId(UUID.fromString("00000000-0000-0000-0000-000000000002"));
        assertThrows(IllegalArgumentException.class, () -> new Club(id, "Orléans", ORLEANS, null, OFFICE));
    }

    @Test
    void should_reject_a_club_without_its_playing_venue() {
        ClubId id = new ClubId(UUID.fromString("00000000-0000-0000-0000-000000000002"));
        assertThrows(IllegalArgumentException.class, () -> new Club(id, "Orléans", ORLEANS, OFFICE, null));
    }
}
