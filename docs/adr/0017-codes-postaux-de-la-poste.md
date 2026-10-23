# 17. Les codes postaux viennent de La Poste

Date : 2026-09-27 (consignée le 2026-10-05)

## Statut

Acceptée

## Contexte

La saisie des adresses d'un club (siège social, salle de jeu) propose localités et codes postaux.
Les communes viennent du code officiel géographique de l'INSEE, qui ne contient aucun code
postal. Une localité postale peut différer du nom de la commune, et la correspondance entre
codes postaux et localités est de plusieurs à plusieurs.

## Décision

Nous prenons les codes postaux et les localités dans la base officielle de La Poste (Licence
Ouverte 2.0), embarquée dans `src/main/resources/laposte` et lue par l'adaptateur `laposte`. Une
localité est retrouvée par le code INSEE de la commune quand on l'a, et écrite comme La Poste
l'écrit (majuscules sans accents). L'application propose, elle ne déduit pas.

## Conséquences

- Plusieurs localités ou codes postaux sont suggérés sans être choisis.
- L'adaptateur met la saisie à la norme de La Poste (SAINT → ST, tirets, apostrophes).
- Leçon YAGNI : trois recherches pour un geste rare, c'était trop (voir `docs/slices.md`) ; l'aide
  à la saisie des adresses ne reçoit plus de durcissement sans besoin constaté.

## Options écartées

- **Le code officiel géographique de l'INSEE** : il ne contient aucun code postal.
- **Déduire la localité d'un code postal** : la correspondance n'est pas une fonction.
