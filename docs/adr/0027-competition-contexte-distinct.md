# 27. Compétition, un bounded context distinct de la gestion de club

Date : 2026-10-23

## Statut

Proposée

## Contexte

Le thème *Compétitions* est resté indivis dans `docs/slices.md` tant qu'aucun cas d'usage n'était
demandé. Il l'est maintenant : le championnat interclubs de Nationale 4 de la Ligue du
Centre-Val de Loire, dont le Loiret est l'un des six comités. Les clubs créés dans l'application y
engagent chacun une équipe ; huit équipes jouent sept rondes, sur six échiquiers. Un calendrier
fixe les rencontres en début de saison. Avant chaque ronde, le capitaine compose son équipe et peut
imprimer la feuille de match ; après la ronde, il saisit les résultats.

Ce que l'on sait, d'après le règlement fédéral du Championnat de France des clubs (A02) et les
règles générales (R01), saison 2026-2027, dont les textes ont été lus :

- la Top 16, la Nationale 1, 2 et 3 sont organisées par la Commission Technique Fédérale, qui
  forme leurs groupes géographiquement ; les divisions inférieures sont organisées par les Ligues
  ou les comités départementaux ;
- la Nationale 4 se joue en groupes de 8 ou 10 équipes, en toutes rondes (sept ou neuf rondes),
  sous la responsabilité des Ligues, qui fixent le nombre de leurs groupes, les composent, nomment
  les directeurs de groupe et peuvent y apporter des modifications mineures. Elle est nationale par
  sa place dans la pyramide, mais organisée par une Ligue ;
- une Ligue regroupe plusieurs comités départementaux : celle du Centre-Val de Loire compte le
  Cher, l'Eure-et-Loir, l'Indre, l'Indre-et-Loire, le Loir-et-Cher et le Loiret ;
- une équipe compte huit licenciés A, et les Ligues peuvent choisir une Nationale 4 à six
  échiquiers : le porteur du projet nous a indiqué que c'est le cas du Loiret, mais aucun
  règlement local n'a été trouvé. Le nombre d'échiquiers dépend donc de la compétition ;
- l'équipe première nommée dans les appariements a les blancs aux échiquiers impairs ;
- l'Elo à prendre en compte est le dernier publié, et quand deux membres d'une équipe ont des Elo
  qui diffèrent de plus de 100 points, le mieux classé joue devant ;
- un club peut engager plusieurs équipes, même dans un même groupe : elles sont numérotées par
  force décroissante, et un joueur ne joue que dans une des équipes d'un groupe ;
- seules les victoires comptent dans le score d'une partie (une nulle n'est pas comptée) ; un
  match gagné vaut 3 points, un match nul 2, un match perdu 1 ;
- le club responsable de la rencontre saisit le procès-verbal sur le site fédéral ;
- un joueur jamais classé reçoit un Elo estimé selon sa catégorie d'âge (R01, article 5), et
  chaque équipe a un capitaine licencié (R01, article 8) ; à défaut du capitaine, un membre de
  l'équipe peut remettre la composition (A02, 3.6.a).

