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
| Consult the clubs of a committee | `clubsofcommittee` | livrée | Create a club |
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
| Define the opening hours of a club | `defineopeninghours` | à venir | Create a club |
| Move the registered office of a club | `moveregisteredoffice` | à venir | Create a club |

- *Define the opening hours of a club* s'appuiera sur le bounded context Craft Calendar (voir
  les thèmes). Le terme métier (« horaires d'ouverture », « séances de jeu », « tranches
  horaires ») reste à confirmer.
- La prochaine slice reste à choisir entre les deux.
- Évolutions prévues de *Create a club* : revenir à la liste après la création, pré-remplir le
  comité à partir du département consulté.

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
