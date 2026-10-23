# 10. Un contrat consumer-driven avec Pact

Date : 2026-09-27 (consignée le 2026-10-05)

## Statut

Acceptée

## Contexte

Le front-end et le back-end évoluent dans des builds séparés. Les tests de chaque côté peuvent
passer alors que les deux ne s'accordent plus sur l'API.

## Décision

Nous vérifions l'API par un contrat consumer-driven avec Pact. Les specs des adaptateurs HTTP du
front-end enregistrent les interactions dans un fichier de contrat commité, régénéré de zéro à
chaque changement. Le back-end le rejoue dans `contract.FrontEndContractTests`, avec PostgreSQL
dans Testcontainers. Un état du fournisseur est nommé dans le langage métier et préparé par les
cas d'usage, jamais directement en base.

Le contrat décrit la forme dont dépend le front-end, pas les règles métier.

## Conséquences

- Toute modification d'une interaction met le contrat à jour dans la même branche ; la CI échoue
  si le contrat régénéré diffère du fichier commité.
- Les specs du front-end s'exécutent l'une après l'autre, sinon elles écraseraient leurs
  interactions.
- Les métriques anonymes de Pact et de Scarf sont désactivées.

## Options écartées

Aucune alternative n'a été consignée au moment de la décision.
