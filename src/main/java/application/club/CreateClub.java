package application.club;

import application.commune.Communes;
import domain.club.Club;
import domain.club.vo.ClubId;
import domain.club.vo.CommitteeCode;
import domain.club.vo.FfeClubId;
import domain.commune.CommuneCode;

import java.util.function.Supplier;

public class CreateClub {
    private final ClubRepository clubs;
    private final Communes communes;
    private final Supplier<ClubId> newClubId;

    public CreateClub(ClubRepository clubs, Communes communes, Supplier<ClubId> newClubId) {
        this.clubs = clubs;
        this.communes = communes;
        this.newClubId = newClubId;
    }

    public ClubId execute(String name, CommitteeCode committee, FfeClubId ffeClubId, CommuneCode commune) {
        if (ffeClubId == null) throw new IllegalArgumentException("A club cannot be created without its FFE identifier");
        if (clubs.existsWithFfeClubId(ffeClubId)) throw new FfeClubIdAlreadyUsed(ffeClubId);
        ClubId id = newClubId.get();
        Club club = new Club(id, name, true, committee, ffeClubId, commune);
        boolean inCommitteeDepartment = communes.find(commune)
                .filter(found -> found.department().equals(committee.department()))
                .isPresent();
        if (!inCommitteeDepartment) throw new CommuneNotInCommitteeDepartment(commune, committee);
        clubs.save(club);
        return id;
    }
}