Ce que l'on ignore : si la Ligue du Centre-Val de Loire adapte ces règles ; si un club d'une autre
Ligue peut jouer dans une Nationale 4 ou une compétition régionale (aucun texte lu ne l'autorise
ni ne l'interdit, et la pratique est de rester dans sa Ligue) ; si les Ligues coïncident
exactement avec les régions de l'INSEE ; et qui saisit les résultats dans l'application, puisque
le procès-verbal officiel se saisit sur le site fédéral.

Le langage, les invariants et le rythme divergent de ceux de la gestion de club :

- **Langage** : équipe engagée, rencontre, ronde, échiquier, composition, feuille de match, là où
  la gestion de club parle de club, de personne, de licence et de relation avec un club ;
- **Invariants** : une composition ne contient que des licenciés A et respecte l'ordre des Elo ;
  une personne licenciée a un identifiant FFE ;
- **Rythme** : une compétition suit un calendrier fédéral, ronde après ronde ; la gestion de club
  suit la saison.

## Décision

Nous créons un bounded context `competition`, package de premier niveau à côté de
`clubmanagement` (voir [0002](0002-monolithe-modulaire.md)), pour les compétitions par équipes de
clubs. Sa première compétition est la Nationale 4 de la Ligue du Centre-Val de Loire.

- **Organisateur** : une compétition est organisée par la Fédération (Top 16 à Nationale 3), par
  une Ligue (Nationale 4, divisions régionales) ou par un comité départemental. L'organisateur
  décide qui peut y jouer : une compétition fédérale accueille des clubs de toute Ligue, une
  compétition de Ligue ceux de cette Ligue, selon la pratique. Seule la Ligue est modélisée pour
  l'instant, sous le nom `organizingLeague` ; la Fédération et le comité attendront une
  compétition qui en a besoin. La Ligue d'un club se déduit de son comité (club, comité, Ligue) ;
  nous envisageons d'identifier les Ligues par les régions de l'INSEE, donnée ouverte déjà dans le
  dépôt, plutôt que par une liste de la FFE ([0018](0018-pas-de-reutilisation-des-donnees-ffe.md)).
  L'équivalence reste à confirmer et fera l'objet d'un ADR.
- **Modèle, hypothèses à éprouver par les exemples** : une `Competition` porte sa saison, son
  nombre d'échiquiers, sa Ligue organisatrice et ses équipes engagées. Une équipe est identifiée
  par son club et son numéro, qui range les équipes du club par force décroissante ; un club peut
  en engager plusieurs, même dans un groupe. Un `Match` (une rencontre) porte sa ronde, l'équipe
  qui reçoit, l'équipe visiteuse, leurs compositions et leurs résultats. Les agrégats se
  référencent par identifiant. Deux capitaines écrivent des rencontres différentes sans se gêner,
  ce qu'un agrégat unique n'autoriserait pas.
- **Règles dans le domaine** : licence A seule, ordre des Elo (écart de plus de 100 points) et
  couleurs par échiquier sont portés par le domaine de Compétition, qui refuse avec un message
  métier. Elles ne vivent jamais dans une requête, un contrôleur ou un adaptateur.
- **Relation avec Club Management** : Compétition est en aval. Son port donne, pour un club et une
  saison, les joueurs qui peuvent être alignés : nom, prénom, identifiant FFE, type de licence, Elo
  facultatif. Un adaptateur anticorruption traduit ce que fournit `ClubMembers`. Seul cet
  adaptateur dépend de `clubmanagement`, et `clubmanagement` ne dépend jamais de `competition`. Un
  joueur est identifié par son identifiant FFE ([0015](0015-identifiant-ffe-de-la-personne.md)),
  pas par son `PersonId` ; un club l'est par son identifiant, traduit à la frontière.
- **Données** : la même base PostgreSQL, JDBC et Flyway
  ([0006](0006-postgresql-et-jdbc-sans-jpa.md), [0007](0007-flyway-pour-le-schema.md)), avec des
  tables propres au contexte et aucune clé étrangère vers celles de `clubmanagement`. L'existence
  d'un club est vérifiée par le port.
- **Données FFE** : les règles sont décrites avec nos mots, sans recopier le texte fédéral ; les
  données de développement restent fictives ([0018](0018-pas-de-reutilisation-des-donnees-ffe.md)).
- **Hors périmètre** : le capitaine comme concept (l'authentification est différée, voir
  [0019](0019-authentification-differee.md)), les coups des parties, le classement, la génération
  du calendrier et les forfaits. Aucun n'est modélisé tant qu'un cas d'usage ne le demande pas.

## Conséquences

- `architecture.ArchitectureTests` n'importe aujourd'hui que `clubmanagement`. La première slice du
  contexte étend les règles hexagonales et l'indépendance des slices à `competition`, et ajoute
  deux règles : `clubmanagement` ne dépend pas de `competition`, et dans `competition` seul
  l'adaptateur anticorruption dépend de `clubmanagement`.
- `ClubMembers` et `ClubMember` deviennent de fait une interface publiée : les faire évoluer touche
  l'adaptateur de Compétition.
- Compétition a son propre joueur (nom, identifiant FFE, Elo, licence) : cette duplication est
  assumée tant qu'un exemple ne montre pas un sens commun.
- Le nombre d'échiquiers étant une donnée de la compétition, une compétition à huit échiquiers ne
  demandera aucun changement de code.
- Les compositions stockées contiennent des noms, des identifiants FFE et des Elo : la slice qui
  les stocke ajoute un traitement à `docs/registre-des-traitements.md`.
- Les règles reposent sur le A02 et le R01 de la saison 2026-2027 et sur l'indication du porteur
  du projet. Chacune reçoit un exemple avant d'être codée, et les questions ouvertes sont tenues
  dans `docs/slices.md`.
- Le seuil d'écart d'Elo a changé avec les saisons (103 points dans le texte de 2009, 100 en
  2025-2026 et 2026-2027) : il s'écrit à un seul endroit du domaine.
- Contrôler qu'un club joue dans la compétition de sa Ligue demandera la Ligue du club : une slice
  préalable de la gestion de club, *Find the league of a committee*, la calculera depuis le comité.
- L'estimation de l'Elo d'un joueur jamais classé demandera sa catégorie d'âge, donc sa date de
  naissance, que l'application ne stocke pas.
- Le bounded context des parties (transcription des coups) n'est pas créé : une rencontre ne
  connaît que le résultat de chaque échiquier.

## Options écartées

- **Garder la compétition dans `clubmanagement`** : le package deviendrait « tout ce qui touche à
  la FFE », avec un langage et des invariants qui divergent (voir
  [0002](0002-monolithe-modulaire.md)).
- **Un Shared Kernel avec Club Management** (partager `Person` ou `ClubMember`) : lierait les
  cycles de vie des deux contextes et empêcherait de les extraire (même raison que pour
  [0016](0016-craft-calendar-contexte-generique.md)).
- **Conformist** (utiliser `ClubMember` tel quel dans le domaine de Compétition) : le vocabulaire de
  la licence et de l'adhésion se mêlerait à celui des compositions.
- **Un seul agrégat `Competition` contenant les rencontres** : un agrégat trop gros, modifié en
  même temps par tous les capitaines.
- **Une clé étrangère vers `club`** : simple, mais elle couple les schémas et reproduit le défaut
  déjà constaté pour un club inconnu, qui échoue par une erreur générique de la base.
- **Modéliser le capitaine dès maintenant** : rien ne vérifie encore qui agit (voir
  [0019](0019-authentification-differee.md)), et aucune règle ne s'appuie sur lui.
- **Mettre la Ligue dans le nom de la compétition** : la Ligue n'est pas une option libre, c'est le
  lien avec la localisation des clubs. Un nom ne permettrait pas de contrôler que le club joue dans
  la compétition de sa Ligue.
- **Un niveau (national, régional) séparé de l'organisateur** : la Nationale 4 est nationale par sa
  place et organisée par une Ligue, donc le niveau ne dit pas qui peut jouer, et l'organisateur
  suffit.
