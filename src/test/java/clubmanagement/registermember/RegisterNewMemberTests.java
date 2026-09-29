package clubmanagement.registermember;

import clubmanagement.domain.club.vo.ClubId;
import clubmanagement.domain.club.vo.Season;
import clubmanagement.domain.member.vo.FfeId;
import clubmanagement.domain.member.vo.FfeLicenseType;
import clubmanagement.domain.person.Person;
import clubmanagement.domain.person.vo.PersonId;
import clubmanagement.ports.InMemoryClubRelationshipRepository;
import clubmanagement.ports.InMemoryPersonRepository;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.UUID;

import static clubmanagement.domain.club.RelationshipStatus.MEMBER;
import static org.junit.jupiter.api.Assertions.*;

class RegisterNewMemberTests {
    private final PersonId camilleId = new PersonId(UUID.fromString("00000000-0000-0000-0000-000000000001"));
    private final ClubId orleans = new ClubId(UUID.fromString("00000000-0000-0000-0000-000000000002"));
    private final Season season = new Season(2026, 2027);
    private final InMemoryPersonRepository people = new InMemoryPersonRepository();
    private final InMemoryClubRelationshipRepository relationships = new InMemoryClubRelationshipRepository();

    @Test
    void a_new_member_is_recorded_as_a_person_with_their_name_and_ffe_identifier() {
        RegisterNewMember registerNewMember = new RegisterNewMember(people, relationships, season, () -> camilleId);

        PersonId newMember = registerNewMember.registerNewMember(orleans, "Camille", "Martin",
                new FfeId("K58213"), FfeLicenseType.A);

        assertEquals(camilleId, newMember);
        Person camille = people.find(newMember).orElseThrow();
        assertEquals("Camille", camille.firstName());
        assertEquals("Martin", camille.lastName());
        assertEquals(Optional.of(new FfeId("K58213")), camille.ffeId());
        var membership = relationships.find(newMember, orleans).orElseThrow();
        assertEquals(MEMBER, membership.status());
        assertEquals(FfeLicenseType.A, membership.license(season).orElseThrow().type());
    }
}
