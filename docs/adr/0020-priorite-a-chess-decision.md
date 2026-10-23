# 20. Priorité à Chess Decision

Date : 2026-10-02 (consignée le 2026-10-05)

## Statut

Acceptée. La pause de la gestion de club est levée par
[0021](0021-reprise-de-la-gestion-de-club.md), et la priorité donnée à Chess Decision est
remplacée par [0028](0028-retour-a-la-vie-du-club.md).

## Contexte

Chess Manager a commencé par la gestion de club. Son auteur veut d'abord un outil personnel de
progression aux échecs.

## Décision

Le cœur du produit devient **Chess Decision** : le joueur soumet une position, éventuellement des
thèmes, son analyse, ses coups candidats et son plan ; l'outil confronte ce raisonnement aux
ressources concrètes de la position et répond en séparant faits vérifiés, interprétations et
incertitudes.

Quatre responsabilités restent distinctes : le domaine (positions, propositions, thèmes,
retours), le moteur (vérification tactique, évaluations, variantes), le LLM (formulation des
explications) et un éventuel serveur MCP (exposition à un client IA, jamais moteur du
raisonnement). Aucune infrastructure, MCP compris, n'est construite avant un premier usage utile.

La gestion de club est en pause : son code est gardé tel quel, sans suppression ni refonte, et
toute nouvelle slice de ce domaine passe d'abord au crible YAGNI.

## Conséquences

- Les prochaines slices viennent de Chess Decision.
- Faire de Chess Decision un bounded context distinct reste une proposition, à valider par un
  ADR.
- Les thèmes sont des hypothèses ou des objectifs d'apprentissage, jamais des vérités ; les
  déséquilibres de Silman peuvent inspirer, sans devenir une grille obligatoire.
- Cette pause ne vient pas d'un refus de la FFE.

## Options écartées

- **Continuer la gestion de club en priorité** : elle ne sert pas l'objectif personnel de
  progression.
- **Supprimer le code de gestion de club** : il reste un acquis et un support d'apprentissage.
