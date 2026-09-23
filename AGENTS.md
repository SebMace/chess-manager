# AGENTS.md — Chess Manager

## 1. Purpose and scope

This file defines how a coding agent must collaborate on the entire Chess Manager repository.

Its goals are to:

- build a correct, maintainable chess application;
- use the project as a deliberate practice ground for software design;
- preserve strict TDD, DDD, Clean Architecture, and Hexagonal Architecture;
- help the developer understand every important design decision rather than delegating blindly;
- favor small, reversible, evidence-backed changes over large speculative solutions.

These instructions apply to the whole repository unless a more specific `AGENTS.md` or
`AGENTS.override.md` exists in a nested directory. A nested file may specialize these rules for
the backend, frontend, or another bounded context, but it must not silently weaken the core
discipline defined here.

Do not modify this file unless the user explicitly asks to evolve the development playbook.

## 2. Order of work

Before changing code:

1. Read all applicable instruction files.
2. Identify the Git root and current working directory.
3. Inspect `git status` and preserve every pre-existing user change.
4. Inspect the relevant production code, tests, build files, and documentation.
5. Discover the actual commands and technology versions from the repository; never invent them.
6. Distinguish established project facts from hypotheses and recommendations.
7. State the smallest observable behavior that should change.
8. Select the smallest test capable of demonstrating that behavior.

Do not begin with a repository-wide redesign. Understand the current slice first.

## 3. Collaboration contract

### 3.1 Language and teaching style

- Communicate with the user in French unless asked otherwise.
- Keep code identifiers, package names, APIs, and standard technical terms in the language
  already established by the codebase, preferably English.
- Explain unfamiliar Java, Spring, Angular, testing, DDD, or architectural mechanisms in plain
  language.
- Explain why a change is proposed, not merely what syntax was produced.
- When several solutions are genuinely viable, compare their concrete trade-offs and recommend
  one.
- Challenge an unsafe, inconsistent, over-engineered, or domain-poor proposal respectfully and
  with evidence.
- Never hide uncertainty. Label facts, assumptions, hypotheses, and decisions explicitly when
  the distinction matters.
- Do not overwhelm the user with theory unrelated to the current small step.

The goal is not only to deliver code. The goal is to make the reasoning inspectable and to help
the developer become more autonomous.

### 3.2 Preserve user control

Ask before making a decision that would materially affect any of the following:

- public API or user-visible behavior outside the requested scope;
- aggregate boundaries or bounded-context boundaries;
- database schema or irreversible data migration;
- production dependencies, frameworks, or major version upgrades;
- authentication, authorization, secrets, or security policy;
- messaging infrastructure, Kafka topology, Event Sourcing, or distributed architecture;
- deletion or broad movement of source files;
- a large refactoring spanning unrelated behavior;
- Git history, commits, branches, rebases, pushes, or pull requests.

Do not ask for confirmation for every safe local edit once the behavior and design direction are
agreed. Work autonomously inside the approved small scope, then report evidence.

Do not delegate to subagents or parallel agents unless the user explicitly requests it. The user
must be able to follow the reasoning and the sequence of changes.

### 3.3 Interpret the user's intent correctly

- **Explain, review, assess, or plan:** remain read-only. Do not edit files.
- **Diagnose:** establish the cause and evidence. Do not implement a fix unless asked.
- **Guide me, next step, or coach me in TDD:** perform at most the requested TDD phase, explain
  it, and stop at the agreed checkpoint.
- **Implement, change, or fix:** implement the smallest complete behavior using one or more visible
  TDD cycles, verify it, and report the result.
- **Refactor:** preserve observable behavior and begin from green tests.

If the request is ambiguous and different interpretations would create materially different
behavior or architecture, ask one focused question. Otherwise, choose the smallest reversible
interpretation and state it.

## 4. Core engineering values

Use these values as decision filters, in this order:

1. Correct domain behavior.
2. Evidence from executable tests.
3. Clarity of the model and ubiquitous language.
4. Simplicity of the current solution.
5. Clean dependency direction.
6. Ease of future change demonstrated by a real requirement.
7. Performance supported by measurement.

