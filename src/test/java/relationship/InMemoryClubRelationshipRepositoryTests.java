package relationship;


import domain.person.vo.PersonId;
import domain.club.vo.ClubId;
import domain.club.ClubRelationship;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class InMemoryClubRelationshipRepositoryTests {

    @Test
    void should_find_a_saved_prospect_relationship() {
        // Given: a repository and Camille's relationship with Orléans
        InMemoryClubRelationshipRepository repository = new InMemoryClubRelationshipRepository();
        PersonId personId = new PersonId(UUID.fromString("00000000-0000-0000-0000-000000000001"));
        ClubId clubId = new ClubId(
                UUID.fromString("00000000-0000-0000-0000-000000000002"));
        ClubRelationship relationship = new ClubRelationship(personId, clubId);

        // When: the relationship is saved
        repository.save(relationship);

        // Then: it can be found using both identities
        assertEquals(Optional.of(relationship), repository.find(personId, clubId));
    }

    @Test
    void should_find_no_relationship_in_an_empty_repository() {
        // Given: an empty repository and the identities of a person and a club
        InMemoryClubRelationshipRepository repository = new InMemoryClubRelationshipRepository();
        PersonId personId = new PersonId(UUID.fromString("00000000-0000-0000-0000-000000000001"));
        ClubId clubId = new ClubId(
                UUID.fromString("00000000-0000-0000-0000-000000000002"));

        // When: their prospect relationship is searched for
        Optional<ClubRelationship> relationship = repository.find(personId, clubId);

        // Then: no relationship is found
        assertEquals(Optional.empty(), relationship);
    }

    @Test
    void should_not_find_a_prospect_relationship_in_another_club() {
        InMemoryClubRelationshipRepository repository = new InMemoryClubRelationshipRepository();
        PersonId personId = new PersonId(UUID.fromString("00000000-0000-0000-0000-000000000003"));
        ClubId clubIdOrleans = new ClubId(
                UUID.fromString("00000000-0000-0000-0000-000000000004")
        );
        ClubId clubIdOlivet = new ClubId(
                UUID.fromString("00000000-0000-0000-0000-000000000005")
        );
        ClubRelationship relationship = new ClubRelationship(personId,clubIdOrleans);
        repository.save(relationship);
        assertEquals(Optional.empty(), repository.find(personId, clubIdOlivet));

    }

    @Test
    void should_not_find_a_prospect_relationship_for_another_person() {
        InMemoryClubRelationshipRepository repository = new InMemoryClubRelationshipRepository();
        PersonId personIdCamille = new PersonId(UUID.fromString("00000000-0000-0000-0000-000000000006"));
        PersonId personIdClaude = new PersonId(UUID.fromString("00000000-0000-0000-0000-000000000007"));
        ClubId clubIdOrleans = new ClubId(
                UUID.fromString("00000000-0000-0000-0000-000000000008")
        );
        ClubRelationship relationship = new ClubRelationship(personIdCamille,clubIdOrleans);
        repository.save(relationship);
        assertEquals(Optional.empty(), repository.find(personIdClaude, clubIdOrleans));
    }

    @Test
    void should_preserve_relationships_already_saved() {
        InMemoryClubRelationshipRepository repository = new InMemoryClubRelationshipRepository();
        PersonId personIdCamille = new PersonId(UUID.fromString("00000000-0000-0000-0000-000000000001"));
        PersonId personIdClaude = new PersonId(UUID.fromString("00000000-0000-0000-0000-000000000002"));
        ClubId clubIdOrleans = new ClubId(
                UUID.fromString("00000000-0000-0000-0000-000000000003")
        );
        ClubRelationship relationshipCamille = new ClubRelationship(personIdCamille,clubIdOrleans);

        repository.save(relationshipCamille);
        ClubRelationship relationshipClaude = new ClubRelationship(personIdClaude,clubIdOrleans);
        repository.save(relationshipClaude);

        assertEquals(Optional.of(relationshipCamille), repository.find(personIdCamille  , clubIdOrleans));
        assertEquals(
                Optional.of(relationshipClaude),
                repository.find(personIdClaude, clubIdOrleans)
        );
    }
}
