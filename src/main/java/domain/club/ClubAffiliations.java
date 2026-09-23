package domain.club;

import domain.club.vo.ClubId;
import domain.club.vo.Season;

import java.util.List;
import java.util.Optional;

/** Rules involving the relationships of one person across clubs. */


public final class ClubAffiliations {
    private final List<ClubRelationship> relationships;

    public ClubAffiliations(List<ClubRelationship> relationships) {
        this.relationships = List.copyOf(relationships);
    }

    public Optional<ClubId> club(Season season) {
        return relationships.stream().filter(relationship -> relationship.license(season).isPresent())
                .map(ClubRelationship::clubId).findFirst();
    }

    public void requireAvailable(ClubId clubId, Season season) {
        if (club(season).filter(currentClub -> !currentClub.equals(clubId)).isPresent()) {
            throw new IllegalStateException("Person already affiliated for this season");
        }
    }

    public void requireUnlicensed(Season season) {
        if (club(season).isPresent()) {
            throw new IllegalStateException("An affiliated person cannot be a prospect");
        }
    }
}
