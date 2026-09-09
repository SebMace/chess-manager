package domain.player.entities;

import domain.club.vo.ClubId;
import domain.club.vo.Season;
import domain.exceptions.FideIdAlreadyAssignedException;
import domain.player.vo.EloRating;
import domain.player.vo.FideId;
import domain.player.vo.PlayerId;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public class Player {

    private final PlayerId playerId;
    private FideId fideId;
    private String firstName;
    private String lastName;

    private EloRating eloRating;
    private EloRating eloRatingLastRecorded;

    private final Map<Season, ClubId> seasonsClub;

    public Player(PlayerId playerId, String firstName, String lastName) {
        if (playerId == null) throw new IllegalArgumentException("playerId cannot be null");
        this.playerId = playerId;
        this.firstName = firstName;
        this.lastName = lastName;
        seasonsClub = new HashMap<>();
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

    public Optional<FideId> fideId() {return Optional.ofNullable(fideId);}

    public PlayerId id() {return playerId;}
    public void affiliateTo(ClubId clubId, Season season) {
        seasonsClub.put(season, clubId);
    }

    public Optional<ClubId> club(Season season) {
        return Optional.ofNullable(seasonsClub.get(season));
    }
    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Player player = (Player) o;
        return Objects.equals(playerId, player.playerId);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(playerId);
    }
}

