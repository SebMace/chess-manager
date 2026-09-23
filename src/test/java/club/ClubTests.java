package club;

import domain.club.Club;
import domain.club.vo.ClubId;
import org.junit.jupiter.api.Test;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;

class ClubTests {
    @Test
    void should_identify_a_club_independently_of_its_name() {
        ClubId id = new ClubId(UUID.fromString("00000000-0000-0000-0000-000000000002"));
        Club club = new Club(id, "Orléans");
        assertEquals(id, club.id());
        assertEquals("Orléans", club.name());
        assertEquals(club, new Club(id, "Orléans Échecs"));
        assertNotEquals(club, new Club(new ClubId(UUID.fromString("00000000-0000-0000-0000-000000000003")), "Orléans"));
    }
    @Test
    void should_reject_a_club_without_an_identity() {
        assertThrows(IllegalArgumentException.class, () -> new Club(null, "Orléans"));
    }
}
