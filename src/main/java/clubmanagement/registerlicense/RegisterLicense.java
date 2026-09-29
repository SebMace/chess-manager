package clubmanagement.registerlicense;

import clubmanagement.ports.ClubRelationshipRepository;
import clubmanagement.ports.PersonRepository;
import clubmanagement.domain.member.vo.FfeId;
import clubmanagement.domain.member.vo.FfeLicenseType;
import clubmanagement.domain.person.Person;
import clubmanagement.domain.club.ClubRelationship;
import clubmanagement.domain.club.ClubAffiliations;
import clubmanagement.domain.club.vo.ClubId;
import clubmanagement.domain.club.vo.Season;
import clubmanagement.domain.member.vo.FfeLicense;
import clubmanagement.domain.person.vo.PersonId;
import java.util.Set;

public class RegisterLicense {
    private final PersonRepository people;
    private final ClubRelationshipRepository relationships;
    private final Season currentSeason;

    public RegisterLicense(PersonRepository people, ClubRelationshipRepository relationships, Season currentSeason) {
        this.people = people;
        this.relationships = relationships;
        this.currentSeason = currentSeason;
    }

    /** A person known to the application, such as a prospect, takes their first license and so their FFE identifier. */
    public void registerFirstLicenseOf(PersonId personId, ClubId clubId, FfeId ffeId, FfeLicenseType licenseType) {
        Person newLicensee = people.find(personId).orElseThrow();
        newLicensee.takesFirstLicense(ffeId);
        registerLicenseOf(personId, clubId, new FfeLicense(ffeId, licenseType));
        people.save(newLicensee);
    }

    /** A licensed player takes the license of the current season, under the FFE identifier they already have. */
    public void renewLicenseOf(PersonId personId, ClubId clubId, FfeLicenseType licenseType) {
        Person licensedPlayer = people.find(personId).orElseThrow();
        registerLicenseOf(personId, clubId, new FfeLicense(licensedPlayer.ffeId().orElseThrow(), licenseType));
    }

    public void registerLicenseOf(PersonId personId, ClubId clubId, FfeLicense license) {
        registerLicenseOfKeepingPartnerships(personId, clubId, license, Set.of());
    }

    /** Keeps as partnerships the prospect relationships with the requested clubs; ends the others. */
    public void registerLicenseOfKeepingPartnerships(PersonId personId, ClubId clubId, FfeLicense license,
                                                     Set<ClubId> requestedPartnerships) {
        new ClubAffiliations(relationships.findByPerson(personId)).requireAvailable(clubId, currentSeason);
        ClubRelationship relationship = relationships.find(personId, clubId)
                .orElseGet(() -> new ClubRelationship(personId, clubId));
        relationships.save(relationship.registerLicense(license, currentSeason));
        for (ClubRelationship other : relationships.findByPerson(personId)) {
            if (!other.clubId().equals(clubId)) {
                other.afterAffiliationElsewhere(requestedPartnerships.contains(other.clubId()))
                        .ifPresentOrElse(relationships::save, () -> relationships.delete(personId, other.clubId()));
            }
        }
    }
}
