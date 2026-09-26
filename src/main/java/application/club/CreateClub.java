package application.club;

import domain.club.Club;
import domain.club.vo.ClubId;
import domain.club.vo.CommitteeCode;
import domain.club.vo.FfeClubId;

import java.util.function.Supplier;

public class CreateClub {
    private final ClubRepository clubs;
    private final Supplier<ClubId> newClubId;

    public CreateClub(ClubRepository clubs, Supplier<ClubId> newClubId) {
        this.clubs = clubs;
        this.newClubId = newClubId;
    }

    public ClubId execute(String name, CommitteeCode committee) {
        ClubId id = newClubId.get();
        clubs.save(new Club(id, name, true, committee));
        return id;
    }

    public ClubId execute(String name, CommitteeCode committee, FfeClubId ffeClubId, String commune) {
        ClubId id = newClubId.get();
        clubs.save(new Club(id, name, true, committee, ffeClubId, commune));
        return id;
    }
}