Apply YAGNI and KISS. Do not build extension points, abstractions, infrastructure, or configurability
for imagined future needs.

SOLID principles are diagnostic tools, not a checklist that justifies more classes:

- use SRP to separate distinct reasons to change;
- use OCP only when an actual axis of variation has appeared;
- preserve substitutability when implementations share a contract;
- keep ports focused on what their clients need;
- orient dependencies toward stable domain and application policies.

## 5. Strict TDD: RED → GREEN → REFACTOR

Behavior-changing production code must normally be driven by a failing automated test.

Exceptions are limited to changes that do not alter behavior, such as documentation, comments,
formatting, build diagnosis, or a pure refactoring already protected by adequate tests. If a safe
test-first path is not available, explain why before proceeding.

### 5.1 RED

1. Select one behavior, rule, example, or regression.
2. Write the smallest test that expresses it through an observable contract.
3. Run the narrowest relevant test command.
4. Confirm that the test fails for the expected reason.
5. If it passes immediately, determine whether the behavior already exists or the test is weak.
6. Do not change production code during RED.

A compilation failure may count as RED when introducing a genuinely missing type or API, but move
quickly to a behavioral failure. Do not leave the test suite in an unexplained broken state.

### 5.2 GREEN

1. Write the minimum production code necessary to satisfy the new test.
2. Avoid speculative validation, abstraction, patterns, and generalization.
3. Run the targeted test.
4. Run the closest relevant test group to detect regressions.
5. Do not clean up unrelated code while seeking GREEN.

“Minimum” means the simplest behaviorally correct implementation, not intentionally poor or
misleading code.

### 5.3 REFACTOR

1. Refactor only while the tests are green.
2. Name the concrete design pressure: duplication, primitive obsession, misplaced responsibility,
   excessive coupling, unclear language, conditional complexity, or another observed smell.
3. Make one structural change at a time.
4. Run the relevant tests after each meaningful refactoring step.
5. Do not add new behavior during REFACTOR.

If no concrete design pressure exists, explicitly choose not to refactor.

### 5.4 TDD interaction checkpoints

When the user asks for step-by-step coaching:

- propose the next test and explain why it is the next smallest behavior;
- wait for agreement when the choice teaches or fixes an important design direction;
- after RED, report the exact failure and stop if RED was the requested checkpoint;
- after GREEN, show why the implementation is minimal;
- before a non-obvious refactoring, explain the smell and expected benefit.

When the user asks for implementation rather than coaching, complete a small full cycle without
artificial pauses, but keep RED, GREEN, and REFACTOR distinguishable in the report.

Never claim that RED or GREEN occurred without having run the corresponding command.

## 6. Acceptance testing and Outside-In development

Prefer Outside-In discovery when implementing a user-visible use case or an interaction across
layers:

1. Express the desired outcome at the most stable observable boundary.
2. Let that test reveal the next missing collaborator or domain capability.
3. Use focused unit-level RED → GREEN → REFACTOR cycles internally.
4. Return to the outer test until it becomes green.

Use Given–When–Then vocabulary for business examples, whether or not a BDD framework is present.
Do not introduce Cucumber or another test framework merely to obtain BDD syntax.

Do not force an acceptance test for a purely internal refactoring or a tiny isolated domain rule
when a focused unit test communicates the behavior more clearly.

## 7. Transformation Priority Premise (TPP)

Use TPP as a guide for choosing the next test and the smallest implementation transformation,
not as a rigid algorithm.

Prefer low-complexity transformations before high-complexity ones when they can honestly satisfy
the current example. Typical progression includes:

- no behavior → explicit constant;
- one example → a value derived from input;
- unconditional behavior → one necessary condition;
- one item → several items;
- duplicated cases → a data structure or abstraction;
- direct expression → a named operation;
- simple linear behavior → iteration or recursion only when demanded.

Use the next test to prevent an implementation from remaining falsely general or falsely specific.
Do not distort the domain API merely to obey a transformation order. Domain clarity and observable
behavior remain primary.

When proposing a next test, briefly explain:

- which behavior it introduces;
- which naive implementation it invalidates;
- which small transformation it is expected to provoke.

## 8. Test design rules

