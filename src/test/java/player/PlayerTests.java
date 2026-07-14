package player;

import domain.player.entities.Player;
import domain.player.vo.EloRating;
import domain.player.vo.PlayerId;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PlayerTests {
    // Day 1 10/07/2026
    @Test
    void should_create_a_player_with_a_first_name_and_a_last_name() {
        Player player = new Player(
                new PlayerId("1503014"),
                "Magnus",
                "Carlsen"
        );
        assertEquals("Magnus", player.firstName());
        assertEquals("Carlsen", player.lastName());
    }
    // Day 2 11/07/2026
    @Test
    void should_reject_a_negative_elo_rating() {
        assertThrows(IllegalArgumentException.class, () -> new EloRating(-1));
    }

    // Day 3 12/07/2026
    // Player as an entity is immutable but its state's value object evolves.
    // Player's identity never changes

    @Test
    void should_change_the_elo_rating_of_a_player() {
        //Given : one existing player with an initial elo rating.
        Player player = new Player("Sébastien", "Macé");
        player.giveEloRating(new EloRating(1500));
        // when : his rating is updated
        player.recordEloRating(new EloRating(1600));
        // then : tha player gets the new rating
        assertEquals(new EloRating(1600),player.eloRating());
    }

    
}
