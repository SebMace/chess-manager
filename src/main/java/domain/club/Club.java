package domain.club;

import domain.club.vo.ClubId;
import domain.club.vo.CommitteeCode;

import java.util.Optional;

public final class Club {
    private final ClubId id;
    private final String name;
    private final boolean managedByApplication;

    public Club(ClubId id, String name) {
        this(id, name, false);
    }

    public Club(ClubId id, String name, boolean managedByApplication) {
        if (id == null) throw new IllegalArgumentException("clubId cannot be null");
        this.id = id;
        this.name = name;
        this.managedByApplication = managedByApplication;
    }

    public ClubId id() { return id; }
    public String name() { return name; }
    public boolean managedByApplication() { return managedByApplication; }
    public Optional<CommitteeCode> committee() { return Optional.empty(); }

    @Override
    public boolean equals(Object other) {
        return other instanceof Club club && id.equals(club.id);
    }

    @Override
    public int hashCode() { return id.hashCode(); }
}
