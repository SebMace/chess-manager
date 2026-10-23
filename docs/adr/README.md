# Décisions d'architecture (ADR)

Ce dossier consigne les décisions d'architecture de Chess Manager, au format de Michael Nygard
(voir [0001](0001-enregistrer-les-decisions-d-architecture.md)). Un ADR dit *pourquoi* une
décision a été prise et ce qui a été écarté ; le README et `docs/slices.md` disent *comment* le
code l'applique.

Les ADR 0002 à 0020 ont été consignés rétroactivement le 2026-10-05, à partir du README, de
`docs/slices.md`, de `AGENTS.md` et de l'historique Git. Leur date est celle de la décision quand
elle est connue.

Pour une nouvelle décision : copier [`template.md`](template.md) sous le numéro suivant. Un ADR
accepté n'est plus réécrit : une décision qui change fait l'objet d'un nouvel ADR, et l'ancien
passe au statut « Remplacée par NNNN ».

| # | Décision | Statut |
|---|---|---|
| [0001](0001-enregistrer-les-decisions-d-architecture.md) | Enregistrer les décisions d'architecture | Acceptée |
| [0002](0002-monolithe-modulaire.md) | Un monolithe modulaire | Acceptée |
| [0003](0003-architecture-hexagonale.md) | Architecture hexagonale vérifiée par ArchUnit | Acceptée |
| [0004](0004-vertical-slices.md) | Organiser le code en vertical slices | Acceptée |
| [0005](0005-java-26-et-spring-boot-4.md) | Java 26 et Spring Boot 4.1.1 | Acceptée |
| [0006](0006-postgresql-et-jdbc-sans-jpa.md) | PostgreSQL et JDBC, sans JPA | Acceptée |
| [0007](0007-flyway-pour-le-schema.md) | Flyway pour le schéma de la base | Acceptée |
| [0008](0008-testcontainers-pour-la-persistance.md) | Testcontainers pour les tests de persistance | Acceptée |
| [0009](0009-front-end-angular-separe.md) | Un front-end Angular séparé, sans règle métier | Acceptée |
| [0010](0010-contrat-pact-front-back.md) | Un contrat consumer-driven avec Pact | Acceptée |
| [0011](0011-acceptation-cucumber-en-langage-metier.md) | Acceptation Cucumber en langage métier | Acceptée |
| [0012](0012-couverture-et-mutation-sans-seuil.md) | Couverture et mutation, sans seuil | Acceptée |
| [0013](0013-ci-github-actions.md) | Intégration continue avec GitHub Actions | Acceptée |
| [0014](0014-trois-agregats-de-la-gestion-de-club.md) | Trois agrégats pour la gestion de club | Acceptée |
| [0015](0015-identifiant-ffe-de-la-personne.md) | L'identifiant FFE appartient à la personne | Acceptée |
| [0016](0016-craft-calendar-contexte-generique.md) | Craft Calendar, un contexte générique | Acceptée |
| [0017](0017-codes-postaux-de-la-poste.md) | Les codes postaux viennent de La Poste | Acceptée |
| [0018](0018-pas-de-reutilisation-des-donnees-ffe.md) | Aucune donnée FFE réutilisée sans autorisation | Acceptée |
| [0019](0019-authentification-differee.md) | Authentification différée | Acceptée |
| [0020](0020-priorite-a-chess-decision.md) | Priorité à Chess Decision | Acceptée, pause levée par 0021 |
| [0021](0021-reprise-de-la-gestion-de-club.md) | Reprise de la gestion de club | Acceptée |
| [0022](0022-transaction-d-un-cas-d-usage-dans-infrastructure.md) | La transaction d'un cas d'usage est posée par infrastructure | Acceptée |
| [0023](0023-relire-un-agregat-par-ses-operations.md) | Relire un agrégat en rejouant ses opérations | Acceptée |
| [0024](0024-port-de-lecture-pour-une-liste.md) | Un port de lecture pour une liste qui joint plusieurs agrégats | Acceptée |
| [0025](0025-saison-courante-en-configuration.md) | La saison courante est une constante de configuration | Acceptée |
