# 13. Intégration continue avec GitHub Actions

Date : 2026-09-21 (consignée le 2026-10-05)

## Statut

Acceptée

## Contexte

Le dépôt est public sur GitHub. Chaque pull request doit prouver que le build, les tests,
l'architecture et le contrat tiennent, et les rapports doivent rester consultables.

## Décision

Nous utilisons GitHub Actions : un job back-end (tests, build gate Cucumber, rapport de tous les
scénarios, PIT, synthèse) et un job front-end (tests et build). Les actions sont épinglées par
hash de commit. Les rapports JaCoCo, PIT et Cucumber sont publiés sur GitHub Pages après chaque
push réussi sur `main`. Le workflow ne demande aucun secret.

## Conséquences

- Les rapports publiés sur Pages sont publics.
- Seul le job de publication reçoit les permissions `pages: write` et `id-token: write`.
- Un run plus récent sur la même référence annule le précédent.

## Options écartées

Aucune alternative n'a été consignée au moment de la décision.
