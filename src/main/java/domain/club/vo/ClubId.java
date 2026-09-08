package domain.club.vo;

import java.util.UUID;

public record ClubId(UUID clubId) {

    public ClubId(UUID clubId) {
        if (clubId == null) throw new IllegalArgumentException("clubId cannot be null");
        this.clubId = clubId;
    }
}
