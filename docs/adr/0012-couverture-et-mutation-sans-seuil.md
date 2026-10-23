# 12. Couverture et mutation, sans seuil

Date : 2026-09-20 (consignée le 2026-10-05)

## Statut

Acceptée

## Contexte

Le TDD strict doit produire des tests qui détectent vraiment les défauts. La couverture seule ne
le prouve pas, et un seuil arbitraire incite à écrire des tests pour le chiffre.

## Décision

Nous mesurons la couverture avec JaCoCo (tests JUnit et Cucumber) et la qualité des tests avec
PIT, sur `clubmanagement` hors adaptateurs. PIT ne tourne que sur demande et en CI. Aucun seuil de
couverture ni de score de mutation n'est imposé : un mutant survivant se lit à côté de la règle
métier pour décider s'il manque un exemple.

Le scanner SonarQube est configuré, mais aucune destination d'analyse n'est choisie.

## Conséquences

- Un mutant survivant ne fait pas échouer la CI ; il appelle une lecture.
- Les tests qui démarrent PostgreSQL, l'acceptation et ArchUnit sont exclus de PIT, trop lents
  ou sans rapport avec le comportement.

## Options écartées

- **Seuils de couverture ou de mutation** : encouragent les tests écrits pour le chiffre.
