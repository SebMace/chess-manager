package club;

import domain.club.Club;
import domain.club.vo.ClubId;
import domain.commune.CommuneCode;
import org.junit.jupiter.api.Test;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;

class ClubTests {
    private static final CommuneCode ORLEANS = new CommuneCode("45234");

    @Test
    void should_identify_a_club_independently_of_its_name() {
        ClubId id = new ClubId(UUID.fromString("00000000-0000-0000-0000-000000000002"));
        Club club = new Club(id, "Orléans", ORLEANS);
        assertEquals(id, club.id());
        assertEquals("Orléans", club.name());
        assertEquals(club, new Club(id, "Orléans Échecs", ORLEANS));
        assertNotEquals(club, new Club(new ClubId(UUID.fromString("00000000-0000-0000-0000-000000000003")), "Orléans", ORLEANS));
    }
    @Test
    void should_reject_a_club_without_an_identity() {
        assertThrows(IllegalArgumentException.class, () -> new Club(null, "Orléans", ORLEANS));
    }
    @Test
    void should_reject_a_club_without_its_commune() {
        ClubId id = new ClubId(UUID.fromString("00000000-0000-0000-0000-000000000002"));
        assertThrows(IllegalArgumentException.class, () -> new Club(id, "Orléans", null));
    }
}
