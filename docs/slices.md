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

## Priorité : la vie du club et les compétitions

Depuis le 23 octobre 2026, Chess Manager est d'abord un outil pour la vie d'un club (ADR 0028).
Ses deux domaines sont la gestion de club (clubs, personnes, licences, horaires d'ouverture,
imports FFE) et les compétitions par équipes, à commencer par la Nationale 4 du Loiret (voir plus
bas). Chaque nouvelle slice passe d'abord au crible YAGNI.

La gestion de club n'est pas en pause : *Register a new member* et *Consult the members of a
club* ont été menées jusqu'à l'écran, avant de présenter l'application au président et au
secrétaire d'un club.

**Chess Decision** n'est plus le produit phare (ADR 0028). Décidé le 2 octobre 2026 (ADR 0020),
c'était un outil personnel de progression : le joueur soumet une position, éventuellement des
thèmes, son analyse, ses coups candidats et son plan, et l'outil confronte ce raisonnement aux
ressources concrètes de la position. Aucun code n'existe et aucune slice n'est planifiée. S'il
reprend, ses principes restent :

- les thèmes sont des hypothèses ou des objectifs d'apprentissage, jamais des vérités ; les
  déséquilibres de Silman peuvent inspirer, sans devenir une grille obligatoire, et aucun contenu
  de Silman n'est inventé ;
- le domaine, le moteur, le LLM et un éventuel serveur MCP restent quatre responsabilités
  distinctes ;
- pas d'infrastructure, MCP compris, avant un premier usage utile identifié ;
- faire de Chess Decision un bounded context distinct reste une proposition, à valider.

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

## Compétition : la Nationale 4 de la Ligue du Centre-Val de Loire

Depuis le 23 octobre 2026, le thème *Compétitions* est découpé pour un premier besoin : un
bounded context `competition` à part (ADR 0027, proposé), des slices nommées d'après leur cas
d'usage, chacune passée au crible YAGNI.

**Le besoin.** Les clubs créés dans l'application engagent chacun une équipe dans le championnat
interclubs de Nationale 4 de la Ligue du Centre-Val de Loire, dont le Loiret est l'un des six
comités : huit équipes, sept rondes, six échiquiers. Un calendrier fixé en début de saison donne
les rencontres de chaque ronde. Avant la ronde, le capitaine compose son équipe et peut imprimer la
feuille de match ; après la ronde, il saisit les résultats.

**Règles retenues**, d'après le A02 et les règles générales R01 de la saison 2026-2027, décrites
avec nos mots (ADR 0018) :

- l'organisateur d'une compétition décide qui peut y jouer. La Fédération organise la Top 16 à la
  Nationale 3, avec des clubs de toute Ligue. Une Ligue organise la Nationale 4 et les divisions
  régionales, pour ses clubs selon la pratique, car aucun texte lu ne l'écrit. Un comité organise
  les divisions départementales. Une Ligue regroupe plusieurs comités : celle du Centre-Val de Loire
  en compte six (Cher, Eure-et-Loir, Indre, Indre-et-Loire, Loir-et-Cher et Loiret) ;
- la Nationale 4 se joue en groupes de 8 ou 10 équipes, en toutes rondes (sept ou neuf rondes),
  sous la responsabilité de la Ligue, qui fixe le nombre de ses groupes, les compose, nomme le
  directeur de groupe et peut apporter des modifications mineures. Les dates des rondes viennent du
  calendrier fédéral ; le directeur de groupe conçoit les appariements et fixe les lieux. L'heure
  de début est 14h15, sauf contrordre ;
- six échiquiers : le A02 laisse aux Ligues le choix d'une Nationale 4 à six échiquiers (3.7.a,
  huit par défaut), et le porteur du projet indique que c'est le cas dans le Loiret, sans règlement
  local publié. La Ligue décidant, nous supposons que c'est le cas de toute la Ligue du
  Centre-Val de Loire (à confirmer). Le nombre d'échiquiers est donc une donnée de la compétition ;
- l'équipe première nommée dans les appariements, celle qui reçoit, a les blancs aux échiquiers
  impairs et les noirs aux échiquiers pairs (3.2) ;
