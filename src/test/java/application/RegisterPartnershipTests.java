package application;

import clubmanagement.registerlicense.RegisterLicense;
import clubmanagement.registerpartnership.RegisterPartnership;
import clubmanagement.domain.club.RelationshipStatus;
import clubmanagement.domain.club.vo.ClubId;
import clubmanagement.domain.club.vo.Season;
import clubmanagement.domain.member.vo.FfeId;
import clubmanagement.domain.member.vo.FfeLicense;
import clubmanagement.domain.member.vo.FfeLicenseType;
import clubmanagement.domain.person.vo.PersonId;
import org.junit.jupiter.api.Test;
import relationship.InMemoryClubRelationshipRepository;

import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;

class RegisterPartnershipTests {
    private final PersonId person = new PersonId(UUID.fromString("00000000-0000-0000-0000-000000000001"));
    private final ClubId orleans = new ClubId(UUID.fromString("00000000-0000-0000-0000-000000000002"));
    private final ClubId olivet = new ClubId(UUID.fromString("00000000-0000-0000-0000-000000000003"));
    private final Season season = new Season(2026, 2027);
    private final FfeLicense license = new FfeLicense(new FfeId("B12345"), FfeLicenseType.B);
    private final InMemoryClubRelationshipRepository relationships = new InMemoryClubRelationshipRepository();

    @Test
    void should_save_a_partnership_without_changing_membership_elsewhere() {
        new RegisterLicense(relationships, season).execute(person, olivet, license);

        new RegisterPartnership(relationships).execute(person, orleans);

        var saved = relationships.find(person, orleans).orElseThrow();
        assertEquals(RelationshipStatus.PARTNER, saved.status());
        assertEquals(person, saved.personId());
        assertTrue(saved.license(season).isEmpty());
        assertEquals(license, relationships.find(person, olivet).orElseThrow().license(season).orElseThrow());
    }
}