### 8.1 Test observable behavior

- Test domain rules, use-case outcomes, state transitions, returned values, emitted domain events,
  persisted effects, and user-visible interactions.
- Do not test private methods or duplicate the production algorithm in the test.
- Avoid assertions tied to incidental call order or internal structure unless that collaboration is
  itself the contract.
- Give tests names that express business behavior and examples, not method names alone.
- Keep each test focused enough that its failure tells a useful story.
- Use deterministic inputs. Control time, randomness, external processes, and network boundaries.
- A bug fix requires a regression test that fails for the reproduced bug before the fix.

### 8.2 Test doubles

- Prefer real domain objects in domain tests.
- Prefer fakes for stable in-memory substitutes such as repositories when they improve readability.
- Use stubs for controlled answers from collaborators.
- Use spies or mocks only when an observable collaboration must be verified.
- Do not mock value objects, entities, or simple pure logic.
- Do not mirror every implementation class with a mock-heavy test.
- Never mock what can be tested more clearly and cheaply with a real object.

### 8.3 Test levels

Maintain complementary levels of confidence:

- many fast domain unit tests for invariants and rules;
- focused application tests for use-case orchestration;
- integration or contract tests for database mappings and external adapters;
- frontend unit/component tests for state and user interactions;
- a small number of end-to-end tests for critical journeys.

For existing untested behavior, first consider a characterization test or a narrow golden master.
Do not freeze accidental behavior more broadly than necessary.

## 9. Domain-Driven Design

### 9.1 Ubiquitous language

Model chess concepts, not database tables or framework mechanisms.

Use one canonical term for each concept. Current domain vocabulary includes, subject to validation
against the repository and use cases:

- Game / Partie;
- Player / Joueur;
- Club;
- Tournament / Tournoi;
- Round / Ronde;
- Move / Coup;
- Position;
- Color / Couleur;
- GameResult / Résultat;
- Rating or Elo;
- PlayerId, GameId, TournamentId;
- FideId and FfeId;
- FEN and PGN.

Code identifiers should remain consistent. Do not alternate between synonyms such as `Game` and
`Match`, or `Player` and `User`, without an explicit domain distinction.

When a term is ambiguous, ask what it means in the business context and reflect the answer in code,
tests, and documentation.

### 9.2 Entities and value objects

- Give entities durable typed identities.
- Compare entities by identity according to the established lifecycle semantics.
- Model descriptive concepts as immutable value objects when value equality and validation matter.
- Value objects must be valid at construction and should expose behavior rather than raw mutable
  state.
- Distinguish absence from invalidity; never use magic values such as `0`, `-1`, or an empty string
  for a missing identifier.
- Use typed identifiers rather than passing unrelated `String` or `long` values interchangeably.
- Use a Java `record` for transparent immutable value data when its generated semantics match the
  domain. Use a class when construction rules, encapsulation, identity, or evolving behavior demand
  it. Do not choose a record only to reduce typing.

Working modeling hypotheses already explored in this project include:

- `Game` and `Player` have durable identities;
- `Move`, `Position`, `FideId`, color, and result are value-object candidates;
- an absent FIDE or FFE identifier is different from an invalid one.

Treat these as informed starting points. Preserve existing verified decisions, but let examples and
invariants refine the model rather than turning earlier sketches into dogma.

### 9.3 Aggregates and invariants

- Define an aggregate around rules that must remain consistent in one transaction.
- Modify aggregate state only through the aggregate root.
- Keep aggregates as small as consistency permits.
- Reference other aggregates by identity when object graphs would create unnecessary coupling.
- Put invariants in the domain model, not only in controllers, forms, or database constraints.
- Keep constructors or factories from creating invalid aggregates.
- Do not create an aggregate, repository, or factory solely because a DDD catalog contains one.

`Game` is a strong aggregate-root candidate when it must validate a move, update the position,
record move history, determine status, or produce a result atomically. An operation such as
`game.play(move)` is preferable to external procedural mutation when those rules belong to the game.
Confirm the actual invariant before cementing the API.

### 9.4 Domain services and application services

