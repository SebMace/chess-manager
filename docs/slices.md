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
| Find the towns of a postcode | `townsofpostcode` | livrée | — |
| Find the postcodes of a town | `postcodesoftown` | livrée | — |
| Find the towns of a commune | `townsofcommune` | livrée | — |

Évolutions de *Create a club* : comité départemental, identifiant FFE, commune, siège social,
salle de jeu, localité et code postal proposés à partir de la commune choisie.

Durcissement de l'aide à la saisie des adresses (*Find the towns of a postcode*, *Find the
postcodes of a town*, *Find the towns of a commune*), sans nouvelle capacité :

- une recherche ne sert que si la saisie qui l'a lancée est toujours celle de l'adresse : une
  réponse tardive pour un code postal, une localité ou une commune remplacés, effacés ou
  désélectionnés entre-temps ne change plus l'adresse ni ses suggestions, même quand elle échoue ;
- une suggestion disparaît dès que change ce pour quoi elle a été trouvée, que l'administrateur
  l'ait modifié ou que le changement de commune l'ait remis à blanc ;
- une localité unique ne remplace plus une localité que l'administrateur a saisie pendant la
  recherche ;
- les recherches en cours sont abandonnées quand l'adresse ou le formulaire ne sont plus affichés.

### Leçon YAGNI : l'aide à la saisie des adresses

L'aide à la saisie des adresses et son durcissement sont gardés tels quels. Le principe YAGNI
aurait pourtant dû être appliqué, et il le sera à l'avenir :

- **un geste rare** : on ne crée un club qu'une fois, et c'est un administrateur qui le fait ;
  saisir deux adresses à la main ne justifiait pas trois recherches ;
- **un seul sens suffisait** : la commune est déjà choisie, et le siège d'un club est presque
  toujours dans sa commune ; *Find the towns of a commune* couvrait le cas courant. *Find the
  postcodes of a town* est venue par symétrie, et c'est la plus coûteuse (mise à la norme de
  La Poste, homonymes) ;
- **des règles pour des cas que personne n'a signalés** : garder ce qui a été proposé par la
  commune, remettre les adresses à blanc quand elle change, garder la saisie au premier choix ;
- **un durcissement sans bug constaté** : les réponses tardives étaient une course théorique,
  alors que le back-end répond en quelques millisecondes depuis la mémoire ; s'en protéger a
  demandé 21 tests et fait passer l'adresse dans un `WritableSignal` plutôt qu'un `model()` ;
- **une justification par l'avenir** : « plus tard lieu d'une session » justifiait la
  généralité par une fonctionnalité qui n'existe pas.

À l'avenir, avant chaque slice, évolution ou correctif, on se demande si le besoin est réel
aujourd'hui et quelle est l'option la plus simple, jusqu'à « ne rien faire ». Ces signaux
imposent la question : un geste rare, la symétrie « pour être complet », un « plus tard » dans
une justification, un correctif sans bug observé, des cas limites qui s'enchaînent sur une même
fonctionnalité. Tant qu'aucun besoin réel n'est constaté, l'aide à la saisie des adresses ne reçoit
plus de durcissement ni de cas limite, et ses questions ouvertes attendent.

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
