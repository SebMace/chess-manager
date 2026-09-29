package clubmanagement.registermember;

import clubmanagement.domain.club.ClubRelationship;
import clubmanagement.domain.club.vo.ClubId;
import clubmanagement.domain.club.vo.Season;
import clubmanagement.domain.member.vo.FfeId;
import clubmanagement.domain.member.vo.FfeLicenseType;
import clubmanagement.domain.person.Person;
import clubmanagement.domain.person.vo.PersonId;
import clubmanagement.ports.ClubRelationshipRepository;
import clubmanagement.ports.PersonRepository;

import java.util.function.Supplier;

/** An administrator registers, as a member of a club, a licensed player the application does not know yet. */
public class RegisterNewMember {
    private final PersonRepository people;
    private final ClubRelationshipRepository relationships;
    private final Season currentSeason;
    private final Supplier<PersonId> identities;

    public RegisterNewMember(PersonRepository people, ClubRelationshipRepository relationships,
                             Season currentSeason, Supplier<PersonId> identities) {
        this.people = people;
        this.relationships = relationships;
        this.currentSeason = currentSeason;
        this.identities = identities;
    }

    public PersonId registerNewMember(ClubId clubId, String firstName, String lastName, FfeId ffeId,
                                      FfeLicenseType licenseType) {
        Person newMember = Person.licensedPlayer(identities.get(), firstName, lastName, ffeId);
        ClubRelationship membership = ClubRelationship.membershipOf(newMember, clubId, licenseType, currentSeason);
        people.save(newMember);
        relationships.save(membership);
        return newMember.id();
    }
}
