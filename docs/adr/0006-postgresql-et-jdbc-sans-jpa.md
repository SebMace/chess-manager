# 6. PostgreSQL et JDBC, sans JPA

Date : 2026-09-25 (consignée le 2026-10-05)

## Statut

Acceptée

## Contexte

Les clubs, puis leurs horaires d'ouverture, devaient être persistés. Les agrégats sont immuables
et sans setter ; leur forme ne doit pas être dictée par un outil de mapping.

## Décision

Nous persistons dans PostgreSQL 18, avec du SQL explicite écrit dans les adaptateurs sortants au
moyen du `JdbcClient` de Spring. Les repositories sont des ports exprimés dans le langage du
domaine. Une transaction qui doit garder un agrégat cohérent appartient à l'adaptateur quand le
cas d'usage ne peut pas dépendre de Spring (par exemple `JdbcClubCalendar`, qui supprime puis
insère les sessions d'un club dans une seule transaction).

## Conséquences

- Chaque requête est visible et testée ; aucune requête cachée, aucun chargement paresseux.
- Le mapping est écrit à la main, adaptateur par adaptateur.
- Un fake en mémoire et l'adaptateur SQL peuvent partager les mêmes tests de contrat
  (`ClubCalendarContract`).

## Options écartées

- **JPA / Hibernate** : requêtes cachées, complexité de mapping et couplage du domaine aux
  annotations ORM, sans bénéfice concret pour ce modèle.
