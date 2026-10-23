# 5. Java 26 et Spring Boot 4.1.1

Date : 2026-09-25 (consignée le 2026-10-05)

## Statut

Acceptée

## Contexte

Le premier squelette de bout en bout (*Create a club*, de HTTP à PostgreSQL) demandait un socle
pour l'application. Le projet sert aussi à pratiquer les versions récentes du langage.

## Décision

Nous utilisons Java 26 (Eclipse Temurin, `--release 26`, sans fonctionnalité en preview) et
Spring Boot 4.1.1, dont la gestion des dépendances fixe aussi les versions des bibliothèques de
test (JUnit Jupiter 6.0.3). Maven est utilisé sans wrapper.

## Conséquences

- Java 26 au minimum pour compiler et exécuter l'application, localement comme en CI.
- Les versions ne montent que sur demande explicite.
- Le passage à Java 27 attendra que Spring Boot le supporte officiellement.

## Options écartées

- **Java 27** : souhaité, mais Spring Boot 4.1.1 ne le supporte officiellement que jusqu'à 26.
