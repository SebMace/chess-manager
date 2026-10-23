  # 8. Testcontainers pour les tests de persistance

Date : 2026-09-25 (consignée le 2026-10-05)

## Statut

Acceptée

## Contexte

Les adaptateurs SQL et le parcours de bout en bout doivent être prouvés contre le dialecte de la
production. Une base en mémoire ne prouve pas le comportement de PostgreSQL.

## Décision

Les tests de persistance, de bout en bout et de contrat côté back-end démarrent un vrai
PostgreSQL 18 avec Testcontainers. En développement, Spring Boot démarre le PostgreSQL décrit
dans `compose.yaml` ; ce support Docker Compose est ignoré pendant les tests.

Les scénarios Cucumber restent au niveau des cas d'usage ; un seul test de bout en bout passe
par HTTP pour le parcours complet.

## Conséquences

- Un moteur Docker doit tourner (OrbStack sur le Mac de développement), localement et en CI.
- Ces tests sont plus lents : ils sont exclus de l'analyse de mutation (voir
  [0012](0012-couverture-et-mutation-sans-seuil.md)).

## Options écartées

- **H2 ou une autre base en mémoire** : ne prouve pas le comportement du dialecte PostgreSQL.
