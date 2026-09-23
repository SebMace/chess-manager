package application.club;

import domain.club.ClubRelationship;
import domain.club.vo.ClubId;
import domain.person.vo.PersonId;

public class RegisterPartnership {
    private final ClubRelationshipRepository relationships;

    public RegisterPartnership(ClubRelationshipRepository relationships) { this.relationships = relationships; }

    public void execute(PersonId personId, ClubId clubId) {
        ClubRelationship relationship = relationships.find(personId, clubId)
                .orElseGet(() -> new ClubRelationship(personId, clubId));
        relationships.save(relationship.registerPartnership());
    }
}
