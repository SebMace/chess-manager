# Backlog des vertical slices

Ce backlog ordonne les capacités métier de Chess Manager (voir `AGENTS.md` §6.1). Une slice est un
cas d'usage métier, livré de bout en bout. Elle porte le nom de son cas d'usage, le même partout :
backlog, package, classe, `.feature`. Un nouveau champ ou une nouvelle règle sur un cas d'usage
existant est une évolution de cette slice, pas une nouvelle slice.

Statuts :

- **livrée** : de l'acceptation à l'écran, en passant par la persistance et REST quand la slice
  en a besoin ;
- **cas d'usage seulement** : le cas d'usage existe, avec des fakes en mémoire, sans persistance,
  ni REST, ni écran ;
- **prochaine** : la slice à démarrer ;
- **à venir**.

Seule la prochaine slice est détaillée ; les autres le seront quand on les démarrera.

## Slices existantes

| Slice | Package | Statut | Dépend de |
|---|---|---|---|
| Create a club | `createclub` | livrée | — |
| Find the communes of a committee | `communesofcommittee` | livrée | — |
| Record a person | `recordperson` | cas d'usage seulement | — |
| Update a person | `updateperson` | cas d'usage seulement | Record a person |
| Register a prospect | `registerprospect` | cas d'usage seulement | Create a club |
| Register a license | `registerlicense` | cas d'usage seulement | Create a club |
| Register a partnership | `registerpartnership` | cas d'usage seulement | Create a club |
| Recognize an external player | `isexternalplayer` | cas d'usage seulement | Register a license |

Évolutions de *Create a club* : comité départemental, identifiant FFE, commune, siège social,
salle de jeu.

## Slices à venir

On finalise d'abord la création d'un club ; les slices sur les joueurs viendront plus tard.

| Slice | Package | Statut | Dépend de |
|---|---|---|---|
| Consult the clubs of a department | `clubsofdepartment` | prochaine | Create a club |
| Define the opening hours of a club | `defineopeninghours` | à venir | Create a club |
| Move the registered office of a club | `moveregisteredoffice` | à venir | Create a club |

- *Define the opening hours of a club* s'appuiera sur le bounded context Craft Calendar (voir
  les thèmes). Le terme métier (« horaires d'ouverture », « séances de jeu », « tranches
  horaires ») reste à confirmer.
- Évolutions prévues de *Create a club* : créer un club depuis la liste d'un département,
  revenir à la liste après la création, pré-remplir le comité.

### Consult the clubs of a department

- **Intention** : à partir d'un numéro de département, l'administrateur retrouve les clubs gérés
  par l'application, avec leur nom, leur commune et leur identifiant FFE. C'est le point d'entrée
  de l'application.
- **Décisions** :
  - le filtre porte sur le comité départemental du club (en métropole, son code est le numéro du
    département), pas sur le département de la commune ;
  - la liste est triée par nom de club, sans tenir compte des accents ni des majuscules ;
  - l'acteur est l'administrateur ; l'identifiant FFE est affiché ;
  - quand aucun club n'a été créé dans le comité, un message le dit à l'administrateur ;
  - un comité inconnu est traité de la même façon : aucun club, sans refus.
- **Reporté** : l'exclusion explicite des clubs non gérés par l'application, jusqu'à ce que de
  tels clubs puissent exister en base (aujourd'hui, seul *Create a club* crée des clubs, tous
  gérés).

## Thèmes, à découper le moment venu

- **Craft Calendar** : bounded context de calendrier générique, indépendant des échecs et
  réutilisable par d'autres applications. Première utilisation prévue : les horaires d'ouverture
  d'un club.
- **Joueurs et relations avec les clubs** : prospects, licences, partenariats, club quitté,
  dirigeants (président, secrétaire, trésorier), visiteurs.
- **Droits et autorisations** : authentification, accès des visiteurs et des joueurs externes.
- **Compétitions** : tournois, rondes, appariements, résultats, compétitions par équipes.
- **Parties** : enregistrer, importer, rejouer et analyser des parties ; PGN et FEN.
- **Intégrations** : Lichess, chess.com, retransmission en direct, moteur Stockfish.
- **Entraînement** : tactique, stratégie, finales, ouvertures, jeu depuis une position.
- **Notifications** des utilisateurs intéressés.
