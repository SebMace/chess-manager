package clubmanagement.domain.club;

import clubmanagement.domain.member.vo.FfeLicenseType;
import clubmanagement.domain.person.Person;
import clubmanagement.domain.person.vo.PersonId;
import clubmanagement.domain.club.vo.ClubId;
import clubmanagement.domain.club.vo.Season;

import java.util.Objects;
import java.util.Optional;
import java.util.Map;
import java.util.HashMap;

public final class ClubRelationship {
    private final PersonId personId;
    private final ClubId clubId;
    /** The type of the license taken with the club, for each season; the FFE identifier belongs to the person. */
    private final Map<Season, FfeLicenseType> licenses;
    private final RelationshipStatus status;

    public ClubRelationship(PersonId personId, ClubId clubId) {
        this(personId, clubId, Map.of(), RelationshipStatus.PROSPECT);
    }

    private ClubRelationship(PersonId personId, ClubId clubId, Map<Season, FfeLicenseType> licenses,
                             RelationshipStatus status) {
        if (personId == null) {
            throw new IllegalArgumentException("personId cannot be null");
        }
        if (clubId == null) {
            throw new IllegalArgumentException("clubId cannot be null");
        }
        this.personId = personId;
        this.clubId = clubId;
        this.licenses = Map.copyOf(licenses);
        this.status = status;
    }

    /** A licensed player is a member, for the season of their license, of the club where they were registered. */
    public static ClubRelationship membershipOf(Person licensedPlayer, ClubId clubId, FfeLicenseType licenseType,
                                                Season season) {
        if (licensedPlayer.ffeId().isEmpty()) throw new IllegalArgumentException("Only a licensed player can be a member");
        return new ClubRelationship(licensedPlayer.id(), clubId).registerLicense(licenseType, season);
    }

    public PersonId personId() { return personId; }
    public ClubId clubId() { return clubId; }
    public RelationshipStatus status() {
        return status;
    }

    public ClubRelationship registerLicense(FfeLicenseType licenseType, Season season) {
        if (licenseType == null) throw new IllegalArgumentException("licenseType cannot be null");
        if (season == null) throw new IllegalArgumentException("season cannot be null");
        Map<Season, FfeLicenseType> updated = new HashMap<>(licenses);
        updated.put(season, licenseType);
        return new ClubRelationship(personId, clubId, updated, RelationshipStatus.MEMBER);
    }

    public Optional<FfeLicenseType> license(Season season) {
        return Optional.ofNullable(licenses.get(season));
    }

    public ClubRelationship registerPartnership() {
        return new ClubRelationship(personId, clubId, licenses, RelationshipStatus.PARTNER);
    }

    public Optional<ClubRelationship> afterAffiliationElsewhere(boolean wishesToRemainPartner) {
        if (status != RelationshipStatus.PROSPECT) return Optional.of(this);
        return wishesToRemainPartner
                ? Optional.of(new ClubRelationship(personId, clubId, licenses, RelationshipStatus.PARTNER))
                : Optional.empty();
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof ClubRelationship relationship
                && personId.equals(relationship.personId) && clubId.equals(relationship.clubId);
    }

    @Override
    public int hashCode() { return Objects.hash(personId, clubId); }
}