- seuls les licenciés A peuvent jouer, et la licence peut être prise jusqu'à l'heure de début de
  la rencontre (3.6.d, 3.7.a). Une infraction coûte à l'équipe un forfait administratif et deux
  points de parties ;
- écart d'Elo : l'Elo retenu est le dernier publié ; quand deux membres de l'équipe ont des Elo qui
  diffèrent de plus de 100 points, le mieux classé joue devant l'autre (3.6.e). Le seuil est
  strict (100 passe, 101 est refusé), la règle vaut pour toutes les paires, pas seulement pour les
  échiquiers voisins, et le manquement coûte un forfait administratif au mieux classé. Les textes
  de 2025-2026 et 2026-2027 disent 100, celui de 2009 disait 103 : la valeur de la Ligue du
  Centre-Val de Loire reste à confirmer ;
- un joueur jamais classé reçoit un Elo estimé selon sa catégorie d'âge (R01 5), par exemple 1399
  pour un senior, 1199 pour un U20 à U16 et 999 pour un U14 ou U12 en cadence standard.
  L'application ne stocke pas la date de naissance, donc pas la catégorie d'âge ;
- la composition est une liste ordonnée, sans trou entre les échiquiers, remise au moins 15
  minutes avant le début ; elle ne se modifie plus ensuite (3.6.a). À défaut du capitaine, un
  membre de l'équipe peut la remettre, et chaque équipe a un capitaine titulaire d'une licence
  fédérale (R01 8) ;
- un club peut engager plusieurs équipes, même dans un même groupe. Elles sont numérotées par force
  décroissante, et un joueur ne joue que dans une des équipes d'un groupe, sauf en barrage (1.2,
  3.7.b, 3.7.d) ;
- une équipe compte au plus trois joueurs mutés par match, deux quand elle est de six (3.7.g) ;
- un club engagé aligne une équipe à chaque ronde, faute de quoi c'est un forfait (4.3), que nous
  ne modélisons pas encore ;
- partie gagnée 1 point, perdue 0, nulle non comptée. Le match va à l'équipe qui a le plus de
  points de parties, et il est nul à égalité. Match gagné 3 points, nul 2, perdu 1, perdu par
  forfait sportif 0 ; une exemption ne marque rien (4.1, 4.2). Le classement se fait au total des
  points de match, puis au résultat entre les équipes à départager (points de match, différentiel,
  points « pour »), puis aux différentiels de toute la compétition (4.4).

Sources : [le A02 de la saison
2026-2027](http://echecs.asso.fr/Actus/2864/A02_2026_27_Championnat_de_France_des_Clubs.pdf)
(daté du 14/10/2026) et [les règles générales R01 de la saison
2026-2027](http://echecs.asso.fr/Actus/2864/R01_2026_27_Regles_generales.pdf) (modifiées le
13/06/2026). Le A02 de 2025-2026 dit la même chose sur ces points, hormis deux précisions de
2026-2027 : la remise de la composition par un autre membre que le capitaine, et l'exception des
barrages pour un joueur dans deux équipes d'un même groupe (3.7.d). Aucun règlement de la Ligue du
Centre-Val de Loire n'a été trouvé.

**Contrôle YAGNI.** Le site de la FFE permet déjà de préparer une feuille de match et de la
poster au directeur de groupe. Le A02 exige un document officiel pour la composition (3.6.a), et
le club responsable de la rencontre saisit le procès-verbal sur le site fédéral avant 22 h
(3.11.a). Ce que le site ne fait pas, c'est contrôler la composition avec les membres, les
licences et les Elo que l'application connaît déjà : c'est la valeur de *Compose a team for a
match*. *Print the match sheet* attend que le besoin se confirme : la feuille ne remplace pas le
document officiel.

*Record the results of a match* ferait double emploi avec la saisie du site fédéral. Le 23 octobre
2026, le porteur du projet a donné la raison qui manquait : l'application garde la mémoire du club,
ce que le site ne fait pas. Pour chaque échiquier, le résultat et un commentaire du capitaine ou du
joueur, puis le récit de la ronde écrit par le webmaster ou le capitaine. La slice passe donc avant
*Compose a team for a match* et accepte la double saisie.

*Create a competition*, *Engage a team in a competition* et *Schedule a match* sont des gestes
d'une fois par saison, faits par un administrateur. Le porteur du projet a tranché le 23 octobre
2026 : ni écran ni REST pour *Engage a team in a competition* et *Schedule a match* (YAGNI). Les
cas d'usage seuls suffisent, alimentés en développement comme les clubs de démonstration (voir la
leçon YAGNI plus haut) ; un écran viendra quand un administrateur le demandera. Même choix pour
l'écriture d'un commentaire et d'un récit : seul leur affichage a un écran.

