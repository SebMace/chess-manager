package application.club;

import domain.club.ClubAffiliations;
import domain.club.vo.Season;
import domain.person.vo.PersonId;

/**
 * An external player is a player licensed in another club
 */
public class IsExternalPlayer {
    private final ClubRelationshipRepository relationships;
    private final ClubRepository clubs;

    public IsExternalPlayer(ClubRelationshipRepository relationships, ClubRepository clubs) {
        this.relationships = relationships;
        this.clubs = clubs;
    }

    public boolean execute(PersonId personId, Season season) {
        return new ClubAffiliations(relationships.findByPerson(personId)).club(season)
                .map(id -> !clubs.find(id).orElseThrow().managedByApplication()).orElse(false);
    }
}
