package domain.player.entities;

import domain.exceptions.FideIdAlreadyAssignedException;
import domain.player.vo.EloRating;
import domain.player.vo.FideId;

public class Player {

    private FideId fideId;
    private String firstName;
    private String lastName;

    private EloRating eloRating;
    private EloRating eloRatingLastRecorded;

    public Player(FideId playerId, String firstName, String lastName) {
        this.fideId = playerId;
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

    public void registerFideId(FideId fideId) throws FideIdAlreadyAssignedException {
        if (this.fideId != null) throw new FideIdAlreadyAssignedException();
        this.fideId = fideId;

    }
}