| Slice | Package | Statut | Dépend de |
|---|---|---|---|
| Find the league of a committee | `clubmanagement.leagueofcommittee` | à venir | — |
| Create a competition | `competition.createcompetition` | livrée | — |
| Consult the competitions of the season | `competition.competitionsofseason` | livrée | Create a competition |
| Engage a team in a competition | `competition.engageteam` | en cours | Create a competition |
| Schedule a match | `competition.schedulematch` | à venir | Engage a team in a competition |
| Consult the calendar of a competition | `competition.calendarofcompetition` | à venir | Schedule a match |
| Record the results of a match | `competition.recordmatchresults` | à venir | Schedule a match, Register a new member |
| Consult a match | `competition.matchofcompetition` | à venir | Record the results of a match, Consult the calendar of a competition |
| Comment a board of a match | `competition.commentboard` | à venir | Record the results of a match |
| Write the report of a match | `competition.writematchreport` | à venir | Record the results of a match |
| Compose a team for a match | `competition.composeteam` | à venir | Schedule a match, Register a new member |
| Print the match sheet | `competition.printmatchsheet` | à venir | Compose a team for a match |
| Consult the standings of a competition | `competition.standingsofcompetition` | à venir | Record the results of a match |

Ordre proposé : la démonstration de la ronde 1, ci-dessous, passe par les sept premières slices du
tableau, de *Engage a team in a competition* à *Write the report of a match* (le contrôle de la
Ligue d'un club attend *Find the league of a committee* : les huit clubs de démonstration sont du
Loiret). *Compose a team for a match*, *Print the match sheet* et le classement suivent. Les slices
de consultation sont celles par lesquelles l'administrateur voit où il en est : la liste des
compétitions, le calendrier avec ses rondes jouées, la rencontre, le classement.

### La démonstration de la ronde 1

Le 23 octobre 2026, le porteur du projet demande de voir la compétition vivre dans l'application :
il est capitaine d'une équipe d'Orléans (« Les Cavaliers de la Loire », DEMO01, club de
démonstration) engagée en Nationale 4 pour la saison 2026-2027. La première ronde, jouée le 11
octobre 2026 contre Pithiviers (« Pithiviers Roque Club », DEMO05), doit montrer, échiquier par
échiquier, le résultat et un commentaire, puis le récit de la ronde. Le jeu d'essais de
développement (`DevelopmentData`) la crée par les cas d'usage, comme les clubs : il vide les tables
et rejoue la démonstration à chaque démarrage.

Ordre des slices, choisi pour que l'écran s'enrichisse à chaque pas :

1. *Engage a team in a competition* : cas d'usage et persistance, ni REST ni écran ;
2. *Schedule a match* : idem ;
3. *Consult the calendar of a competition* : REST, contrat et écran, la ronde 1 « à jouer » ;
4. *Record the results of a match* : cas d'usage et persistance, la ronde devient « jouée » ;
5. *Consult a match* : REST, contrat et écran, les échiquiers, les joueurs et les résultats ;
6. *Comment a board of a match* : cas d'usage et persistance, les commentaires sur l'écran de la
   rencontre ;
7. *Write the report of a match* : idem, le récit.

Le canevas de la rencontre. Orléans reçoit, donc ses joueurs ont les blancs aux échiquiers impairs.
Seuls les licenciés A jouent, du mieux classé au moins bien classé ; les Elo viennent des données
de développement, dont le tirage est fixe.

