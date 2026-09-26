package acceptance.support;

import application.club.RegisterLicense;
import application.club.RegisterPartnership;
import application.club.IsExternalPlayer;
import club.InMemoryClubRepository;
import application.person.RecordPerson;
import application.prospect.RegisterProspect;
import domain.club.Club;
import domain.club.ClubAffiliations;
import domain.club.RelationshipStatus;
import domain.club.vo.ClubId;
import domain.club.vo.PostalAddress;
import domain.commune.CommuneCode;
import domain.club.vo.Season;
import domain.member.vo.FfeId;
import domain.member.vo.FfeLicense;
import domain.member.vo.FfeLicenseType;
import domain.person.Person;
import domain.person.vo.PersonId;
import person.InMemoryPersonRepository;
import relationship.InMemoryClubRelationshipRepository;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/** One application fixture per scenario; commands use the same repositories as observations. */
public class ClubManagementDriver {
    private static final UUID PERSON_UUID = UUID.fromString("00000000-0000-0000-0000-000000000001");
    private static final PostalAddress OFFICE = new PostalAddress("12 rue des Échecs", "45000", "Orléans");
    private final InMemoryClubRepository clubRepository = new InMemoryClubRepository();
    private final InMemoryPersonRepository people = new InMemoryPersonRepository();
    private final InMemoryClubRelationshipRepository relationships = new InMemoryClubRelationshipRepository();
    private final Map<String, Club> clubs = Map.of(
            "Orléans", new Club(new ClubId(UUID.fromString("00000000-0000-0000-0000-000000000002")), "Orléans", new CommuneCode("45234"), OFFICE, OFFICE),
            "Olivet", new Club(new ClubId(UUID.fromString("00000000-0000-0000-0000-000000000003")), "Olivet", new CommuneCode("45232"), OFFICE, OFFICE),
            "Gien", new Club(new ClubId(UUID.fromString("00000000-0000-0000-0000-000000000004")), "Gien", new CommuneCode("45155"), OFFICE, OFFICE));
    private Season season = new Season(2026, 2027);
    private PersonId personId;

    public ClubManagementDriver() {
        defineClub("Orléans", true);
        defineClub("Olivet", true);
        defineClub("Gien", false);
    }

    public void defineClub(String name, boolean managed) {
        clubRepository.save(new Club(clubId(name), name, managed, clubs.get(name).commune(), OFFICE, OFFICE));
    }

    public boolean isClubManaged(String name) { return clubRepository.find(clubId(name)).orElseThrow().managedByApplication(); }

    public void registerPartnership(String club) {
        ensurePerson();
        new RegisterPartnership(relationships).execute(personId, clubId(club));
    }

    public boolean isExternalPlayer() { return new IsExternalPlayer(relationships, clubRepository).execute(personId, season); }

    public PersonId relationshipPerson(String club) { return relationships.find(personId, clubId(club)).orElseThrow().personId(); }

    public void currentSeason(Season currentSeason) { season = currentSeason; }
    public Season currentSeason() { return season; }

    public void ensurePerson() {
        if (personId == null) personId = new RecordPerson(people).execute(PERSON_UUID, "Camille", "Martin");
    }

    public void registerProspect(String club, String firstName, String lastName, String email) {
        personId = registerProspect().execute(clubId(club), firstName, lastName, email);
    }

    public void registerExistingPersonAsProspect(String club) {
        ensurePerson();
        registerProspect().execute(personId, clubId(club));
    }

    public void registerLicense(String club, String type, Set<String> requestedPartners) {
        ensurePerson();
        FfeLicense license = type == null ? null : new FfeLicense(new FfeId("A12345"), FfeLicenseType.valueOf(type));
        new RegisterLicense(relationships, season).execute(personId, clubId(club), license,
                requestedPartners.stream().map(this::clubId).collect(Collectors.toSet()));
    }

    public void registerLicenseDetails(String club, String ffeId, String type, Season currentSeason) {
        ensurePerson();
        new RegisterLicense(relationships, currentSeason).execute(personId, clubId(club), ffeId,
                type == null ? null : FfeLicenseType.valueOf(type));
    }

    public Optional<FfeLicense> license(String club) {
        return relationships.find(personId, clubId(club)).flatMap(r -> r.license(season));
    }

    public boolean hasStatus(String club, RelationshipStatus status) {
        return relationships.find(personId, clubId(club)).filter(r -> r.status() == status).isPresent();
    }

    public boolean hasRelationship(String club) { return relationships.find(personId, clubId(club)).isPresent(); }

    public boolean isAffiliated(String club) { return personId != null && license(club).isPresent(); }

    public Optional<String> affiliation(Season requestedSeason) {
        return new ClubAffiliations(relationships.findByPerson(personId)).club(requestedSeason)
                .map(id -> clubs.values().stream().filter(club -> club.id().equals(id)).findFirst().orElseThrow().name());
    }

    public boolean hasAnyAffiliation() {
        return relationships.findByPerson(personId).stream().anyMatch(r -> r.status() == RelationshipStatus.MEMBER);
    }

    public Person person() { return people.find(personId).orElseThrow(); }
    public ClubId clubId(String club) { return club == null ? null : clubs.get(club).id(); }

    private RegisterProspect registerProspect() {
        return new RegisterProspect(people, relationships, season, () -> new PersonId(PERSON_UUID));
    }
}
