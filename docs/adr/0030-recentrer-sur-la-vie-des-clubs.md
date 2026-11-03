# 30. Recentrer ChessManager sur la vie des clubs d'échecs avec Orléans comme club pilote

Date : 2026-11-01

## Statut

Acceptée. Précise [0028](0028-retour-a-la-vie-du-club.md) : la vie du club reste la priorité, mais
son cœur devient le récit et le partage ; les compétitions passent en soutien.

## Contexte

L'ADR [0028](0028-retour-a-la-vie-du-club.md) (23 octobre 2026) a mis la vie du club et les
compétitions par équipes en priorité, avec la Nationale 4 du Loiret pour premier besoin. Depuis,
l'auteur a précisé ce que ChessManager doit apporter aux clubs : raconter leurs rencontres,
publier leurs comptes rendus et partager les parties commentées de leurs joueurs. Le premier club
est Orléans ; d'autres suivront.

### Le problème

Le périmètre écrit dans le README, dans `docs/slices.md` et dans l'ADR
[0027](0027-competition-contexte-distinct.md) met au centre la composition des équipes, la feuille
de match et la saisie des résultats. Ce serait reconstruire une partie de la FFE : le club qui
reçoit saisit déjà le procès-verbal sur le site fédéral, et les données fédérales ne sont pas
réutilisables sans autorisation écrite ([0018](0018-pas-de-reutilisation-des-donnees-ffe.md)). Le
spike [`docs/spikes/ffe-data.md`](../spikes/ffe-data.md) n'a trouvé aucune interface documentée à
l'usage des tiers. La valeur propre de ChessManager est ailleurs, et l'auteur a décidé de ne pas
s'arrêter aux contraintes fédérales.

### Les forces en présence

- un club pilote réel à servir vite, et d'autres clubs à accueillir sans réécriture ;
- un seul développeur : peu de contextes ([0002](0002-monolithe-modulaire.md)) ;
- du code livré à préserver : la gestion de club (trois agrégats, voir
  [0014](0014-trois-agregats-de-la-gestion-de-club.md)), la création et la consultation des
  compétitions, et l'engagement d'une équipe (PR #30, ouverte) ;
- Lichess fait déjà très bien la saisie et l'analyse des parties ; un viewer PGN complet existe,
  mais sous GPL-3.0, alors que le dépôt est sous MIT ;
- l'authentification est différée ([0019](0019-authentification-differee.md)), alors qu'un site
  pour de vrais utilisateurs la demandera ;
- des récits qui nomment des personnes demandent de respecter leurs droits et ceux des contenus.

### Les options

- **A** : poursuivre le développement d'un gestionnaire de compétitions ;
- **B** : une plateforme multi-club centrée sur la vie associative, avec Orléans pour pilote ;
- **C** : un site dédié à Orléans seulement.

## Décision

Nous retenons l'option B. Nous inscrivons ce principe :

> ChessManager est une plateforme indépendante dédiée à la vie associative des clubs d'échecs. Les
> clubs sont responsables des contenus qu'ils produisent et publient. La création de rencontres, la
> rédaction de récits, le partage de parties et la publication des activités du club ne dépendent
> d'aucun service fédéral. Une intégration FFE pourra être étudiée ultérieurement, sans conditionner
> le fonctionnement du produit.

1. **Le cœur du produit.** Deux sous-domaines Core : *Club Life* (récits, actualités, activités du
   club, validation éditoriale) et *Chess Experience Sharing* (les parties commentées, liées aux
   rencontres). Les autres sont de soutien ou génériques ; la carte complète est dans
   [`docs/carte-du-domaine.md`](../carte-du-domaine.md).
2. **Un seul nouveau contexte, `clublife`**, pour ces deux sous-domaines : ils partagent les mêmes
   invariants (un contenu appartient à un club, un webmaster le valide). Séparer « publication » et
   « expérience de partie » serait artificiel aujourd'hui. Il est créé par la première slice qui
   en a besoin, pas dans la PR qui consigne cette décision.
3. **Les contextes existants restent.** `clubmanagement` est inchangé ; il recevra la désignation
   du webmaster. `competition` est conservé tel que livré, sans nouvelle slice. `clublife` ne
   dépend pas de `competition` : le club crée lui-même ses rencontres.
4. **Rien de fédéral.** Pas de contexte, pas d'adaptateur, même désactivé, pas de configuration,
   pas de démarche. Les identifiants FFE ([0015](0015-identifiant-ffe-de-la-personne.md)) restent
   des références facultatives, et 0018 reste valable. Le spike FFE est conservé comme référence
   historique. Une intégration éventuelle fera l'objet d'un nouvel ADR.
5. **Chess Moves n'est pas un contexte.** Le PGN est un texte, conservé tel que reçu dans une
   contribution (provisoirement `GameContribution`), jamais un agrégat par coup. Le serveur ne
   l'interprète pas en V1 ; rejouer la partie est une brique de présentation du front-end. Le
   chantier `chessmoves` (branche `feat/replay-game-from-pgn`, non fusionnée, ADR 0029 en
   préparation) n'est pas poursuivi comme contexte. S'il était fusionné, le présent ADR remplacerait
   0029 sur ce point. Sa lecture de PGN côté serveur, derrière une couche anticorruption, pourra
   revenir dans `clublife` si un besoin réel apparaît.
6. **Craft Calendar reste un contexte autonome et générique** ([0016](0016-craft-calendar-contexte-generique.md)),
   toujours non créé : aucune slice de la vie du club n'a besoin de récurrences ni de rappels. Trois
   choses restent distinctes : le calendrier officiel d'une compétition (une donnée de
   `competition`), les activités internes d'un club (les horaires d'ouverture, par le port
   `ClubCalendar`) et les capacités génériques de calendrier (Craft Calendar). Les événements datés
   du club pourront un jour l'appeler.
