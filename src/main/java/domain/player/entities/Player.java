package domain.player.entities;

import domain.player.vo.PlayerId;

public class Player {

    private String firstName;
    private String lastName;
    private PlayerId playerId;

    public Player(PlayerId playerId, String firstName, String lastName) {
        this.playerId = playerId;
        this.firstName = firstName;
        this.lastName = lastName;
    }


    public String firstName() {
        return firstName;
    }
    public String lastName() {
        return lastName;
    }
}
