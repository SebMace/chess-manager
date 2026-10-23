# 23. Relire un agrégat en rejouant ses opérations

Date : 2026-10-23

## Statut

Acceptée

## Contexte

Les agrégats sont immuables, sans setter, et leur état ne change que par des opérations
métier (voir [0014](0014-trois-agregats-de-la-gestion-de-club.md)). Pour relire une
`ClubRelationship` depuis PostgreSQL, il faudrait sinon un constructeur qui accepte n'importe
quel état, taillé pour la base et qui contourne les invariants.

## Décision

`JdbcClubRelationshipRepository` reconstitue une relation en rejouant ses licences enregistrées
puis son statut par les opérations du domaine (`registerLicense`, `registerPartnership`), à
partir d'une relation vierge. Le domaine n'a aucun constructeur destiné à la persistance.

## Conséquences

- Un état relu respecte les mêmes invariants qu'un état créé par un cas d'usage.
- L'adaptateur dépend de l'ordre et de la sémantique des opérations : si une règle change, la
  relecture d'anciennes données peut échouer, ce que les tests de contrat du repository révèlent.
- Ce n'est pas de l'Event Sourcing : on stocke l'état, pas une suite d'événements.

## Options écartées

- **Un constructeur de reconstitution public** : laisserait créer un agrégat invalide.