- Put behavior on an entity or value object when it naturally belongs there.
- Introduce a domain service only for a genuine domain operation that does not belong to one
  entity or value object.
- Use application services to orchestrate a use case, load aggregates, call domain behavior,
  persist results, manage the transaction boundary, and coordinate ports.
- Application services must not become containers for domain rules.
- Controllers, components, repositories, and message consumers must not make domain decisions.

### 9.5 Domain events

- Name domain events as completed business facts in the past tense, for example `MovePlayed`.
- Emit an event only after the aggregate has reached a valid state.
- Keep domain events free of Spring, Kafka, HTTP, persistence, and serialization concerns.
- Do not emit events for every field change.
- Domain Events do not imply Event Sourcing.
- Kafka delivery does not define the domain model; a broker is an outbound adapter.
- Use an outbox only when a proven use case requires reliable atomic publication with persistence.
- Introduce event versioning and idempotency when events cross a durable external boundary.

### 9.6 CQRS and Event Sourcing

- Respect Command–Query Separation at the method and use-case level when it clarifies intent.
- Introduce separate read and write models only when their needs genuinely diverge.
- Do not introduce CQRS merely to separate folders.
- Do not introduce Event Sourcing without explicit user approval and a demonstrated need for
  temporal reconstruction or auditability.
- Before Event Sourcing, discuss event evolution, replay, snapshots, migrations, concurrency,
  storage, and operational cost.

## 10. Chess Manager domain direction

Chess Manager is intended to grow around capabilities such as:

- recording, importing, replaying, and analyzing games;
- managing players, identities, ratings, and clubs;
- managing tournaments, rounds, pairings, and results;
- importing PGN and handling FEN positions;
- integrating with Lichess and chess.com;
- relaying live games;
- using Stockfish through an adapter;
- notifying interested users;
- supporting training in tactics, strategy, endgames, openings, and play from a position.

These are roadmap capabilities, not proof that they already exist and not permission to implement
them without a requested use case.

Potential domain areas include Games, Players & Clubs, Tournaments, Training, and Notifications.
Begin with a modular monolith and explicit internal module boundaries. Do not automatically turn
every area into a bounded context or microservice. Extract a distinct bounded context only when
language, invariants, lifecycle, data ownership, or change cadence genuinely diverge.

For chess rules and formats:

- never guess a rule; capture it with concrete examples and tests;
- distinguish notation parsing, position representation, move legality, game history, and analysis;
- keep PGN/FEN parsing errors explicit and useful;
- remember that a FEN position and a complete game history are not interchangeable concepts;
- do not identify a player solely by a mutable display name;
- isolate engine-specific and provider-specific concepts from the core domain.

## 11. Clean and Hexagonal Architecture

Treat architecture as a dependency rule, not merely a folder layout.

### 11.1 Dependency direction

- The domain depends on no framework, database, transport, UI, or external chess provider.
- The application layer depends on the domain and defines use cases and required ports.
- Inbound adapters call application use cases.
- Outbound adapters implement ports required by the application or domain policy.
- Infrastructure composes the application and provides framework configuration.
- Dependencies point toward domain and application policies, never outward from them.

Typical conceptual areas are:

- `domain`: entities, value objects, aggregates, domain services, domain events, invariants;
- `application`: use cases, commands, queries, orchestration, ports, transaction intent;
- `adapters.in`: REST, CLI, message consumers, scheduled triggers, UI-facing entry points;
- `adapters.out`: persistence, Lichess/chess.com, Stockfish, Kafka, clock, filesystem;
- `infrastructure`: Spring wiring, configuration, database and runtime details.

Preserve the repository's current organization when it already expresses these dependency rules.
Do not perform a wholesale package migration merely to match these names. Improve boundaries
incrementally under test.

### 11.2 Boundary rules

- Do not expose persistence entities or external DTOs as domain objects.
- Map at adapter boundaries.
- Keep transport validation and parsing at the edge; keep business invariants in the domain.
- Define ports from the needs of the core, not from the API of a vendor or library.
- Keep external provider models behind an anti-corruption layer.
- Avoid circular module and package dependencies.
- Do not leak Spring annotations, Angular types, JSON annotations, ORM annotations, or Kafka types
  into the domain.

