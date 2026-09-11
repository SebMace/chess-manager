# Chess Manager

Chess Manager is a learning project built progressively with Domain-Driven Design,
Test-Driven Development and Clean / Hexagonal Architecture principles.

## Current bounded context: Club Management

The current implementation focuses on members and their seasonal club affiliations.
`Member` is the canonical term in this context: it represents the person whose
identity and affiliations a club administrator manages. A member can be created
before being affiliated to a club.

This context does not yet implement games, tournament participation, authentication
or club administration workflows. Those concerns must not determine the current
member model.

## Ubiquitous language

| Term | Meaning in the current model |
| --- | --- |
| `Member` | Entity with a durable internal identity, first name, last name and seasonal affiliations. |
| `MemberId` | Internal identity backed by a non-null UUID; independent of names and FIDE registration. |
| `ClubId` | Identity of the club referenced by an affiliation, backed by a non-null UUID. |
| `Season` | Value object containing a beginning year and an ending year. |
| Affiliation | Association of a member with one club for a given season, managed through `Member.affiliateTo`. |
| `FideId` | Positive FIDE identifier, optional on a member. |
| `FfeId` | Identifier assigned by the Fédération Française des Échecs (FFE), represented as a non-blank string. |
| `FfeLicenseType` | FFE license category: A or B. |
| `FfeLicense` | Immutable association of an FFE identifier with a license category. |
| `EloRating` | Non-negative rating value that can be assigned and updated. |

`Member` equality is based on `MemberId`: names do not establish identity.
Affiliation is currently represented inside `Member` as a map from `Season` to
`ClubId`; there is no separate affiliation entity or `Club` aggregate yet.

## Seasonal affiliation rules

- An affiliation requires both a club and a season; null arguments are rejected.
- A member can be affiliated to only one club for a given season.
- Repeating an affiliation to the same club for the same season has no effect.
- Attempting to affiliate to another club for that season is rejected, preserving
  the original affiliation.
- A member may join another club in a subsequent season without losing previous
  affiliations.
- Looking up a season without an affiliation returns `Optional.empty()`.

For example, a member affiliated to club A in 2026–2027 can join club B in
2027–2028. Both affiliations remain available. Joining club B in 2026–2027 would
be rejected.

## FFE identification and licenses

Every member must be constructed with an FFE license A or B and its FFE identifier.
Construction without a license is rejected. FFE registration is
independent of the member's internal identity and optional FIDE identifier.

`Member.registerFfeLicense(ffeId, licenseType)` records the identifier and category
together. Both arguments are required. `Member.ffeId()` and
`Member.ffeLicenseType()` expose the values supplied at construction or subsequently recorded.

- A and B are the only modeled license categories.
- An FFE identifier cannot be null, empty or whitespace-only. Its value is preserved
  exactly; no federation-specific pattern or normalization is assumed yet.
- Invalid registration requests leave the member's existing registration unchanged.
- `FfeLicense` keeps the identifier and category together as one valid immutable value.

The current operation records the supplied valid pair, replacing any previous pair.
Restrictions on changing an existing FFE identifier remain to be specified.
License seasons, expiry, renewal, category changes and federation lookup are not
modeled by this first slice. The example identifiers in tests are synthetic, not
evidence of an official FFE identifier format.

## Other implemented rules and current limits

- A member requires an internal identity and may initially have no FIDE identifier.
- Once assigned, a FIDE identifier cannot be assigned again.
- Negative Elo ratings and non-positive FIDE identifiers are rejected.
- The relationship between a season's beginning and ending years is not yet validated.
- Names and the full lifecycle of ratings are not yet constrained by business rules.

## Implementation

The repository currently contains a Java domain model and JUnit tests. There are
no application services, persistence adapters, REST endpoints or frontend yet.
The domain has no framework or persistence dependencies.

- `src/main/java/domain/member`: member entity, identity, FIDE and FFE identifiers, FFE license and rating.
- `src/main/java/domain/club/vo`: club identity and season.
- `src/main/java/domain/exceptions`: domain exception for an already assigned FIDE identifier.
- `src/test/java/member`: member identity, FIDE, FFE registration and rating tests.
- `src/test/java/affiliation`: seasonal affiliation and season tests.

## Acceptance specifications and Cucumber

Cucumber 7.22.1 runs on the JUnit Platform alongside JUnit Jupiter 5.10.2.
All Cucumber dependencies are test-scoped; the domain remains independent of them.
The setup follows the [Cucumber JUnit Platform integration](https://cucumber.io/docs/installation/java/).

- `src/test/resources/features/implemented`: specifications of rules already covered
  by domain tests; this directory does not imply that their Cucumber steps exist.
- `src/test/resources/features/pending`: confirmed behaviors to build together.
- `src/test/java/acceptance/RunCucumberTests.java`: selects scenarios tagged `@acceptance`.
- `src/test/java/acceptance/steps`: Java bindings for the selected scenarios.

Only "Reject construction of a member without a license" currently has the
`@acceptance` tag and step definitions. Other scenarios remain specifications,
so undefined-step editor warnings on those scenarios are expected.

The acceptance scenario now passes because the real `Member` constructor rejects
a missing FFE license. The ATDD outer loop has reached GREEN, alongside 35 domain
tests. No application registration use case exists yet: this first scenario
exercises the domain construction boundary directly.

The inner loop verifies rejection of a missing license and successful construction
with either category A or B. Test fixtures explicitly provide synthetic valid licenses.
Add `@acceptance` to further scenarios when connecting them to real steps and
assertions; do not add empty steps merely to remove editor warnings.

## Run the tests

The Maven project targets Java 17 and uses JUnit Jupiter 5.10.2. With a compatible
JDK and Maven installed (no Maven wrapper is currently included):

```sh
mvn test
```

To run only the affiliation tests:

```sh
mvn -Dtest=affiliation.AffiliationsTests test
```

To run the selected Cucumber scenarios:

```sh
mvn -Dtest=RunCucumberTests test
```

The full `mvn test` command also runs this acceptance suite and therefore currently
reports 36 passing tests. Cucumber writes its HTML report to
`target/cucumber/cucumber.html`.

In IntelliJ, reload the Maven project after changing `pom.xml`, then run
`RunCucumberTests` to use the same scenario selection as Maven. Editor recognition
of Java step definitions requires Cucumber for Java support in addition to Gherkin.

Use RED → GREEN → REFACTOR for new behavior. Rename and simplify existing code
under green tests, keeping domain language consistent and changes small.

## License and authorship

MIT License. Created by Sébastien Macé.
Human contributors remain responsible for reviewing and understanding their contributions.
