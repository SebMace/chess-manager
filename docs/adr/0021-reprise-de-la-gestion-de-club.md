# 21. Reprise de la gestion de club

Date : 2026-10-23

## Statut

Acceptée. Lève la pause de la gestion de club décidée par
[0020](0020-priorite-a-chess-decision.md) ; le reste de 0020 demeure.

## Contexte

L'application doit être présentée au président et au secrétaire d'un club. Pour cela, il faut
au moins enregistrer et consulter les joueurs liés à un club, jusqu'à l'écran.

## Décision

La gestion de club n'est plus en pause. Elle reprend avec les joueurs liés à un club :
*Register a new member* et *Consult the members of a club*, livrées de bout en bout (PR 24).
Chaque nouvelle slice de ce domaine passe d'abord au crible YAGNI ; ses thèmes restent indivis
tant qu'aucun cas d'usage n'est demandé.

## Conséquences

- La gestion de club et Chess Decision avancent toutes deux ; le principe d'une seule pull
  request à la fois s'applique entre elles.
- Les données de personnes sont désormais stockées : le registre des traitements le reflète, et
  seules des données fictives sont utilisées tant que l'authentification n'existe pas (voir
  [0019](0019-authentification-differee.md)).

## Options écartées

Aucune alternative n'a été consignée au moment de la décision.
