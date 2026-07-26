package domain.player.vo;

import java.io.Serializable;
import java.util.UUID;

public record PlayerId(UUID playerId) implements Serializable {

    public PlayerId(UUID playerId) {
        if (playerId == null) throw new IllegalArgumentException("playerId cannot be null");
        this.playerId = playerId;
    }
}
