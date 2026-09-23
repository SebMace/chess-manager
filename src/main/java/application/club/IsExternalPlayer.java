package application.club;

import domain.club.ClubAffiliations;
import domain.club.vo.Season;
import domain.person.vo.PersonId;

/**
 * An external player is licensed, for the season, in a club not managed by the application.
 * A club unknown to the application is, by definition, not managed by it.
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
                .map(id -> clubs.find(id).map(club -> !club.managedByApplication()).orElse(true))
                .orElse(false);
    }
}
