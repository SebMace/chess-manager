# 19. Authentification différée

Date : 2026-09-25 (consignée le 2026-10-05)

## Statut

Acceptée

## Contexte

Les scénarios parlent d'un administrateur qui crée un club, mais aucun cas d'usage n'exige
encore de distinguer qui agit. Ce que peut faire un joueur externe une fois connecté relève d'un
futur contexte d'autorisation.

## Décision

Nous différons l'authentification et l'autorisation : rien ne vérifie encore que l'appelant est
un administrateur. Elles seront traitées comme un thème à part entière, quand un cas d'usage le
demandera et au plus tard avant la mise en production.

## Conséquences

- L'application ne doit être exposée qu'en local tant que ce thème n'est pas traité.
- Depuis [0021](0021-reprise-de-la-gestion-de-club.md), la liste des membres d'un club est lisible
  par quiconque joint l'API : seules des données fictives sont utilisées.
- Toute décision de sécurité (fournisseur d'identité, rôles, secrets) fera l'objet de son propre
  ADR.

## Options écartées

Aucune alternative n'a été consignée au moment de la décision.
