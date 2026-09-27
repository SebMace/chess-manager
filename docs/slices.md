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
| Define the opening hours of a club | `defineopeninghours` | prochaine | Create a club |
| Move the registered office of a club | `moveregisteredoffice` | à venir | Create a club |

- Évolutions prévues de *Create a club* : revenir à la liste après la création, pré-remplir le
  comité à partir du département consulté.

### Define the opening hours of a club

- **Intention** : l'administrateur définit les horaires d'ouverture d'un club, par exemple « le
  club ouvre chaque vendredi de 15:00 à 17:00 ».
- **Décisions** :
  - le terme métier est *opening hours* (horaires d'ouverture du club), pas les séances de jeu ;
  - les horaires s'appuient sur le bounded context Craft Calendar. Craft Calendar est en amont
    (Open Host Service et Published Language) ; Club Management est en aval et traduit dans une
    couche anticorruption. Le club ne connaît aucune classe de Craft Calendar ;
  - les concepts de Craft Calendar émergent des tests de cette slice, sans conception préalable.
- **Reporté** : activités, lieux, saisons, vacances scolaires, abonnement depuis un agenda
  externe.
- **Questions ouvertes** : refuser un club non géré par l'application ? Une plage peut-elle
  franchir minuit ? Définir des horaires ajoute-t-il une plage ou remplace-t-il les précédentes ?

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