## 12. Backend Java and Spring

- Inspect the configured Java and Spring versions before using language or framework features.
- Do not upgrade Java, Spring, Maven plugins, or dependencies unless explicitly requested.
- Prefer expressive immutable domain objects and explicit behavior.
- Favor constructor injection. Do not use field injection.
- Omit `@Autowired` on an unambiguous constructor.
- Instantiate domain objects with constructors or domain factories; do not ask Spring to manage
  entities or value objects.
- Use Spring beans for application orchestration, adapters, and composition where dependency
  injection is useful.
- Use stereotypes according to architectural meaning, not on every class.
- Keep framework configuration explicit when it clarifies infrastructure assembly.
- Avoid generic `Utils`, `Manager`, or anemic `Service` classes. Name types after domain concepts
  or use cases.
- Prefer explicit exceptions or result types that preserve domain meaning. Do not swallow errors.
- Avoid returning `null` when the established API can represent absence safely; follow the existing
  project convention consistently.
- Keep time and randomness injectable at boundaries when behavior depends on them.
- Comments explain non-obvious reasons, constraints, or trade-offs; they do not narrate obvious
  code.

Use the repository's Maven wrapper when present. Do not assume Maven, Gradle, module names, or test
commands before inspecting the project.

## 13. Persistence

- Prefer explicit, simple SQL/JDBC-style persistence when it fits the use case and existing stack.
- Do not introduce JPA or Hibernate without explicit approval and a concrete benefit that outweighs
  hidden queries, mapping complexity, and domain coupling.
- Keep repository interfaces expressed in domain or application language.
- Keep SQL, row mapping, ORM entities, and database-specific details in outbound adapters.
- Test non-trivial mappings and queries against a suitable integration boundary.
- Do not rely on an in-memory database as proof of production-dialect behavior when dialect details
  matter.
- Make transaction boundaries align with an application use case and aggregate consistency.
- Avoid N+1 queries and unbounded loading, but optimize only with evidence and realistic data shape.
- Any schema change requires an explicit migration, backward-compatibility consideration, and user
  approval.

## 14. External integrations

For Lichess, chess.com, Stockfish, Kafka, email, or any future provider:

- define a core-owned port;
- implement provider details in an outbound adapter;
- map external DTOs into internal types explicitly;
- handle timeouts, failures, retries, rate limits, and malformed data deliberately;
- make imports idempotent when the same external game can be received twice;
- never call a real remote service from a domain unit test;
- do not log tokens, credentials, private data, or full sensitive payloads;
- do not add an SDK when a small explicit client would be clearer without first comparing costs.

Do not add Kafka because events exist. Add messaging only when an asynchronous use case and its
delivery semantics have been agreed.

## 15. Angular frontend

- Inspect the Angular version, build system, project conventions, and test runner before editing.
- Do not migrate Angular or replace the test stack unless explicitly requested.
- Explain Angular-specific concepts because frontend work is also a learning objective.
- Build from user behavior and component responsibilities, not from visual markup alone.
- Keep components focused; move domain-independent orchestration and external calls into appropriate
  services or adapters.
- Keep chess domain rules out of components.
- Prefer explicit typed models and avoid pervasive `any`.
- Keep state ownership clear and derive values instead of duplicating mutable state.
- Follow the reactive style already selected by the project; do not mix paradigms casually.
- Test user-visible behavior, inputs, outputs, state transitions, validation, and accessibility.
- Avoid tests coupled to private fields, internal method calls, or fragile DOM structure.
- Use semantic HTML and keyboard-accessible interactions. Add ARIA only where native semantics are
  insufficient.
- Keep HTTP DTO mapping at the boundary so the UI does not silently redefine the domain.
- For a bug, reproduce the user interaction in a failing test before fixing it.

For editor, board, replay, or training features, keep coordinate conversion, notation, presentation,
and domain legality as distinct responsibilities.

## 16. Emergent design and patterns

Let design emerge from examples and refactoring pressure.

Do not introduce a Gang of Four pattern because it appears academically appropriate. Introduce a
pattern only when:

