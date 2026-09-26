package clubmanagement.registerpartnership;

import clubmanagement.ports.ClubRelationshipRepository;
import clubmanagement.domain.club.ClubRelationship;
import clubmanagement.domain.club.vo.ClubId;
import clubmanagement.domain.person.vo.PersonId;

public class RegisterPartnership {
    private final ClubRelationshipRepository relationships;

    public RegisterPartnership(ClubRelationshipRepository relationships) { this.relationships = relationships; }

    public void execute(PersonId personId, ClubId clubId) {
        ClubRelationship relationship = relationships.find(personId, clubId)
                .orElseGet(() -> new ClubRelationship(personId, clubId));
        relationships.save(relationship.registerPartnership());
    }
}
