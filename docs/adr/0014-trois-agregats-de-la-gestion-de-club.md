# 14. Trois agrégats pour la gestion de club

Date : 2026-09-23 (consignée le 2026-10-05)

## Statut

Acceptée

## Contexte

Le premier modèle avait une entité `Member` qui mélangeait l'identité d'une personne, ses
licences et ses affiliations, et un `ProspectRelationship` spécialisé trop tôt. Une même personne
peut pourtant être prospect de plusieurs clubs, membre de l'un et partenaire d'un autre. Le
rapport `docs/club-relationship-refactoring.md` détaille cette correction.

## Décision

Nous séparons trois racines d'agrégat :

- `Person` : l'identité (`PersonId`), les noms, l'email, l'identification FIDE et le classement ;
- `Club` : l'identité (`ClubId`), le nom, et le fait que l'application le gère ou non ;
- `ClubRelationship` : une relation identifiée par la paire `PersonId` + `ClubId`, son statut
  (`PROSPECT`, `MEMBER`, `PARTNER`) et ses licences par saison.

`ClubRelationship` ne contient que des identifiants. Ni `Person` ni `Club` ne porte de collection
de relations. Les règles qui portent sur plusieurs clubs (un seul club par saison, éligibilité
d'un prospect) vivent dans `ClubAffiliations`. Les agrégats sont immuables et sans setter.

## Conséquences

- `PROSPECT`, `MEMBER` et `PARTNER` décrivent une relation, pas un type de personne.
- Les cas d'usage chargent et enregistrent plusieurs agrégats ; la frontière de transaction qui
  les enregistrera ensemble reste une question ouverte.
- La saison en cours est fournie explicitement aux cas d'usage ; ses bornes ne sont pas calculées.

## Options écartées

- **Une entité `Member` unique** : le modèle d'origine, qui mélangeait trois cycles de vie.
- **Des collections de relations sur `Person` ou `Club`** : agrégats plus gros et couplés sans
  invariant qui l'exige.
