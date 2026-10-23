# 24. Un port de lecture pour une liste qui joint plusieurs agrégats

Date : 2026-10-23

## Statut

Acceptée

## Contexte

La liste des membres d'un club joint chaque personne et sa relation au club pour une saison.
Aucun agrégat ne porte cette liste : `Club` n'a pas de collection de relations, `Person` non
plus. La construire en chargeant les agrégats un à un multiplierait les requêtes.

## Décision

Nous définissons un port en lecture seule, `ClubMembers`, qui renvoie des `ClubMember` (une
vue, pas un agrégat). `JdbcClubMembers` l'implémente par une requête dédiée. Le cas d'usage
`MembersOfClub` le lit pour la saison courante.

## Conséquences

- Les lectures qui joignent plusieurs agrégats passent par un port de lecture, sans modifier les
  agrégats ni leurs repositories.
- C'est une séparation commande/requête au niveau d'un cas d'usage, pas un modèle de lecture
  séparé : les deux lisent les mêmes tables.

## Options écartées

- **Charger les agrégats puis les joindre en mémoire** : requêtes en N+1 sans bénéfice.
- **Une collection de membres dans `Club`** : un agrégat plus gros, sans invariant qui l'exige.
