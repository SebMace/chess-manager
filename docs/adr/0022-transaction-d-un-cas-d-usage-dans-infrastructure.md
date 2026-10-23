# 22. La transaction d'un cas d'usage est posée par infrastructure

Date : 2026-10-23

## Statut

Acceptée

## Contexte

*Register a new member* enregistre deux agrégats : la personne (`Person`) et sa relation au club
(`ClubRelationship`). Une personne enregistrée sans son adhésion violerait l'invariant « une
personne connue comme licenciée FFE est membre du club où elle a été inscrite ». Le cas d'usage
ne doit pas dépendre de Spring (voir [0003](0003-architecture-hexagonale.md)), et chaque
repository n'écrit que son propre agrégat : la transaction ne peut appartenir à aucun des deux
adaptateurs, contrairement à `JdbcClubCalendar` (voir
[0006](0006-postgresql-et-jdbc-sans-jpa.md)).

## Décision

Quand un cas d'usage doit enregistrer plusieurs agrégats ensemble, `infrastructure` l'enveloppe
dans une transaction : `TransactionalRegisterNewMember` étend `RegisterNewMember` et exécute son
opération par `TransactionOperations`. Le cas d'usage reste sans Spring. Un test force un échec
après le premier enregistrement et vérifie que rien n'est conservé.

## Conséquences

- On s'écarte délibérément de la règle « un agrégat par transaction » : la cohérence des deux
  agrégats est immédiate, pas éventuelle.
- Deux emplacements de transaction coexistent : l'adaptateur pour un seul agrégat écrit en
  plusieurs requêtes, `infrastructure` pour un cas d'usage qui écrit plusieurs agrégats.
- L'héritage oblige le cas d'usage à rester une classe non finale, aux méthodes redéfinissables.

## Options écartées

- **`@Transactional` sur le cas d'usage** : ferait entrer Spring dans le cœur.
- **Une transaction dans un repository** : aucun repository ne voit les deux agrégats.
