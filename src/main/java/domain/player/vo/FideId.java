package domain.player.vo;

public record FideId(Long fideId) {
    public FideId(Long fideId) {
        if (fideId <= 0) throw new IllegalArgumentException("Fide id cannot be negative");
        this.fideId = fideId;
    }
}
