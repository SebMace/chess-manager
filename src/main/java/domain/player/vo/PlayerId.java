package domain.player.vo;

import java.io.Serializable;
import java.util.UUID;

public record PlayerId(UUID playerId) implements Serializable {
}
