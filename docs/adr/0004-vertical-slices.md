# 4. Organiser le code en vertical slices

Date : 2026-09-26 (consignée le 2026-10-05)

## Statut

Acceptée

## Contexte

Le travail est livré par capacité métier, de l'acceptation à l'écran. Un rangement par couche
technique dispersait un même cas d'usage dans plusieurs packages et rendait sa portée difficile
à lire.

## Décision

À l'intérieur d'un bounded context, chaque cas d'usage a son package, nommé d'après lui
(`clubmanagement.createclub`) : le cas d'usage, ses refus, son adaptateur entrant dans `rest`, et
ses tests unitaires. Le modèle du domaine (`domain`), les ports (`ports`) et les adaptateurs
sortants qui persistent un agrégat ou lisent une référence (`persistence`, `insee`, `laposte`)
restent partagés. Une slice ne dépend jamais d'une autre slice. Le front-end suit la même
organisation.

Le backlog ordonné des slices vit dans `docs/slices.md` ; une slice porte un nom métier, jamais
un code.

## Conséquences

- Ajouter un cas d'usage ajoute un package, sans toucher aux autres slices.
- Un agrégat n'a qu'un modèle : une slice ne possède jamais de modèle parallèle.
- ArchUnit interdit les dépendances entre slices.

## Options écartées

- **Un modèle par slice** : dupliquerait les invariants des agrégats.
- **Rangement par couche technique** : l'organisation d'origine, abandonnée pour la raison
  ci-dessus.
