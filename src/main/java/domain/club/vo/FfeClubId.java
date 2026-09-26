package domain.club.vo;

import java.util.Locale;

public record FfeClubId(String value) {
    public FfeClubId {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("FFE club identifier cannot be null or blank");
        }
        value = value.strip().toUpperCase(Locale.ROOT);
    }
}
