package domain.club;

import domain.club.vo.ClubId;
import domain.club.vo.CommitteeCode;
import domain.club.vo.FfeClubId;
import domain.club.vo.PostalAddress;
import domain.commune.CommuneCode;

import java.util.Optional;

public final class Club {
    private final ClubId id;
    private final String name;
    private final boolean managedByApplication;
    private final CommitteeCode committee;
    private final FfeClubId ffeClubId;
    private final CommuneCode commune;
    private final PostalAddress registeredOffice;
    private final PostalAddress playingVenue;

    public Club(ClubId id, String name, CommuneCode commune, PostalAddress registeredOffice, PostalAddress playingVenue) {
        this(id, name, false, commune, registeredOffice, playingVenue);
    }

    public Club(ClubId id, String name, boolean managedByApplication, CommuneCode commune,
                PostalAddress registeredOffice, PostalAddress playingVenue) {
        this(id, name, managedByApplication, null, null, commune, registeredOffice, playingVenue);
    }

    public Club(ClubId id, String name, boolean managedByApplication, CommitteeCode committee,
                FfeClubId ffeClubId, CommuneCode commune, PostalAddress registeredOffice,
                PostalAddress playingVenue) {
        if (id == null) throw new IllegalArgumentException("clubId cannot be null");
        if (commune == null) throw new IllegalArgumentException("A club cannot exist without its commune");
        if (registeredOffice == null) throw new IllegalArgumentException("A club cannot exist without its registered office");
        if (playingVenue == null) throw new IllegalArgumentException("A club cannot exist without its playing venue");
        this.id = id;
        this.name = name;
        this.managedByApplication = managedByApplication;
        this.committee = committee;
        this.ffeClubId = ffeClubId;
        this.commune = commune;
        this.registeredOffice = registeredOffice;
        this.playingVenue = playingVenue;
    }

    public ClubId id() { return id; }
    public String name() { return name; }
    public boolean managedByApplication() { return managedByApplication; }
    public Optional<CommitteeCode> committee() { return Optional.ofNullable(committee); }
    public Optional<FfeClubId> ffeClubId() { return Optional.ofNullable(ffeClubId); }
    public CommuneCode commune() { return commune; }
    public PostalAddress registeredOffice() { return registeredOffice; }
    public PostalAddress playingVenue() { return playingVenue; }

    @Override
    public boolean equals(Object other) {
        return other instanceof Club club && id.equals(club.id);
    }

    @Override
    public int hashCode() { return id.hashCode(); }
}
