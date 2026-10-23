# 3. Architecture hexagonale vérifiée par ArchUnit

Date : 2026-09-21 (consignée le 2026-10-05)

## Statut

Acceptée

## Contexte

Le domaine doit pouvoir être testé vite, sans framework, et survivre aux changements de
technologie (base, transport, moteur d'échecs). Une convention de packages seule s'érode sans
qu'on le remarque.

## Décision

Nous appliquons l'architecture hexagonale comme une règle de dépendance :

- le domaine ne dépend d'aucun framework, d'aucune base, d'aucun transport ;
- les cas d'usage dépendent du domaine et de ports définis selon leurs besoins ;
- les adaptateurs (`rest`, `persistence`, `insee`, `laposte`) implémentent ou appellent ces
  ports ;
- le domaine, les ports et les cas d'usage ne portent aucune annotation Spring : les objets sont
  déclarés comme `@Bean` dans `infrastructure`, explicitement.

`architecture.ArchitectureTests` (ArchUnit) vérifie ces règles à chaque `mvn test`.

## Conséquences

- Les tests du domaine et des cas d'usage tournent sans Spring, avec des fakes en mémoire.
- Le câblage est écrit à la main : un peu plus de code dans `infrastructure`, mais aucune magie.
- Les motifs de packages des adaptateurs doivent être mis à jour dans ArchUnit quand un
  adaptateur est ajouté.

## Options écartées

- **Couches classiques (controller → service → repository)** : le domaine finit par dépendre de
  la persistance.
- **Scan de composants et `@Service` sur les cas d'usage** : ferait entrer Spring dans le cœur.
