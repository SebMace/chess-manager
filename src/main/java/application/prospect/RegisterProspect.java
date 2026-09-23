package application.prospect;

import application.club.ClubRelationshipRepository;
import application.person.PersonRepository;
import domain.club.ClubRelationship;
import domain.club.ClubAffiliations;
import domain.club.vo.ClubId;
import domain.club.vo.Season;
import domain.person.Person;
import domain.person.vo.PersonId;

import java.util.function.Supplier;

/**
 * Use case RegisterProspect
 */
public class RegisterProspect {
    private final PersonRepository people;
    private final ClubRelationshipRepository relationships;
    private final Season currentSeason;
    private final Supplier<PersonId> identities;

    public RegisterProspect(PersonRepository people, ClubRelationshipRepository relationships,
                            Season currentSeason, Supplier<PersonId> identities) {
        this.people = people;
        this.relationships = relationships;
        this.currentSeason = currentSeason;
        this.identities = identities;
    }

    public PersonId execute(ClubId clubId, String firstName, String lastName, String email) {
        PersonId id = identities.get();
        ClubRelationship relationship = new ClubRelationship(id, clubId);
        people.save(new Person(id, firstName, lastName, email));
        relationships.save(relationship);
        return id;
    }

    public void execute(PersonId personId, ClubId clubId) {
        people.find(personId).orElseThrow();
        new ClubAffiliations(relationships.findByPerson(personId)).requireUnlicensed(currentSeason);
        relationships.save(new ClubRelationship(personId, clubId));
    }
}
