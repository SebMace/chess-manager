package domain.member.vo;

public record FfeId(String value) {
    public FfeId {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("FFE identifier cannot be null or blank");
        }
    }
}