1. a concrete variation or coupling problem exists;
2. tests protect the current behavior;
3. the pattern makes the present code simpler or safer to change;
4. its cost and rejected simpler alternative can be explained.

Possible patterns must remain responses to pressure, for example:

- Strategy for an established family of varying rules;
- State for meaningful lifecycle-dependent behavior that has outgrown simple conditions;
- Factory for genuinely complex valid construction;
- Adapter for external chess providers or engines;
- Observer or Domain Events for proven decoupling needs;
- Specification for business predicates that truly need composition.

Do not add interfaces with one implementation merely “for testability.” Add a port when crossing an
architectural boundary or when a real substitution need exists.

Prefer duplication over the wrong abstraction during early discovery. Remove duplication when its
shared meaning has become clear, not merely because lines look similar.

## 17. Change discipline

- Keep the diff limited to the requested behavior.
- Preserve existing formatting and conventions unless changing them is part of the task.
- Do not rewrite adjacent code opportunistically.
- Do not add production dependencies without approval.
- Do not update lockfiles, generated sources, snapshots, or dependency versions incidentally.
- Do not suppress warnings or disable tests to obtain green.
- Do not weaken an assertion merely to make a test pass.
- Do not catch and ignore exceptions.
- Do not leave dead code, commented-out implementations, debug output, or unexplained TODOs.
- Do not create abstractions or modules for speculative roadmap features.
- Do not expose secrets or place credentials in source, test fixtures, logs, or documentation.

When encountering unrelated failing tests or defects, report them separately. Do not silently expand
the task to fix them.

## 18. Git safety

- Inspect `git status` before editing and again before reporting completion.
- Treat existing modified and untracked files as user work.
- Never discard, overwrite, stash, or reformat unrelated user changes.
- Never run destructive Git commands such as `reset --hard` or forced checkout.
- Do not commit, amend, rebase, merge, push, force-push, create a pull request, or change branches
  unless explicitly requested.
- Do not include unrelated files in a proposed commit.
- Prefer small, reviewable diffs corresponding to one behavior.
- If a requested change overlaps unresolved user edits, stop and explain the conflict.

## 19. Verification

Discover commands from wrapper files, `pom.xml`, `build.gradle`, `package.json`, workspace files,
CI configuration, and the README. Prefer repository wrappers and declared scripts.

For each change:

1. Run the targeted test during RED and record the expected failure.
2. Run it again during GREEN.
3. Run the nearest relevant suite after refactoring.
4. Run broader tests, static analysis, formatting, and build checks when proportionate to the scope.
5. Report the exact commands and outcomes.

Never state that tests pass if they were not run. If a command cannot run, state the precise blocker,
what remains unverified, and the safest next action.

Do not “fix” an environment failure by changing production behavior unless the evidence proves that
production code is the cause.

## 20. Definition of done

A change is complete only when:

- the requested observable behavior is implemented;
- a test demonstrated RED for the expected reason when behavior changed;
- the implementation reached GREEN;
- relevant regression tests pass;
- any refactoring occurred only under green tests;
- domain language and invariants remain explicit;
- dependency direction is preserved;
- no unrelated files or behavior changed;
- documentation is updated when a public contract or workflow changed;
- remaining risks, assumptions, and unverified checks are disclosed.

## 21. Completion report

End implementation work with a concise report containing:

1. **Outcome:** what behavior now exists or what defect was fixed.
2. **TDD evidence:** the RED failure, GREEN result, and relevant test commands.
3. **Design:** important domain or architectural choice and why it was the smallest suitable one.
4. **Files changed:** only the relevant files.
5. **Verification:** tests and checks actually run.
6. **Remaining point:** one risk, open question, or recommended next test, only if one genuinely
   remains.

For a read-only review, lead with findings ordered by severity and cite the affected files and lines.
For a teaching session, finish by asking the user to explain or choose the next small step rather
than taking over the whole design.

## 22. Default first response to a new task

For a non-trivial request, begin with a short orientation that states:

- the behavior or problem understood;
- the relevant area of the repository;
- the first test or diagnostic action;
- any material assumption requiring confirmation.

Then proceed within the intent authorized by the user. Keep the working conversation concise and
evidence-based.
