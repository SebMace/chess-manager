package application.club;

import domain.club.ClubRelationship;
import domain.club.vo.ClubId;
import domain.person.vo.PersonId;

import java.util.Optional;
import java.util.List;

public interface ClubRelationshipRepository {
    Optional<ClubRelationship> find(PersonId personId, ClubId clubId);
    void save(ClubRelationship relationship);
    List<ClubRelationship> findByPerson(PersonId personId);
    void delete(PersonId personId, ClubId clubId);
}
