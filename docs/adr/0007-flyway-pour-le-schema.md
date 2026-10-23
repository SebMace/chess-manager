# 7. Flyway pour le schéma de la base

Date : 2026-09-25 (consignée le 2026-10-05)

## Statut

Acceptée

## Contexte

Le schéma PostgreSQL évolue à chaque slice qui persiste. La mise en production est prévue à la
rentrée 2027 ; d'ici là, seule la base de développement locale existe.

## Décision

Nous versionnons le schéma avec Flyway, dans `src/main/resources/db/migration`. Flyway applique
les migrations manquantes au démarrage. Une migration appliquée n'est jamais modifiée : un
changement est un nouveau fichier `V<n>`.

Avant la production, les migrations n'ont pas à préserver de données existantes ; elles seront
fusionnées en une seule avant la mise en production.

## Conséquences

- Le schéma est reproductible, en local comme dans les tests avec Testcontainers.
- Une migration qui casse la base locale se règle en vidant les tables locales, sans backfill.
- La fusion des migrations est une dette assumée, à solder avant la rentrée 2027.

## Options écartées

Aucune alternative n'a été consignée au moment de la décision.
