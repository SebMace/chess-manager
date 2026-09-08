package player;

import domain.exceptions.FideIdAlreadyAssignedException;
import domain.player.entities.Player;
import domain.player.vo.EloRating;
import domain.player.vo.FideId;
import domain.player.vo.PlayerId;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class PlayerTests {
    // Day 1 10/07/2026
    @Test
    void should_create_a_player_with_a_first_name_and_a_last_name() {
        Player player = new Player(
                new PlayerId(UUID.randomUUID()),
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
        Player player = new Player(new PlayerId(UUID.randomUUID()),"Sébastien", "Macé");
        player.giveEloRating(new EloRating(1500));
        // when : his rating is updated
        player.recordEloRating(new EloRating(1600));
        // then : tha player gets the new rating
        assertEquals(new EloRating(1600),player.eloRating());
    }

    // Day 4 13/07/2026

    @Test
    void should_reject_replacing_an_existing_fide_id() throws FideIdAlreadyAssignedException {
    //Given  : a player with an existing FIDE ID
        Player player = new Player(new PlayerId(UUID.randomUUID()),"Sébastien", "Macé");
        player.registerFideId(new FideId(641839L));
    // When / Then  : assigning another FIDE ID is rejected
        assertThrows(FideIdAlreadyAssignedException.class, () -> player.registerFideId(new FideId(1503014L)));
    }
    // Day 5 14/07/2026
    @ParameterizedTest
    @ValueSource(longs = {-1L, 0L})
    void should_reject_a_non_positive_fide_id(long invalidFideId)  {
        assertThrows(IllegalArgumentException.class, () -> new FideId(invalidFideId));
    }
    // Day 6
    @Test
    void should_create_a_player_with_an_internal_id_and_without_a_fide_id() {
        // Given  : a PlayerId, first name, last_name, no FideId
        PlayerId playerId = new PlayerId(UUID.randomUUID());
        Player player = new Player(playerId, "Anatoly", "Karpov");
        assertEquals(playerId, player.id());
        assertTrue(player.fideId().isEmpty());
    }
    // Day 7
    @Test
    void should_reject_a_player_without_a_player_id() {
        assertThrows(IllegalArgumentException.class, () -> new Player(null, "Anatoly", "Karpov"));
    }
    // Day 8
    @Test
    void should_reject_a_null_uuid_in_player_id() {
        assertThrows(IllegalArgumentException.class, () -> new PlayerId(null));
    }
    // Day 9

    @Test
    void should_consider_players_with_the_same_player_id_as_the_same_player() {
        // Given: two players with equal identifiers and different names
        UUID uuid = UUID.fromString("00000000-0000-0000-0000-000000000001");
        Player player1 = new Player(new PlayerId(uuid), "Anatoly", "Karpov");
        Player player2 = new Player(new PlayerId(uuid), "Magnus", "Carlsen");
        // When / Then: they represent the same player
        assertEquals(player1, player2);
    }

    @Test
    void should_consider_homonyms_as_different_players_if_their_identities_are_different () {
        PlayerId playerId1 = new PlayerId(UUID.fromString("00000000-0000-0000-0000-000000000001"));
        PlayerId playerId2 = new PlayerId(UUID.fromString("00000000-0000-0000-0000-000000000002"));
        Player player1 = new Player(playerId1, "Anatoly", "Karpov");
        Player player2 = new Player(playerId2, "Anatoly", "Karpov");
        assertNotEquals(player1, player2);
    }
}
