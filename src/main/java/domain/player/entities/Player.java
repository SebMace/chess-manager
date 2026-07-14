package domain.player.entities;

import domain.player.vo.EloRating;
import domain.player.vo.PlayerId;

public class Player {

    private String firstName;
    private String lastName;
    private PlayerId playerId;
    private EloRating eloRating;
    private EloRating eloRatingLastRecorded;

    public Player(PlayerId playerId, String firstName, String lastName) {
        this.playerId = playerId;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public Player(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public String firstName() {
        return firstName;
    }
    public String lastName() {
        return lastName;
    }

    public void giveEloRating(EloRating eloRating) {
        this.eloRating = eloRating;
    }

    public void recordEloRating(EloRating eloRating) {
        this.eloRatingLastRecorded = this.eloRating;
        this.eloRating = eloRating;
    }

    public EloRating eloRating() {
    return this.eloRating;
    }
}
