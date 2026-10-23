# 27. Compétition, un bounded context distinct de la gestion de club

Date : 2026-10-23

## Statut

Proposée

## Contexte

Le thème *Compétitions* est resté indivis dans `docs/slices.md` tant qu'aucun cas d'usage n'était
demandé. Il l'est maintenant : le championnat interclubs de Nationale 4 du Loiret. Les clubs
créés dans l'application y engagent chacun une équipe ; huit équipes jouent sept rondes, sur six
échiquiers. Un calendrier fixe les rencontres en début de saison. Avant chaque ronde, le capitaine
compose son équipe et peut imprimer la feuille de match ; après la ronde, il saisit les résultats.

Ce que l'on sait :

- les règles viennent du règlement fédéral du Championnat de France des clubs, que les ligues
  complètent. Le seul texte officiel lisible date de 2009 (équipes de licenciés A, couleurs fixées
  par le calendrier, règle des 103 points sur l'ordre des Elo) ; la version en vigueur reste à
  lire ;
- les six échiquiers de la Nationale 4 du Loiret nous ont été indiqués par le porteur du projet.
  Les règlements publiés d'autres ligues parlent de huit joueurs, et aucun règlement local n'a été
  trouvé : le nombre d'échiquiers dépend de la compétition.

Ce que l'on ignore : l'Elo à retenir quand la FFE et la FIDE en publient deux, le traitement d'un
joueur non classé, le barème des points de match, qui saisit les résultats, et si le Loiret limite
les joueurs mutés.

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
clubs. Sa première compétition est la Nationale 4 du Loiret.

- **Modèle, hypothèses à éprouver par les exemples** : une `Competition` porte sa saison, son
  nombre d'échiquiers et ses équipes engagées, une seule par club. Un `Match` (une rencontre) porte
  sa ronde, l'équipe qui reçoit, l'équipe visiteuse, leurs compositions et leurs résultats. Les
  agrégats se référencent par identifiant. Deux capitaines écrivent des rencontres différentes sans
  se gêner, ce qu'un agrégat unique n'autoriserait pas.
- **Règles dans le domaine** : licence A seule, ordre des Elo (règle des 103 points) et couleurs
  par échiquier sont portés par le domaine de Compétition, qui refuse avec un message métier. Elles
  ne vivent jamais dans une requête, un contrôleur ou un adaptateur.
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
- Les règles reposent sur un texte de 2009 et sur l'indication du porteur du projet. Chacune reçoit
  un exemple avant d'être codée, et les questions ouvertes sont tenues dans `docs/slices.md`.
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
