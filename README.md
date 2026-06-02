# chess-manager

> Open-source SaaS platform for chess games, clubs, players, tournaments and teams.  
> Licensed under MIT.

## Vision

ChessManager aims to become a complete platform for the chess ecosystem:

- manage chess clubs
- manage members and players
- manage teams and championships
- manage tournaments
- store and analyze chess games
- publish club websites
- send notifications
- support training and progression
- provide a SaaS-ready architecture

The ambition is to build ChessManager progressively, with a strong emphasis on:

- Domain Driven Design
- Software Craftsmanship
- Test Driven Development
- Clean (Hexagonal) Architecture
- Open Source
- long-term maintainability

---

## Main Bounded Contexts

### Club Management Context

Manages clubs, members, memberships, roles, volunteers and club administration.

Possible concepts:

- Club
- Member
- Membership
- Role
- Season

---


### Game Management Context

Dedicated to chess games stored in the database.

This context is responsible for registering, importing, storing and retrieving chess games.

Possible concepts:

- Game
- Move
- Position
- Result
- PGN
- Opening
- TimeControl
- PlayerColor

Possible features:

- Add a game manually
- Import a PGN file
- Store games in database
- Search games
- Link games to players, tournaments or teams
- Analyze game metadata
- Build a large chess game database

---

### Tournament Context

Manages chess tournaments.

Possible concepts:

- Tournament
- Round
- Pairing
- Standing
- TournamentPlayer
- TournamentResult

Possible features:

- Create a tournament
- Register players
- Manage rounds
- Record results
- Generate standings

---

### Team Context

Manages teams and team competitions.

Possible concepts:

- Team
- TeamMatch
- Board
- Lineup
- Championship
- MatchSheet

---

### Training Context

Manages learning and progression.

Possible concepts:

- TrainingSession
- Exercise
- Puzzle
- OpeningRepertoire
- StudyPlan

---

### Website Context

Manages public club websites.

Possible concepts:

- Page
- Article
- News
- Event
- ClubWebsite

Future direction:

- static website generation
- later CMS integration
- internationalized content

---

### Notification Context

Manages communication and notifications.

Possible concepts:

- Notification
- NotificationTemplate
- Recipient
- Channel
- DeliveryStatus

A first notification module may be implemented in TypeScript.

Possible features:

- send club announcements
- notify players about tournaments
- notify team members about matches
- send reminders
- manage email or application notifications

---

### Identity and Access Context

Manages users, authentication and permissions.

Possible concepts:

- User
- Account
- Role
- Permission
- OrganizationAccess

---

## DDD Roadmap

The project will start by clarifying the domain before rushing into implementation.

### Step 1 — Strategic Design

Define:

- domains
- subdomains
- core domain
- supporting subdomains
- generic subdomains
- bounded contexts
- context map
- relationships between contexts

The goal is to understand where the real business complexity lives.

---

### Step 2 — Ubiquitous Language

For each bounded context, define a precise ubiquitous language.

Examples:

- What is a player?
- What is a member?
- What is a game?
- What is a tournament?
- What is a team match?
- What is a PGN?
- What is a notification?

The same word may have different meanings in different contexts.

---

### Step 3 — Tactical Design

Use DDD tactical patterns where they make sense:

- Entities
- Value Objects
- Aggregates
- Repositories
- Domain Services
- Domain Events
- Factories
- Specifications

The goal is not to apply patterns mechanically, but to let the model emerge from the domain.

---

### Step 4 — Acceptance Tests

Some bounded contexts will be described using:

- Gherkin
- Cucumber
- executable specifications

This is especially relevant for business workflows such as:

- registering a player
- creating a tournament
- importing a PGN game
- creating a team match sheet
- sending a notification

---

### Step 5 — Progressive Implementation

Implementation details will be decided progressively.

For now, the priority is:

- clarify the model
- define bounded contexts
- define subdomains
- create the context map
- build the ubiquitous language
- identify the first core use cases

---

## Suggested First Use Cases

Possible first slices:

1. Register a chess club
2. Register a member
3. Register a player
4. Store a chess game in PGN format
5. Search stored games
6. Create a tournament
7. Send a notification

A good first vertical slice could be:

> As a club manager, I want to store a chess game in the database so that I can build a searchable game archive for my club.

---

## Long-Term Vision

ChessManager aims to become:

- a chess club management platform
- a team competition management platform
- a tournament platform
- a chess game database
- a PGN management tool
- a training platform
- a club website generator
- a notification platform
- a SaaS product for chess organizations

---
## Learning and Knowledge Sharing

ChessManager is more than a software project.

It is also an educational project whose purpose is to promote Software Craftsmanship practices and help developers improve their skills while building a real-world application.

The project embraces continuous learning and knowledge sharing through:

- Domain Driven Design (DDD)
- Test Driven Development (TDD)
- Acceptance Test Driven Development (ATDD)
- Clean Architecture
- Refactoring
- Software Craftsmanship
- Functional programming concepts where appropriate

Whenever possible, architectural decisions, design choices and development practices should be documented alongside the code.

The goal is not only to build software, but also to explain:

- why a solution was chosen
- what alternatives were considered
- what trade-offs were made
- how the domain was modeled

Documentation is therefore considered a first-class citizen of the project.

---

## Build in Public

ChessManager is intended to be developed openly.

Part of the project's journey will be shared publicly through articles, videos, live coding sessions and technical discussions.

The objective is to make the design and development process visible, including:

- domain discovery
- ubiquitous language definition
- bounded context identification
- modeling sessions
- architecture discussions
- TDD sessions
- refactoring sessions

---

## Mob Programming Sessions

The project may include regular mob programming sessions.

These sessions aim to:

- learn collectively
- discuss design decisions
- share Software Craftsmanship practices
- explore DDD concepts
- improve code quality
- mentor less experienced developers

## Contributing

ChessManager welcomes human contributors.

AI tools may be used as assistants, but they are not considered contributors to the project. Every contribution must be reviewed, understood and assumed by a human developer.

As Julien Dubois, creator of JHipster, said in podcast "If This Then Dev"

> "You don't have a drink with an AI."

## License

MIT License

---

## Author

Created by Sébastien Macé.

Contributions are welcome! Please ensure your pull requests strictly follow the project's guidelines for high-quality code.

---

For more information or to contribute, feel free to reach out.
 