| Éch. | Blancs | Noirs | Résultat |
|---|---|---|---|
| 1 | Bastien Durand (Orléans, 2092) | Hugo Moreau (Pithiviers, 2313, MF) | 0-1 |
| 2 | Élodie Morel (Pithiviers, 2049) | Fabien Fournier (Orléans, 2028) | ½-½ |
| 3 | Antoine Robert (Orléans, 2009) | Damien Roux (Pithiviers, 1897) | 1-0 |
| 4 | Antoine Leroy (Pithiviers, 1836) | Inès Leroy (Orléans, 1904) | 0-1 |
| 5 | Alice Bernard (Orléans, 1871) | Inès Lefebvre (Pithiviers, 1829) | ½-½ |
| 6 | Bastien Laurent (Pithiviers, 1773) | Damien Michel (Orléans, 1839) | ½-½ |

Orléans l'emporte de justesse, avec deux victoires contre une. Les deux licenciées B d'Orléans
(Gaëlle Dubois et Chloé Simon) restent sur le banc. Les commentaires et le récit, entièrement
imaginaires, sont rédigés quand leurs slices existent, et montrés au porteur du projet avant d'être
écrits dans le jeu d'essais.

Hypothèses retenues, sauf avis contraire :

- l'auteur d'un commentaire ou d'un récit est un membre de l'un des deux clubs, sans rôle de
  capitaine ni de webmaster (hors backlog ; l'authentification est différée, ADR 0019) ;
- un commentaire par échiquier, ajouté après la saisie du résultat ; un récit par club et par
  rencontre ; ni l'un ni l'autre ne se modifie (YAGNI) ;
- ni écran de saisie, ni REST d'écriture, pour l'engagement, la rencontre, le commentaire et le
  récit : le jeu d'essais appelle les cas d'usage, et seules les consultations ont un écran ;
- la saisie des résultats ne contrôle ni la licence A ni l'écart d'Elo : ces règles sont celles de
  *Compose a team for a match*, pour les rondes à venir. Le jeu d'essais les respecte ;
- pas de « club par défaut » : Orléans est DEMO01. La notion viendra avec un écran « ma rencontre » ;
- seule la rencontre Orléans–Pithiviers est jouée. Les trois autres de la ronde 1 et le classement
  viennent plus tard, le classement les exigeant toutes ;
- le récit s'appelle `MatchReport` dans le code (à valider) ;
- la date du 11 octobre 2026 vient du calendrier fédéral et ne vit que dans le jeu d'essais, jamais
  dans le domaine (ADR 0029 à écrire : l'ADR 0018 vise les données de référence, pas sept dates
  d'événement ; la lecture juridique est au porteur du projet) ;
- chaque slice qui stocke des données a sa migration Flyway, validée par le porteur du projet, et
  les textes libres (commentaires, récits) ajoutent un traitement au registre des traitements.

### Create a competition

- **Intention** : l'administrateur crée la compétition d'une saison, avec son nom, la Ligue qui
  l'organise et le nombre d'échiquiers de ses équipes. Par exemple « Nationale 4 », organisée par
  la Ligue du Centre-Val de Loire, six échiquiers, pour la saison 2026-2027.
- **Décisions proposées** :
  - le formulaire demande le nom, la Ligue organisatrice et le nombre d'échiquiers : la saison est
    la saison courante (ADR 0025), et la division reste dans le nom. La Ligue n'est plus dans le
    nom, parce qu'elle est le lien avec la localisation des clubs (ADR 0027) ;
  - la Ligue organisatrice est une donnée de la compétition, nommée `organizingLeague` ; la
    Fédération et le comité attendront une compétition qui en a besoin (YAGNI) ;
  - la Ligue est d'abord désignée par son nom. Choisir sa Ligue dans une liste (les régions de
    l'INSEE) viendra avec *Find the league of a committee* ;
  - le nombre d'échiquiers est un entier strictement positif, refusé sinon. Livré : l'invariant
    vit dans l'agrégat `Competition`, qui lève `CompetitionNeedsAtLeastOneBoard`, et un second
    scénario le décrit ;
  - le nombre de rondes ne se déclare pas : il découle du calendrier (YAGNI) ;
  - le contexte a sa propre saison plutôt que d'emprunter celle de `clubmanagement` : une slice ne
    possède pas le modèle d'un autre contexte (ADR 0027).
- **Premier test**, en langage métier : quand l'administrateur crée la compétition « Nationale 4 »
  organisée par la Ligue du Centre-Val de Loire et jouée sur six échiquiers, il est informé que la
  compétition « Nationale 4 » a été créée pour la saison 2026-2027, organisée par cette Ligue et
  jouée sur six échiquiers. Un second scénario décrit le refus d'une compétition sans échiquier.
- **État** : livrée. Acceptation, cas d'usage, persistance, REST, contrat Pact et écran
  (`POST /api/competitions`, avec le vidage de la table au démarrage du profil dev, et l'écran
  `/competitions/new`), puis les règles ArchUnit du contexte et l'extension de PIT.
- **Couches** : acceptation, cas d'usage, persistance, REST, contrat et écran. L'écran est voulu
  pour cette première slice, qui pose aussi le squelette du contexte (package, migration, REST,
  contrat, règles ArchUnit) : le geste ne se fait qu'une fois par saison, donc le formulaire reste
  minimal.
- **Questions ouvertes** : deux compétitions de même nom, de même Ligue et de même saison ?
  L'effectif du groupe (8 ou 10 équipes) se déclare-t-il à la création, ou se constate-t-il aux
  engagements ? Une Ligue qui a plusieurs groupes de Nationale 4 : une compétition par groupe, ou
  une compétition par Ligue qui contient des groupes ?

### Consult the competitions of the season

- **Intention** : l'administrateur voit les compétitions de la saison courante, avec leur nom, la
  Ligue qui les organise et leur nombre d'échiquiers, sous le titre de la saison. Par exemple, pour
  2026-2027 : « Nationale 4 », Ligue du Centre-Val de Loire, six échiquiers.
- **Décisions proposées** :
  - la saison est la saison courante (ADR 0025). Elle est affichée en titre, même quand aucune
    compétition n'existe : la réponse porte la saison et la liste ;
  - seules les compétitions de la saison courante sont listées ; celles d'une autre saison existent
    dans la base, sans écran pour l'instant ;
  - la liste est triée par nom, puis par Ligue, sans tenir compte des accents ni des majuscules
    (comme les clubs d'un comité) : « Élite 1 » vient entre « départementale 2 » et « Nationale 4 ».
    Le cas d'usage trie, l'écran garde l'ordre reçu. Ni recherche, ni pagination, ni filtre (YAGNI,
    comme pour les membres d'un club) ;
  - la lecture passe par le port `CompetitionRepository` (une requête par saison) : la liste ne
    joint aucun autre agrégat, ce que vise l'ADR 0024 pour un port de lecture ;
  - la réponse ne porte pas d'identifiant de compétition : aucun écran n'en a besoin, les slices
    qui ouvrent une compétition l'ajouteront ;
  - la liste s'enrichira des équipes engagées et des rondes jouées au fil des slices suivantes.
- **Premiers tests**, en langage métier : quand l'administrateur consulte les compétitions de la
  saison 2026-2027 après la création de « Nationale 4 », organisée par la Ligue du Centre-Val de
  Loire et jouée sur six échiquiers, il voit la saison 2026-2027 et cette compétition. Une
  compétition d'une autre saison n'est pas listée. Sans compétition, il est informé qu'il n'y en a
  aucune pour la saison.
- **État** : livrée. Trois scénarios d'acceptation, cas d'usage (saison affichée, tri par nom puis
  Ligue), requête par saison dans le port et son adaptateur JDBC, REST (`GET /api/competitions`),
  contrat Pact (l'interaction énoncée par le front-end, l'état fournisseur « la compétition
  Nationale 4 de la Ligue du Centre-Val de Loire est créée pour la saison courante ») et écran
  (`/competitions`, lien « Compétitions »).
- **Couches** : acceptation, cas d'usage, persistance (requête par saison), REST
  (`GET /api/competitions`), contrat Pact et écran (`/competitions`, lien « Compétitions »).
- **Questions ouvertes** : l'écran de création mène-t-il à la liste une fois la compétition créée ?
  Faut-il voir les compétitions d'une saison passée ?

### Engage a team in a competition

- **Intention** : l'administrateur engage une équipe d'un club dans une compétition de la saison.
  Par exemple, le club « Les Cavaliers de la Loire » dans la Nationale 4 de 2026-2027 : son équipe 1.
- **Décisions proposées** :
  - `Competition` porte ses équipes engagées (ADR 0027) : l'engagement passe par l'agrégat. Une
    équipe est identifiée par son club et son numéro (3.7.b), et un club peut en engager plusieurs,
    même dans un groupe ;
  - le numéro est attribué, pas saisi : la première équipe d'un club dans la compétition reçoit 1,
    la suivante 2, dans l'ordre des engagements. Le A02 les range par force décroissante : changer
    l'ordre viendra avec un exemple qui le demande (YAGNI) ;
  - le numéro est propre à la compétition : le même club a l'équipe 1 de la Nationale 4 et l'équipe
    1 d'une autre compétition ;
  - le club est désigné par un identifiant propre au contexte (`ClubId` de `competition`), traduit
    à la frontière. L'existence du club n'est pas vérifiée aujourd'hui : personne n'engage à la
    main, et le jeu d'essais engage des clubs qu'il vient de créer. Le port et l'adaptateur
    anticorruption viendront avec le premier exemple qui l'exige : un club inconnu refusé, ou la
    lecture des joueurs du club par *Record the results of a match* (ADR 0027) ;
  - pas de contrôle de la Ligue du club avant *Find the league of a committee* ;
  - pas de limite à 8 ou 10 équipes : l'effectif se constate, il ne se déclare pas (YAGNI) ;
  - ni REST ni écran (23 octobre 2026) : le jeu d'essais appelle le cas d'usage, et engage les huit
    clubs de démonstration dans la Nationale 4.
- **Premiers tests**, en langage métier : quand l'administrateur engage une équipe du club « Les
  Cavaliers de la Loire » dans la compétition « Nationale 4 », le club a l'équipe 1 engagée dans
  cette compétition. Un second scénario : quand il engage une seconde équipe du même club, le club
  a les équipes 1 et 2.
- **État** : en cours. Livrés : le scénario d'acceptation (dans la porte de build), le domaine
  (`Competition.engageTeamOf`, numérotation par club) et le cas d'usage `EngageTeam`, sur le dépôt
  en mémoire. Restent la persistance, dont l'adaptateur JDBC devra mettre à jour une compétition
  existante (son `save` ne sait qu'insérer), et le jeu d'essais.
- **Couches** : acceptation, cas d'usage, domaine (`Competition` et ses équipes), persistance (une
  table des équipes engagées, avec sa migration Flyway à valider) et jeu d'essais de développement.
  Ni REST, ni contrat, ni écran.
- **Questions ouvertes** : engager deux fois la même équipe est-il une erreur ? Une équipe se
  retire-t-elle (forfait général) ? Le numéro est-il propre à la compétition, ou au club pour toute
  la saison ? Les Ligues sont-elles exactement les régions de l'INSEE, pour le contrôle à venir ?

### Les slices suivantes

Détaillées quand on les démarrera ; ce qui suit garde les questions déjà constatées.

- *Schedule a match* : l'administrateur fixe une rencontre (ronde, date, équipe qui reçoit, équipe
  visiteuse). Deux équipes engagées et distinctes ; une équipe ne joue qu'une rencontre par ronde.
  Les dates viennent du calendrier fédéral et les appariements du directeur de groupe. Ni REST ni
  écran ; pour la démonstration, une seule rencontre : la ronde 1, le 11 octobre 2026, Orléans reçoit
  Pithiviers. Questions : saisir à la main les 28 rencontres (huit équipes, sept rondes, quatre
  rencontres par ronde), ou générer le calendrier, ce qui n'est pas demandé ? L'heure de début,
  14h15 par défaut, compte-t-elle pour le délai de remise des compositions ?
- *Compose a team for a match*, le cœur : le capitaine place des joueurs de son club aux
  échiquiers de son équipe. Trois cycles : le cas nominal (membres du club, un seul échiquier par
  joueur, sans trou, couleurs dérivées), puis la licence A seule (les données de développement ont
  deux licenciés B par club), puis l'écart d'Elo (100 accepté, 101 refusé, paire non voisine).
  À trancher : une composition contraire aux règles est-elle refusée (recommandé) ou seulement
  signalée ? Une équipe incomplète est-elle permise (le A02 n'interdit que les trous, 3.6.a) ? La
  composition fige-t-elle l'Elo et la licence au moment où elle est faite ? Jusqu'à quand se
  modifie-t-elle ? Un joueur non classé reçoit un Elo estimé selon sa catégorie d'âge (R01 5) :
  faut-il stocker la date de naissance, ou saisir l'Elo estimé ? Les autres règles du A02 sur la
  composition sont à examiner chacune avec un exemple avant d'être codées (YAGNI) : les mutés
  (3.7.g, la mutation n'a pas encore d'opération propre dans Club Management), la nationalité
  (3.7.h), l'Elo supérieur à 2400 (3.7.j), un joueur dans une seule équipe d'un groupe (3.7.d) et
  le nombre de parties déjà jouées (3.7.e).
- *Print the match sheet* : la feuille d'une rencontre, prête à imprimer, avec les échiquiers dans
  l'ordre, les couleurs, le nom, l'identifiant FFE et l'Elo des joueurs des deux équipes, et la
  place des résultats et des signatures. Lecture seule, impression par le navigateur. Question :
  elle ne remplace pas le document officiel exigé par le A02 (3.6.a) ; à quoi sert-elle alors ?
- *Record the results of a match* : pour chaque échiquier, les deux joueurs, identifiés par leur
  identifiant FFE, leur couleur, qui découle de l'échiquier, et le résultat ; le score de la
  rencontre en découle selon le barème retenu plus haut. Aucune composition préalable, et aucun
  contrôle de la licence A ni de l'écart d'Elo : ces règles sont celles de *Compose a team for a
  match*, pour les rondes à venir. Le procès-verbal officiel est saisi sur le site fédéral par le
  club responsable de la rencontre, le club recevant sauf décision contraire (2.5, 3.11.a) : la
  slice garde la mémoire du club et accepte la double saisie. Ni REST ni écran de saisie. Les
  joueurs viennent des membres du club : c'est le premier usage du port et de l'adaptateur
  anticorruption de l'ADR 0027, qui change la règle d'architecture « `competition` ne dépend pas de
  `clubmanagement` » en « seul cet adaptateur en dépend ». Questions : le nom et l'Elo sont-ils
  figés au moment de la saisie ? Une saisie se corrige-t-elle ?
- *Consult the calendar of a competition* : les rondes de la saison avec leur date et leurs
  rencontres, la ronde jouée distinguée de celle qui reste à jouer. Pas d'horloge : une ronde est
  jouée quand les résultats de ses rencontres sont saisis. REST, contrat et écran. Question : une
  ronde dont une partie seulement est saisie est-elle jouée ?
- *Consult a match* : l'écran d'une rencontre, atteint depuis le calendrier : les deux équipes, la
  ronde et la date, puis, pour chaque échiquier, les joueurs (nom, Elo, couleur) et le résultat.
  REST, contrat et écran. Il s'enrichit des commentaires et des récits.
- *Comment a board of a match* : un membre de l'un des deux clubs laisse un commentaire sur un
  échiquier dont le résultat est saisi. Un commentaire par échiquier, sans modification (YAGNI),
  affiché sur l'écran de la rencontre ; ni écran ni REST d'écriture. L'auteur est un membre du club,
  sans rôle. Questions : un joueur peut-il commenter la partie de l'adversaire ? Un commentaire se
  supprime-t-il ? Le texte libre ajoute un traitement au registre des traitements.
- *Write the report of a match* : le récit de la rencontre (`MatchReport`), écrit par un membre
  d'un des deux clubs, qui le raconte à sa façon : un récit par club et par rencontre, sans
  modification, affiché sous les échiquiers ; ni écran ni REST d'écriture, pas d'éditeur de texte
  riche (YAGNI). Questions : les deux clubs écrivent-ils chacun le leur ? Le récit d'un club est-il
  visible de l'autre ? Même traitement au registre que les commentaires.
- *Consult the standings of a competition* : le classement, au total des points de match (3, 2, 1),
  puis au résultat entre les équipes à égalité, au différentiel et aux points « pour » (A02 4.4),
  une nulle ne comptant pas dans le score d'une partie. Il n'est calculé que sur les rencontres
  dont les résultats sont saisis.

Hors backlog, tant qu'aucun cas d'usage ne le demande (YAGNI) : la génération du calendrier, les
forfaits et pénalités, le capitaine comme concept, les coups des parties, les autres divisions et
les écrans de saisie de l'engagement, de la rencontre, du commentaire et du récit.

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
