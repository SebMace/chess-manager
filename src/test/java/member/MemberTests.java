package member;

import domain.exceptions.FideIdAlreadyAssignedException;
import domain.member.entities.Member;
import domain.member.vo.EloRating;
import domain.member.vo.FideId;
import domain.member.vo.MemberId;
import domain.member.vo.FfeId;
import domain.member.vo.FfeLicense;
import domain.member.vo.FfeLicenseType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class MemberTests {
    private final FfeLicense ffeLicense = new FfeLicense(new FfeId("A12345"), FfeLicenseType.A);

    // Day 1 10/07/2026
    @Test
    void should_create_a_member_with_a_first_name_and_a_last_name() {
        Member member = new Member(
                new MemberId(UUID.randomUUID()),
                "Magnus",
                "Carlsen",
                ffeLicense
        );
        assertEquals("Magnus", member.firstName());
        assertEquals("Carlsen", member.lastName());
    }
    // Day 2 11/07/2026
    @Test
    void should_reject_a_negative_elo_rating() {
        assertThrows(IllegalArgumentException.class, () -> new EloRating(-1));
    }

    // Day 3 12/07/2026
    // Member as an entity is immutable but its state's value object evolves.
    // Member's identity never changes

    @Test
    void should_change_the_elo_rating_of_a_member() {
        //Given : one existing member with an initial elo rating.
        Member member = new Member(new MemberId(UUID.randomUUID()),"Sébastien", "Macé", ffeLicense);
        member.giveEloRating(new EloRating(1500));
        // when : his rating is updated
        member.recordEloRating(new EloRating(1600));
        // then : tha member gets the new rating
        assertEquals(new EloRating(1600),member.eloRating());
    }

    // Day 4 13/07/2026

    @Test
    void should_reject_replacing_an_existing_fide_id() throws FideIdAlreadyAssignedException {
    //Given  : a member with an existing FIDE ID
        Member member = new Member(new MemberId(UUID.randomUUID()),"Sébastien", "Macé", ffeLicense);
        member.registerFideId(new FideId(641839L));
    // When / Then  : assigning another FIDE ID is rejected
        assertThrows(FideIdAlreadyAssignedException.class, () -> member.registerFideId(new FideId(1503014L)));
    }
    // Day 5 14/07/2026
    @ParameterizedTest
    @ValueSource(longs = {-1L, 0L})
    void should_reject_a_non_positive_fide_id(long invalidFideId)  {
        assertThrows(IllegalArgumentException.class, () -> new FideId(invalidFideId));
    }
    // Day 6
    @Test
    void should_create_a_member_with_an_internal_id_and_without_a_fide_id() {
        // Given  : a MemberId, first name, last_name, no FideId
        MemberId memberId = new MemberId(UUID.randomUUID());
        Member member = new Member(memberId, "Anatoly", "Karpov", ffeLicense);
        assertEquals(memberId, member.id());
        assertTrue(member.fideId().isEmpty());
    }
    // Day 7
    @Test
    void should_reject_a_member_without_a_member_id() {
        assertThrows(IllegalArgumentException.class, () -> new Member(null, "Anatoly", "Karpov", ffeLicense));
    }
    // Day 8
    @Test
    void should_reject_a_null_uuid_in_member_id() {
        assertThrows(IllegalArgumentException.class, () -> new MemberId(null));
    }
    // Day 9

    @Test
    void should_consider_members_with_the_same_member_id_as_the_same_member() {
        // Given: two members with equal identifiers and different names
        UUID uuid = UUID.fromString("00000000-0000-0000-0000-000000000001");
        Member member1 = new Member(new MemberId(uuid), "Anatoly", "Karpov", ffeLicense);
        Member member2 = new Member(new MemberId(uuid), "Magnus", "Carlsen", ffeLicense);
        // When / Then: they represent the same member
        assertEquals(member1, member2);
    }

    @Test
    void should_consider_homonyms_as_different_members_if_their_identities_are_different () {
        MemberId memberId1 = new MemberId(UUID.fromString("00000000-0000-0000-0000-000000000001"));
        MemberId memberId2 = new MemberId(UUID.fromString("00000000-0000-0000-0000-000000000002"));
        Member member1 = new Member(memberId1, "Anatoly", "Karpov", ffeLicense);
        Member member2 = new Member(memberId2, "Anatoly", "Karpov", ffeLicense);
        assertNotEquals(member1, member2);
    }

    @Test
    void should_reject_a_member_without_an_ffe_license() {
        MemberId memberId = new MemberId(
                UUID.fromString("00000000-0000-0000-0000-000000000001")
        );

        assertThrows(IllegalArgumentException.class,
                () -> new Member(memberId, "Camille", "Martin", null));
    }
}
