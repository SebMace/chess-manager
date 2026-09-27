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
| Define the opening hours of a club | `defineopeninghours` | livrée | Create a club |
| Consult the opening hours of a club | `openinghoursofclub` | livrée | Define the opening hours of a club |
| Record a person | `recordperson` | cas d'usage seulement | — |
| Update a person | `updateperson` | cas d'usage seulement | Record a person |
| Register a prospect | `registerprospect` | cas d'usage seulement | Create a club |
| Register a license | `registerlicense` | cas d'usage seulement | Create a club |
| Register a partnership | `registerpartnership` | cas d'usage seulement | Create a club |
| Recognize an external player | `isexternalplayer` | cas d'usage seulement | Register a license |
| Find the towns of a postcode | `townsofpostcode` | livrée | — |
| Find the postcodes of a town | `postcodesoftown` | livrée | — |
| Find the towns of a commune | `townsofcommune` | livrée | — |

Évolutions de *Create a club* : comité départemental, identifiant FFE, commune, siège social,
salle de jeu, localité et code postal proposés à partir de la commune choisie.

Évolution de *Consult the clubs of a committee* : chaque club porte son identifiant, pour mener
à ses horaires d'ouverture.

Les règles des horaires d'ouverture (sessions, activités, lieu, remplacement) sont décrites dans
le README. Club Management enregistre lui-même les sessions derrière son port `ClubCalendar` :
Craft Calendar sera extrait quand un exemple réclamera un vrai comportement de calendrier.

## Slices à venir

On finalise d'abord la création d'un club ; les slices sur les joueurs viendront plus tard.

| Slice | Package | Statut | Dépend de |
|---|---|---|---|
| Move the registered office of a club | `moveregisteredoffice` | à venir | Create a club |

- Évolutions prévues de *Create a club* : revenir à la liste après la création, pré-remplir le
  comité à partir du département consulté.
- Évolutions possibles de *Define the opening hours of a club*, à passer au crible YAGNI : refuser
  un club non géré par l'application ou inconnu ; décider si une session peut franchir minuit ;
  présenter les sessions du lundi au dimanche ; afficher le nom du club sur l'écran des horaires.
- Reporté : saisons, vacances scolaires, abonnement depuis un agenda externe.
- La prochaine slice reste à choisir.

### Find the towns of a commune

- **Intention** : quand l'administrateur choisit la commune d'un club, l'application propose
  par défaut sa localité postale, et ses codes postaux, pour le siège social et la salle de jeu.
  Par exemple Olivet (45232) → OLIVET, 45160 ; Colmars (04061) → COLMARS LES ALPES, 04370.
- **Décisions** :
  - la localité est trouvée par le code INSEE de la commune dans la base La Poste, pas par son
    nom : pas d'homonyme (Olivet de la Mayenne) et la localité est celle de La Poste, même quand
    elle diffère du nom de la commune ;
  - une commune peut avoir plusieurs localités postales (44 cas, surtout en Polynésie) ;
  - à l'écran, quand la commune choisie change, la localité et le code postal du siège social et
    de la salle de jeu sont remis à blanc, puis la nouvelle commune propose les siens ; au premier
    choix d'une commune, une localité ou un code postal déjà saisis sont gardés. Plusieurs codes
    postaux sont suggérés sans être choisis.

### Find the towns of a postcode

- **Intention** : quand l'administrateur saisit le code postal d'une adresse, l'application lui
  propose les localités desservies par ce code, par exemple 45240 → LA FERTE ST AUBIN,
  LIGNY LE RIBAULT, MARCILLY EN VILLETTE, MENESTREAU EN VILLETTE, SENNELY. Sert à toutes les
  adresses postales : siège social, salle de jeu, et plus tard lieu d'une session.
- **Décisions** :
  - la source est la base officielle des codes postaux de La Poste (Licence Ouverte 2.0), pas le
    code officiel géographique de l'INSEE, qui ne contient aucun code postal ;
  - la localité d'une adresse est le libellé d'acheminement de La Poste, qui diffère parfois du
    nom de la commune (Colmars → COLMARS LES ALPES) ;
  - la localité est proposée telle que La Poste l'écrit, en majuscules sans accents, forme
    attendue sur la dernière ligne d'une adresse (NF Z10-011) ;
  - un code postal peut desservir plusieurs localités : l'application propose, elle ne déduit pas.
  - à l'écran, une seule localité remplit le champ ; plusieurs sont suggérées sans être
    choisies ; rien n'est cherché avant les 5 chiffres ; si la recherche échoue, rien n'est
    suggéré et la saisie reste libre, sans message.
- **Questions ouvertes** : refuser une localité absente de la base, ou la laisser libre ?

### Find the postcodes of a town

- **Intention** : quand l'administrateur saisit la localité d'une adresse, l'application lui
  propose les codes postaux qui la desservent, par exemple ORLEANS → 45000, 45100. C'est le
  sens inverse de *Find the towns of a postcode*, avec la même source La Poste.
- **Décisions** :
  - une localité peut avoir plusieurs codes postaux, et des localités homonymes existent dans
    plusieurs départements (OLIVET → 45160, 53410) : l'application propose tous leurs codes ;
  - l'adaptateur La Poste met la saisie à la norme de La Poste : majuscules sans accents, tirets
    et apostrophes remplacés par des espaces, les mots SAINT et SAINTE abrégés en ST et STE
    (mais SAINTES, la ville, reste en toutes lettres) ;
  - à l'écran, un code postal unique remplit le champ seulement s'il est vide ; plusieurs codes
    sont suggérés ; si la recherche échoue, rien n'est suggéré, sans message.

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
