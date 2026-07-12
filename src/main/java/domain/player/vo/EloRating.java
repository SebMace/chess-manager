package domain.player.vo;

public record EloRating(int rating) {
     public EloRating  {
        if (rating<0) throw new IllegalArgumentException();
    }
}
