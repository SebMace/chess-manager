package clubmanagement.ports;

import clubmanagement.domain.club.ClubRelationship;
import clubmanagement.domain.club.vo.ClubId;
import clubmanagement.domain.person.vo.PersonId;

import java.util.Optional;
import java.util.List;

public interface ClubRelationshipRepository {
    Optional<ClubRelationship> find(PersonId personId, ClubId clubId);
    void save(ClubRelationship relationship);
    List<ClubRelationship> findByPerson(PersonId personId);
    void delete(PersonId personId, ClubId clubId);
}
