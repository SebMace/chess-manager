# 11. Acceptation Cucumber en langage métier

Date : 2026-09-11 (consignée le 2026-10-05)

## Statut

Acceptée

## Contexte

Les règles métier doivent être lisibles par quelqu'un qui connaît les échecs et les clubs, et
rester vraies quel que soit le canal (cas d'usage, API, écran).

## Décision

Nous écrivons les scénarios d'acceptation en Gherkin et les exécutons avec Cucumber sur la
plateforme JUnit. Les scénarios ne parlent que le langage métier : jamais de HTTP, d'URL, de
JSON, de SQL, d'écran ni de bouton. Les steps passent par un driver de scénario qui appelle les
cas d'usage. Seuls les scénarios marqués `@acceptance` conditionnent le build ; la CI exécute
aussi tous les autres pour signaler ceux qui sont en attente.

## Conséquences

- Les dépendances Cucumber restent en scope test ; le domaine n'en sait rien.
- Des spécifications futures peuvent être écrites avant leur implémentation, sans casser le
  build.
- Un détail technique va dans les steps, le driver ou un adaptateur, jamais dans le scénario.

## Options écartées

Aucune alternative n'a été consignée au moment de la décision.
