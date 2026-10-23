# 9. Un front-end Angular séparé, sans règle métier

Date : 2026-09-25 (consignée le 2026-10-05)

## Statut

Acceptée

## Contexte

Les slices se livrent jusqu'à l'écran. Le front-end est aussi un objectif d'apprentissage.

## Décision

Nous écrivons le front-end en Angular 22, dans `front-end/`, comme un projet npm séparé que Maven
ne construit pas. Il ne contient aucune règle métier : elles restent dans le back-end. Il suit
lui aussi des ports (`Clubs`, `Communes`, `OpeningHours`) implémentés par des adaptateurs HTTP,
et le même rangement par slice. Les tests de composants utilisent Vitest avec jsdom.

Toutes les routes de l'API sont servies sous `/api` ; en développement, `proxy.conf.json` relaie
`/api` vers le back-end, si bien que le navigateur ne parle qu'au port 4200 et que le back-end n'a
pas besoin de CORS.

Les écrans sont des décisions de produit : chaque écran est validé sur une maquette basse
fidélité avant d'être codé.

## Conséquences

- Deux chaînes de build (Maven et npm) et deux jobs de CI.
- L'accord sur l'API entre les deux côtés doit être vérifié (voir
  [0010](0010-contrat-pact-front-back.md)).
- Recharger un écran comme `/clubs/new` affiche l'écran au lieu d'appeler l'API.

## Options écartées

Aucune alternative de framework n'a été consignée au moment de la décision.
