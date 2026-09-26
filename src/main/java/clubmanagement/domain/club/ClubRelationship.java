package clubmanagement.domain.club;

import clubmanagement.domain.person.vo.PersonId;
import clubmanagement.domain.club.vo.ClubId;
import clubmanagement.domain.club.vo.Season;
import clubmanagement.domain.member.vo.FfeLicense;

import java.util.Objects;
import java.util.Optional;
import java.util.Map;
import java.util.HashMap;

public final class ClubRelationship {
    private final PersonId personId;
    private final ClubId clubId;
    private final Map<Season, FfeLicense> licenses;
    private final RelationshipStatus status;

    public ClubRelationship(PersonId personId, ClubId clubId) {
        this(personId, clubId, Map.of(), RelationshipStatus.PROSPECT);
    }

    private ClubRelationship(PersonId personId, ClubId clubId, Map<Season, FfeLicense> licenses,
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

    public PersonId personId() { return personId; }
    public ClubId clubId() { return clubId; }
    public RelationshipStatus status() {
        return status;
    }

    public ClubRelationship registerLicense(FfeLicense license, Season season) {
        if (license == null) throw new IllegalArgumentException("license cannot be null");
        if (season == null) throw new IllegalArgumentException("season cannot be null");
        Map<Season, FfeLicense> updated = new HashMap<>(licenses);
        updated.put(season, license);
        return new ClubRelationship(personId, clubId, updated, RelationshipStatus.MEMBER);
    }

    public Optional<FfeLicense> license(Season season) {
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
