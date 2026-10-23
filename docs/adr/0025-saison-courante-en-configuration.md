# 25. La saison courante est une constante de configuration

Date : 2026-10-23

## Statut

Acceptée

## Contexte

Les cas d'usage reçoivent la saison courante explicitement. Une saison commence à la rentrée
scolaire, qui n'a pas de jour fixe : aucune règle métier ne permet de la déduire de la date du
jour.

## Décision

La saison courante est une constante, 2026-2027, dans `infrastructure.ClubConfiguration`, fournie
aux cas d'usage qui en ont besoin.

## Conséquences

- Aucune date du jour n'est lue : les cas d'usage restent déterministes.
- La constante doit être changée à la main à chaque rentrée.
- Quand une règle donnera la date de la rentrée (par exemple par Craft Calendar, voir
  [0016](0016-craft-calendar-contexte-generique.md)), une nouvelle décision remplacera celle-ci.

## Options écartées

- **Calculer la saison depuis la date du jour** : inventerait une borne que le métier n'a pas
  fixée.
