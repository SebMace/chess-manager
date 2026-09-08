package affiliation;

import domain.club.vo.ClubId;
import domain.club.vo.Season;
import domain.player.entities.Player;
import domain.player.vo.PlayerId;
import org.junit.jupiter.api.Test;

import java.util.UUID;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AffiliationsTests {
    @Test
    void should_affiliate_a_player_to_a_club_for_a_season() {
        // Given: a player without an affiliation for the season
        Player player = new Player(
                new PlayerId(UUID.fromString("00000000-0000-0000-0000-000000000001")),
                "Anatoly", "Karpov"
        );
        ClubId clubId = new ClubId(
                UUID.fromString("00000000-0000-0000-0000-000000000002")
        );
        Season season = new Season(2026, 2027);

        // When: the player joins the club for that season
        player.affiliateTo(clubId, season);

        // Then: the affiliation identifies the club for that season
        assertEquals(Optional.of(clubId), player.club(season));
    }
}
