package relationship;

import clubmanagement.ports.ClubRelationshipRepository;
import clubmanagement.domain.person.vo.PersonId;
import clubmanagement.domain.club.vo.ClubId;
import clubmanagement.domain.club.ClubRelationship;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.List;

public class InMemoryClubRelationshipRepository implements ClubRelationshipRepository {
    private record Identity(PersonId personId, ClubId clubId) {}
    private final Map<Identity, ClubRelationship> relationships = new HashMap<>();
    public Optional<ClubRelationship> find(PersonId personId, ClubId clubId) {
        Identity key = new Identity(personId, clubId);
        return Optional.ofNullable(relationships.get(key));

    }
    public void save(ClubRelationship relationship) {
        relationships.put(new Identity(relationship.personId(), relationship.clubId()), relationship);
    }

    public List<ClubRelationship> findByPerson(PersonId personId) {
        return relationships.values().stream()
                .filter(relationship -> relationship.personId().equals(personId)).toList();
    }

    public void delete(PersonId personId, ClubId clubId) {
        relationships.remove(new Identity(personId, clubId));
    }
}
