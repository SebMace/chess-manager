package application.club;

import domain.club.Club;
import domain.club.vo.ClubId;

import java.util.function.Supplier;

public class CreateClub {
    private final ClubRepository clubs;
    private final Supplier<ClubId> newClubId;

    public CreateClub(ClubRepository clubs, Supplier<ClubId> newClubId) {
        this.clubs = clubs;
        this.newClubId = newClubId;
    }

    public ClubId execute(String name) {
        ClubId id = newClubId.get();
        clubs.save(new Club(id, name, true));
        return id;
    }
}
