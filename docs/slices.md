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

## Priorité : Chess Decision

Depuis le 2 octobre 2026, Chess Manager devient d'abord un outil personnel de progression aux
échecs. Son cœur, **Chess Decision** : le joueur soumet une position, éventuellement des thèmes,
son analyse, ses coups candidats et son plan ; l'outil confronte ce raisonnement aux ressources
concrètes de la position et lui répond de façon pédagogique, en séparant les faits vérifiés, les
interprétations et les incertitudes.

- Les thèmes sont des hypothèses ou des objectifs d'apprentissage, jamais des vérités. Les
  déséquilibres de Silman peuvent inspirer, sans devenir une grille obligatoire, et aucun contenu
  de Silman n'est inventé.
- Quatre responsabilités restent distinctes : le domaine (positions, propositions, thèmes,
  retours), le moteur (vérification tactique, évaluations, variantes), le LLM (formuler des
  explications à partir des éléments disponibles) et un éventuel serveur MCP (exposer ces
  capacités à un client IA, sans être le moteur du raisonnement).
- Pas d'infrastructure, MCP compris, avant un premier usage utile identifié.
- Faire de Chess Decision un bounded context distinct reste une proposition, à valider.
- La première slice de Chess Decision reste à choisir.

La gestion de club (clubs, personnes, licences, imports FFE) n'est plus en pause depuis le
23 octobre 2026. Elle reprend avec l'enregistrement des joueurs liés à un club : *Register a new
member* et *Consult the members of a club* ont été menées jusqu'à l'écran, avant de présenter
l'application au président et au secrétaire d'un club. Chaque nouvelle slice de ce domaine passe
d'abord au crible YAGNI.

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
| Register a new member | `registermember` | livrée | Create a club |
| Consult the members of a club | `membersofclub` | livrée | Register a new member |
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

### Register a new member et Consult the members of a club

Une seule page d'écran, `/clubs/:id/members`, porte les deux slices : la liste des membres du club
et le formulaire d'inscription. Les exemples utilisent des joueurs fictifs.

*Register a new member*

- **Intention** : l'administrateur inscrit comme membre d'un club un joueur licencié que
  l'application ne connaît pas encore, avec son prénom, son nom, son identifiant FFE et le type
  de sa licence pour la saison. Par exemple Camille Martin, K58213, licence A, U.S. Orléans.Echecs,
  2026-2027.
- **Invariant** : une personne connue comme licenciée FFE possède nécessairement un identifiant
  FFE personnel et stable, et elle est membre, pour la saison de sa licence, du club dans lequel
  elle a été inscrite.
- **Décisions** :
  - l'identifiant FFE appartient à la personne, pas à la licence : il vient avec la première
    licence A ou B et ne change plus ; `FfeLicense` a été supprimée, la relation au club ne garde
    que le type de licence par saison ;
  - le domaine crée les deux agrégats cohérents (`Person.licensedPlayer`, puis
    `ClubRelationship.membershipOf`) ; le cas d'usage `RegisterNewMember` les enregistre ;
  - le joueur est retrouvé par son identité, jamais par son nom ;
  - un renouvellement se fait dans le même club ; une licence prise dans un autre club est une
    mutation, pas un renouvellement ;
  - la personne et sa relation s'enregistrent ensemble, ou pas du tout : le cas d'usage s'exécute
    dans une transaction posée par `infrastructure` (`TransactionalRegisterNewMember`), pour qu'il
    reste sans Spring ;
  - PostgreSQL stocke la personne (`person`, avec un identifiant FFE unique) et la relation
    (`club_relationship` et `club_relationship_license`, migrations V10 et V11) ; une relation est
    relue en rejouant ses licences et son statut par les opérations du domaine, sans constructeur
    taillé pour la base ;
  - la saison courante est une constante, 2026-2027, dans `ClubConfiguration` : la rentrée n'a pas
    de jour fixe, on ne la calcule pas à partir de la date ;
  - la réponse du back-end n'a pas de `Location` : aucune route ne la servirait ;
  - le classement Elo du joueur est facultatif : un joueur non classé peut être inscrit, ce qui n'est
    pas la même chose qu'un joueur classé à zéro. Le domaine le construit par une fabrique nommée,
    `Person.ratedLicensedPlayer`, plutôt que par un setter ; un Elo négatif est refusé (400) ;
  - `person.elo` est une colonne facultative (migration V12).
- **Couches** : acceptation, cas d'usage, persistance, REST, contrat et écran.

*Consult the members of a club*

- **Intention** : l'administrateur voit les membres d'un club pour la saison courante, avec leur
  nom, leur prénom, leur Elo, leur identifiant FFE et leur licence, triés par nom puis par prénom.
  Un joueur non classé affiche « NC ». Un joueur titré porte son titre FIDE en badge à côté de son nom.
