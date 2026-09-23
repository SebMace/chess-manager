package relationship;

import domain.club.ClubRelationship;
import domain.club.vo.ClubId;
import domain.club.vo.Season;
import domain.person.vo.PersonId;
import domain.member.vo.FfeId;
import domain.member.vo.FfeLicense;
import domain.member.vo.FfeLicenseType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.util.UUID;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class FfeMembershipTests {
    private ClubRelationship relationship;
    private final ClubId clubId = new ClubId(UUID.fromString("00000000-0000-0000-0000-000000000002"));
    private final Season season = new Season(2026, 2027);
    private final FfeLicense initialLicense = new FfeLicense(new FfeId("B54321"), FfeLicenseType.B);

    @BeforeEach
    void setUp() {
        relationship = new ClubRelationship(
                new PersonId(UUID.fromString("00000000-0000-0000-0000-000000000001")),
                clubId
        ).registerLicense(initialLicense, season);
    }

    @ParameterizedTest
    @EnumSource(FfeLicenseType.class)
    void should_create_a_member_with_an_ffe_license(FfeLicenseType licenseType) {
        FfeLicense license = new FfeLicense(new FfeId("A12345"), licenseType);

        ClubRelationship licensedMember = new ClubRelationship(
                new PersonId(UUID.fromString("00000000-0000-0000-0000-000000000001")),
                clubId
        ).registerLicense(license, season);

        assertEquals(Optional.of(license.ffeId()), licensedMember.license(season).map(FfeLicense::ffeId));
        assertEquals(Optional.of(licenseType), licensedMember.license(season).map(FfeLicense::type));
    }

    @ParameterizedTest
    @EnumSource(FfeLicenseType.class)
    void should_assign_an_ffe_id_with_either_license_type(FfeLicenseType licenseType) {
        // Given: a member with an existing FFE license
        FfeId ffeId = new FfeId("A12345");

        // When: the member receives an A or B license
        relationship = relationship.registerLicense(new FfeLicense(ffeId, licenseType), season);

        // Then: both the identifier and the license type are recorded
        assertEquals(Optional.of(ffeId), relationship.license(season).map(FfeLicense::ffeId));
        assertEquals(Optional.of(licenseType), relationship.license(season).map(FfeLicense::type));
    }

    @ParameterizedTest
    @EnumSource(FfeLicenseType.class)
    void should_reject_a_license_without_an_ffe_id(FfeLicenseType licenseType) {
        assertThrows(IllegalArgumentException.class,
                () -> relationship.registerLicense(new FfeLicense(null, licenseType), season));

        assertEquals(Optional.of(initialLicense.ffeId()), relationship.license(season).map(FfeLicense::ffeId));
        assertEquals(Optional.of(initialLicense.type()), relationship.license(season).map(FfeLicense::type));
    }

    @Test
    void should_reject_an_ffe_id_without_a_license_type() {
        assertThrows(IllegalArgumentException.class,
                () -> relationship.registerLicense(new FfeLicense(new FfeId("A12345"), null), season));

        assertEquals(Optional.of(initialLicense.ffeId()), relationship.license(season).map(FfeLicense::ffeId));
        assertEquals(Optional.of(initialLicense.type()), relationship.license(season).map(FfeLicense::type));
    }

    @Test
    void should_preserve_ffe_registration_when_a_license_without_an_id_is_rejected() {
        FfeId ffeId = new FfeId("A12345");
        relationship = relationship.registerLicense(new FfeLicense(ffeId, FfeLicenseType.A), season);

        assertThrows(IllegalArgumentException.class,
                () -> relationship.registerLicense(new FfeLicense(null, FfeLicenseType.B), season));

        assertEquals(Optional.of(ffeId), relationship.license(season).map(FfeLicense::ffeId));
        assertEquals(Optional.of(FfeLicenseType.A), relationship.license(season).map(FfeLicense::type));
    }

    @Test
    void should_preserve_ffe_registration_when_a_missing_license_type_is_rejected() {
        FfeId ffeId = new FfeId("A12345");
        relationship = relationship.registerLicense(new FfeLicense(ffeId, FfeLicenseType.A), season);

        assertThrows(IllegalArgumentException.class,
                () -> relationship.registerLicense(new FfeLicense(new FfeId("B54321"), null), season));

        assertEquals(Optional.of(ffeId), relationship.license(season).map(FfeLicense::ffeId));
        assertEquals(Optional.of(FfeLicenseType.A), relationship.license(season).map(FfeLicense::type));
    }
}
