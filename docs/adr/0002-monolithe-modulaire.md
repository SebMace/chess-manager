# 2. Un monolithe modulaire

Date : 2026-09-27 (consignée le 2026-10-05)

## Statut

Acceptée

## Contexte

Le projet pourrait couvrir plusieurs domaines : gestion de club, calendrier, parties, compétitions,
entraînement. Il est développé par une seule personne, sans exigence de déploiement indépendant
ni de charge. Les frontières entre ces domaines sont encore en découverte.

## Décision

Nous construisons une seule application Spring Boot. Un bounded context est un package de premier
niveau (`clubmanagement`, plus tard `craftcalendar` ou `chessdecision`), pas un service. Une
frontière de bounded context n'est pas une frontière de déploiement : les contextes partagent la
JVM, jamais leur modèle, et ne partagent pas de transaction.

Un nouveau bounded context n'est extrait que lorsque le langage, les invariants, le cycle de vie,
la propriété des données ou le rythme de changement divergent réellement.

## Conséquences

- Un seul build, un seul déploiement, une seule base PostgreSQL.
- Les dépendances entre contextes passent par un port et un adaptateur, comme si l'appel était
  déjà distant ; l'extraction reste possible sans réécrire le domaine (voir
  [0016](0016-craft-calendar-contexte-generique.md)).
- Rien n'empêche encore le compilateur de franchir une frontière : ArchUnit le vérifie (voir
  [0003](0003-architecture-hexagonale.md)).

## Options écartées

- **Microservices** : coût d'exploitation et de cohérence sans aucun besoin démontré.
- **Un bounded context par domaine dès le départ** : découpage spéculatif ; `Person`, `Club` et
  `ClubRelationship` restent dans un seul contexte.
