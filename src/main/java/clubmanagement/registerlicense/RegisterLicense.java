package clubmanagement.registerlicense;

import clubmanagement.ports.ClubRelationshipRepository;
import clubmanagement.domain.club.ClubRelationship;
import clubmanagement.domain.club.ClubAffiliations;
import clubmanagement.domain.club.vo.ClubId;
import clubmanagement.domain.club.vo.Season;
import clubmanagement.domain.member.vo.FfeLicense;
import clubmanagement.domain.person.vo.PersonId;
import java.util.Set;

public class RegisterLicense {
    private final ClubRelationshipRepository relationships;
    private final Season currentSeason;

    public RegisterLicense(ClubRelationshipRepository relationships, Season currentSeason) {
        this.relationships = relationships;
        this.currentSeason = currentSeason;
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
