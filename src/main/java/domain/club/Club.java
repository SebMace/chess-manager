package domain.club;

import domain.club.vo.ClubId;
import domain.club.vo.CommitteeCode;
import domain.club.vo.FfeClubId;

import java.util.Optional;

public final class Club {
    private final ClubId id;
    private final String name;
    private final boolean managedByApplication;
    private final CommitteeCode committee;
    private final FfeClubId ffeClubId;
    private final String commune;

    public Club(ClubId id, String name, String commune) {
        this(id, name, false, commune);
    }

    public Club(ClubId id, String name, boolean managedByApplication, String commune) {
        this(id, name, managedByApplication, null, null, commune);
    }

    public Club(ClubId id, String name, boolean managedByApplication, CommitteeCode committee,
                FfeClubId ffeClubId, String commune) {
        if (id == null) throw new IllegalArgumentException("clubId cannot be null");
        if (commune == null) throw new IllegalArgumentException("A club cannot exist without its commune");
        this.id = id;
        this.name = name;
        this.managedByApplication = managedByApplication;
        this.committee = committee;
        this.ffeClubId = ffeClubId;
        this.commune = commune;
    }

    public ClubId id() { return id; }
    public String name() { return name; }
    public boolean managedByApplication() { return managedByApplication; }
    public Optional<CommitteeCode> committee() { return Optional.ofNullable(committee); }
    public Optional<FfeClubId> ffeClubId() { return Optional.ofNullable(ffeClubId); }
    public Optional<String> commune() { return Optional.ofNullable(commune); }

    @Override
    public boolean equals(Object other) {
        return other instanceof Club club && id.equals(club.id);
    }

    @Override
    public int hashCode() { return id.hashCode(); }
}