7. **Lichess est l'outil de saisie**, ChessManager ne le reproduit pas. L'import se fait par collage
   du PGN d'abord, par adresse de l'étude ensuite ; l'association d'un compte attend un besoin réel
   ([`docs/spikes/lichess.md`](../spikes/lichess.md)).
8. **Les rôles sont toujours relatifs à un club** : l'administrateur de la plateforme (crée les
   clubs, nomme leur webmaster), le webmaster (crée les rencontres, publie, valide), le joueur
   membre (propose ses parties) et le visiteur (lit). Un webmaster d'Orléans ne peut rien sur les
   contenus d'un autre club.
9. **La licence passe de MIT à AGPL-3.0**, pour pouvoir utiliser le viewer PGN de Lichess, sous
   GPL-3.0 ([`docs/spikes/pgn-viewer.md`](../spikes/pgn-viewer.md)). Cet ADR ne la change pas
   lui-même : voir [0031](0031-passer-sous-licence-agpl.md).

## Conséquences

**Ce qui devient plus facile.** Des slices claires pour le club pilote ; aucune dépendance à un
service que nous ne maîtrisons pas ; le multi-club dès la conception, sans multi-tenant technique :
une application, une base, chaque contenu porte son club.

**Ce qui devient plus difficile, ou à surveiller.**

- l'authentification devient nécessaire avant d'ouvrir le site aux utilisateurs d'Orléans : 0019
  garde son principe, mais son échéance se rapproche ;
- sans import, le club saisit à la main ses rencontres, qu'il a déjà saisies sur le site fédéral :
  une double saisie, à surveiller ;
- les récits et les PGN nomment des personnes, dont des adversaires : chaque slice qui les stocke
  ajoute un traitement à `docs/registre-des-traitements.md` ;
- les futures dépendances devront être compatibles avec l'AGPL-3.0 ([0031](0031-passer-sous-licence-agpl.md)) ;
- une partie du code de `competition` risque de rester peu utilisée.

### Impacts sur les contextes

| Contexte | Avant | Après |
|---|---|---|
| `clubmanagement` | soutien, livré | inchangé ; accueille la désignation du webmaster ; reste en amont |
| `competition` | domaine du produit (0027, 0028) | soutien, gelé : le livré reste, aucune nouvelle slice |
| `clublife` | n'existe pas | nouveau, Core, créé par la deuxième slice du backlog |
| `craftcalendar` | planifié | inchangé |
| `chessmoves` | en préparation hors de `main` | non créé |

### Stratégie de migration

1. Cette PR : des documents seulement. Aucun code, aucune migration, contrat Pact inchangé.
2. Première slice, dans `clubmanagement` : nommer un webmaster.
3. Deuxième slice : création du package `clublife` et extension des règles ArchUnit (indépendance
   des contextes, seul l'adaptateur vers `clubmanagement` en dépend).
4. Les branches en cours restent à l'auteur : la PR #30 ne touche pas Club Life ; la branche
   `feat/create-competition` contient la slice *Tell the story of a game* dans Competition, à
   reprendre dans Club Life plutôt qu'à fusionner telle quelle ; `chessmoves` n'est pas fusionné
   comme contexte.

### Sort des ADR précédents

| ADR | Sort |
|---|---|
| 0001 à 0008, 0010 à 0015, 0017, 0021 à 0025 | conservés |
| 0009 | conservé, précisé : un viewer d'affichage n'est pas une règle métier |
| 0016 | conservé : Craft Calendar reste autonome, non créé |
| 0018 | conservé, renforcé : la V1 n'utilise aucune donnée fédérale |
| 0019 | conservé, avec une échéance : avant l'ouverture aux utilisateurs d'Orléans |
| 0020 | inchangé (déjà remplacé par 0028) |
| 0027 | conservé pour le contexte `competition` ; son périmètre futur est gelé |
| 0028 | précisé : le cœur est de raconter et partager ; les compétitions passent en soutien |

L'historique n'est pas réécrit : seul le statut de 0028 annonce la précision.

## Options écartées

- **A. Poursuivre le gestionnaire de compétitions** : reproduit des fonctions que la FFE assure déjà,
  repose sur des données qu'on ne peut pas reprendre sans autorisation, et apporte peu au club en
  dehors de la composition d'équipe.
- **C. Un site dédié à Orléans** : plus rapide au départ, mais d'autres clubs sont prévus ; il
  faudrait réécrire. Le coût du multi-club maintenant est faible : l'identité du club dans chaque
  contenu, et des droits relatifs à un club.
- **Deux contextes, publication et expérience de partie** : mêmes invariants, même cycle de vie ;
  ce serait un découpage spéculatif.
- **Chess Moves comme contexte** : le langage d'une partie jouée (coups, positions) n'est pas le
  nôtre. Nous conservons un PGN, Lichess et un viewer font le reste.
- **Un adaptateur FFE désactivé, « pour plus tard »** : de la configuration et du code que rien
  n'utilise.

## Questions ouvertes

1. **Authentification** : quel fournisseur, et à quelle slice ?
2. **Webmaster** : un seul par club ? Qui le retire ? Doit-il être membre ? Le capitaine peut-il
   créer les rencontres ? (V1 : le webmaster seulement.)
3. **Nom de la rencontre** dans le code : `Match` est déjà pris par `competition`.
4. **Noms des adversaires** dans les PGN et les récits publiés : règle de publication.
5. **Modération** par l'administrateur de la plateforme.
6. **Sort des branches** `chessmoves` et `feat/create-competition`.
