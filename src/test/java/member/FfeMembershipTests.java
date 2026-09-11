package member;

import domain.member.entities.Member;
import domain.member.vo.MemberId;
import domain.member.vo.FfeId;
import domain.member.vo.FfeLicenseType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.util.UUID;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class FfeMembershipTests {
    private Member member;

    @BeforeEach
    void setUp() {
        member = new Member(
                new MemberId(UUID.fromString("00000000-0000-0000-0000-000000000001")),
                "Anatoly", "Karpov"
        );
    }

    @Test
    void should_create_a_member_without_an_ffe_id() {
        assertTrue(member.ffeId().isEmpty());
        assertTrue(member.ffeLicenseType().isEmpty());
    }

    @ParameterizedTest
    @EnumSource(FfeLicenseType.class)
    void should_assign_an_ffe_id_with_either_license_type(FfeLicenseType licenseType) {
        // Given: a member without FFE registration
        FfeId ffeId = new FfeId("A12345");

        // When: the member receives an A or B license
        member.registerFfeLicense(ffeId, licenseType);

        // Then: both the identifier and the license type are recorded
        assertEquals(Optional.of(ffeId), member.ffeId());
        assertEquals(Optional.of(licenseType), member.ffeLicenseType());
    }

    @ParameterizedTest
    @EnumSource(FfeLicenseType.class)
    void should_reject_a_license_without_an_ffe_id(FfeLicenseType licenseType) {
        assertThrows(IllegalArgumentException.class,
                () -> member.registerFfeLicense(null, licenseType));

        assertTrue(member.ffeId().isEmpty());
        assertTrue(member.ffeLicenseType().isEmpty());
    }

    @Test
    void should_reject_an_ffe_id_without_a_license_type() {
        assertThrows(IllegalArgumentException.class,
                () -> member.registerFfeLicense(new FfeId("A12345"), null));

        assertTrue(member.ffeId().isEmpty());
        assertTrue(member.ffeLicenseType().isEmpty());
    }

    @Test
    void should_preserve_ffe_registration_when_a_license_without_an_id_is_rejected() {
        FfeId ffeId = new FfeId("A12345");
        member.registerFfeLicense(ffeId, FfeLicenseType.A);

        assertThrows(IllegalArgumentException.class,
                () -> member.registerFfeLicense(null, FfeLicenseType.B));

        assertEquals(Optional.of(ffeId), member.ffeId());
        assertEquals(Optional.of(FfeLicenseType.A), member.ffeLicenseType());
    }

    @Test
    void should_preserve_ffe_registration_when_a_missing_license_type_is_rejected() {
        FfeId ffeId = new FfeId("A12345");
        member.registerFfeLicense(ffeId, FfeLicenseType.A);

        assertThrows(IllegalArgumentException.class,
                () -> member.registerFfeLicense(new FfeId("B54321"), null));

        assertEquals(Optional.of(ffeId), member.ffeId());
        assertEquals(Optional.of(FfeLicenseType.A), member.ffeLicenseType());
    }
}
