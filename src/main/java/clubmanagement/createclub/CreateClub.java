package clubmanagement.createclub;

import clubmanagement.ports.ClubRepository;
import clubmanagement.ports.Communes;
import clubmanagement.domain.club.Club;
import clubmanagement.domain.club.vo.ClubId;
import clubmanagement.domain.club.vo.CommitteeCode;
import clubmanagement.domain.club.vo.FfeClubId;
import clubmanagement.domain.club.vo.PostalAddress;
import clubmanagement.domain.commune.CommuneCode;

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

    public ClubId execute(String name, CommitteeCode committee, FfeClubId ffeClubId, CommuneCode commune,
                          PostalAddress registeredOffice, PostalAddress playingVenue) {
        if (ffeClubId == null) throw new IllegalArgumentException("A club cannot be created without its FFE identifier");
        if (clubs.existsWithFfeClubId(ffeClubId)) throw new FfeClubIdAlreadyUsed(ffeClubId);
        ClubId id = newClubId.get();
        Club club = new Club(id, name, true, committee, ffeClubId, commune, registeredOffice, playingVenue);
        boolean inCommitteeDepartment = communes.find(commune)
                .filter(found -> found.isIn(committee.department()))
                .isPresent();
        if (!inCommitteeDepartment) throw new CommuneNotInCommitteeDepartment(commune, committee);
        clubs.save(club);
        return id;
    }
}