- **Décisions** :
  - une requête dédiée, derrière le port de lecture `ClubMembers`, joint la personne et sa relation
    avec le club : aucun agrégat ne porte cette liste ;
  - un joueur dont la licence ne date que d'une saison passée n'est pas listé ;
  - ni recherche, ni pagination, ni filtre tant qu'aucun besoin réel n'est constaté.
- **Couches** : cas d'usage, persistance, REST, contrat et écran ; la lecture n'a pas de scénario
  d'acceptation, les tests de bout en bout la couvrent.

*Questions ouvertes*, constatées en livrant ces deux slices :

- **un identifiant FFE déjà enregistré** : refuser, ou rattacher la personne existante à un autre
  club (une mutation) ? Aujourd'hui la contrainte d'unicité fait échouer l'inscription : réponse 500,
  message générique à l'écran ;
- **un club inconnu** donne la même réponse 500, par la clé étrangère ; la transaction annule bien
  l'enregistrement de la personne ;
- **un prénom ou un nom vide** n'est refusé que par le navigateur : `Person` ne le valide pas ;
- **courriel et identifiant FIDE** : `JdbcPersonRepository` ne les stocke pas et une `Person` qui les
  porte les perd en silence. Refuser explicitement, ou ajouter les colonnes quand un cas d'usage les
  écrira ? Le classement Elo, lui, est stocké depuis la migration V12 ;
- **titre FIDE** : livré, en mention facultative affichée en badge à côté du nom, sans colonne
  dans le tableau des membres. Valeurs CM, MF, MI et GMI (`person.fide_title`, migration V13) ; un
  titre inconnu est refusé (400). Les équivalents féminins (GMF, MIF, MFF, CMF) ne sont pas modélisés ;
- **niveaux d'arbitre** : pas construits. Les titres d'arbitre de la FFE changent depuis la saison
  2026-2027 (réforme de la DNA : les anciens titres sont valables jusqu'au 31 août 2026). Les
  sources lisibles annoncent AFJ, AFM, AFC et AFO, l'ancien AFC devenant AFM (qui arbitre les
  interclubs), mais la liste officielle est dans des PDF que je n'ai pas pu lire. Les titres AF1 à
  AF4 que j'avais relevés dans un premier temps sont périmés. À confirmer avec le document « Parcours
  arbitre » de la DNA avant de modéliser ;
- **niveaux d'animateur** : pas construits, la terminologie n'est pas fixée. L'Institut National de
  Formation de la FFE liste le DAFFE parmi ses formations, une présentation de la FFE parle de
  brevets fédéraux BF1, BF2 et BF3, et les anciens diplômes sont DIFFE, DAFFE 1, DAFFE 2 et DEFFE.
  À choisir par le club ;
- **`save` fait un `INSERT`** : enregistrer deux fois la même personne ou la même relation échoue
  sur PostgreSQL, alors que le fake en mémoire remplace ;
- **`ClubRelationshipRepository`** : `findByPerson` et `delete` lèvent une
  `UnsupportedOperationException` sur PostgreSQL, aucun cas d'usage branché ne s'en sert. Le port
  est plus large que ce que ces slices utilisent ;
- la mutation n'a pas encore d'opération propre.

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

*Move the registered office of a club* est écartée (YAGNI : un déménagement de siège est un
geste rare). *Register a new member* et *Consult the members of a club* sont livrées : voir plus
haut.

- Évolutions prévues de *Create a club* : revenir à la liste après la création, pré-remplir le
  comité à partir du département consulté.
- Évolutions possibles de *Define the opening hours of a club*, à passer au crible YAGNI : refuser
  un club non géré par l'application ou inconnu ; décider si une session peut franchir minuit ;
  présenter les sessions du lundi au dimanche ; afficher le nom du club sur l'écran des horaires.
- Retrait envisagé du siège social de *Create a club*, à décider plus tard : il est saisi et
  stocké mais aucun cas d'usage ne le lit ; il sert seulement de raccourci pour la salle de jeu,
  et il figure déjà sur le site de la FFE. C'est souvent le domicile d'un dirigeant : ne pas le
  collecter respecte la minimisation des données du RGPD (voir `docs/registre-des-traitements.md`).
  Il reviendra quand un cas d'usage le demandera, par exemple les reçus fiscaux du back-office du
  club.
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

Les thèmes de la gestion de club (Craft Calendar, joueurs et relations avec les clubs, droits et
autorisations, compétitions) restent indivis : on ne découpe un thème que lorsqu'un cas d'usage est
demandé.

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
