package domain.player.vo;

public record EloRating(int rating) {
     public EloRating(int rating) {

        if (rating<0) throw new IllegalArgumentException();
    }
}
