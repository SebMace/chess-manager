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

    public ClubId execute(String name, CommitteeCode committee, FfeClubId ffeClubId, String commune) {
        if (ffeClubId == null) throw new IllegalArgumentException("A club cannot be created without its FFE identifier");
        if (commune == null || commune.isBlank()) throw new IllegalArgumentException("A club cannot be created without its commune");
        if (clubs.existsWithFfeClubId(ffeClubId)) throw new FfeClubIdAlreadyUsed(ffeClubId);
        ClubId id = newClubId.get();
        clubs.save(new Club(id, name, true, committee, ffeClubId, commune));
        return id;
    }
}
