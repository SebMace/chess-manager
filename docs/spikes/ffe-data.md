# Spike FFE : ce que le plugin SPIP « Data FFE » dit du service fédéral

Date : 2026-11-01. Spike de la PR #31 (pivot vers la vie des clubs).

**Statut : investigation terminée, conservée comme référence historique.** L'auteur a décidé que la
V1 ne dépend d'aucun service fédéral : ni appel, ni extraction, ni adaptateur, ni démarche auprès de
la FFE ([0030](../adr/0030-recentrer-sur-la-vie-des-clubs.md)). Les sections 5 et 6
(architecture et questions) disent ce qu'on ferait si une intégration était un jour étudiée ; elles
ne sont pas un plan.

Ce document constate, il ne décide rien : les décisions iront dans l'ADR du pivot. Il ne contient
aucune donnée de la FFE ([0018](../adr/0018-pas-de-reutilisation-des-donnees-ffe.md)), seulement la
description d'une interface relevée dans le code public d'un tiers, et le sens que nous donnons à
des textes publics, avec nos mots. Rien n'y est un avis juridique.

## 1. Ce qui a été fait, et ce qui ne l'a pas été

Fait, en lecture seule :

- le dépôt Git du plugin a été cloné et lu fichier par fichier, sans jamais être exécuté : 40
  fichiers, 80 commits entre novembre 2015 et février 2026 ;
- la fiche du plugin sur plugins.spip.net et sa page de documentation sur ressources-echecs.net ;
- deux PDF publics du site echecs.asso.fr : les mentions légales (4 pages) et la politique de
  protection des données (13 pages). Ce sont des documents du site, pas le service.

Pas fait :

- **aucun appel à `service.echecs.asso.fr`** ni à un autre service de la FFE. Rien de ce qui suit
  n'a été confronté à une réponse réelle : le format et les champs sont ceux que le code du plugin
  suppose ;
- aucune demande à la FFE ;
- aucune relecture par un juriste.

| Source | Apport | Version, date |
|---|---|---|
| Dépôt `spip-contrib-extensions/ffedata` | le code | 1.0.1, dernier commit 2026-02-07, GPL, SPIP 4.1 à 4.4 |
| Fiche sur plugins.spip.net | licence, dépendances | la 0.8.5 est marquée obsolète |
| Documentation de l'auteur | usage des modèles | ne donne ni hôte, ni autorisation, ni limite ; l'auteur prévoit que le plugin devienne obsolète si le système d'information de la FFE est modernisé |
| Mentions légales (PDF) | conditions d'utilisation du site | date non indiquée ; cite des textes anciens et Internet Explorer |
| Politique de protection des données (PDF) | usages annoncés des données | date non indiquée |

La licence GPL du plugin concerne son code, pas les données qu'il affiche. Aucune ligne du plugin
n'est reprise ici ni ne doit l'être dans ce dépôt (la licence MIT d'alors a depuis laissé la place
à l'AGPL, mais le plugin n'est pas un sujet de ce projet).

## 2. Le service vu par le plugin

### 2.1 Protocole et format

- **Protocole** : un simple GET HTTP sur des adresses de la forme
  `http://service.echecs.asso.fr/<Service>.asmx/<Méthode>?<paramètres>`. L'hôte n'est pas celui du
  site (`www.echecs.asso.fr`). L'adresse est en `http`, jamais en `https`, dans tout le code.
- **Technologie** : `.asmx` est la marque des services web ASP.NET, une technologie Microsoft
  ancienne. Le plugin n'envoie aucune enveloppe SOAP : il appelle la méthode directement par son
  adresse, ce qui est la liaison « HTTP GET » d'ASMX (déduit de la forme des adresses).
- **Format de la réponse** : de l'XML. Le lecteur du plugin traite un `DataSet` .NET sérialisé en
  « diffgram » (espace de noms `urn:schemas-microsoft-com:xml-diffgram-v1`, racine `NewDataSet`).
  Chaque ligne est un élément dont les enfants sont les champs. Deux pièges que le code contourne :
  une réponse à une seule ligne n'est pas une liste, et un champ vide est absent de la réponse
  (absence et chaîne vide ne se distinguent pas).
- **Ce que le plugin ne fait pas** : il ne règle ni en-tête, ni identification du client, ni délai
  d'attente, ni nouvelle tentative. Le comportement par défaut du mécanisme de lecture de SPIP
  s'applique, et nous ne l'avons pas examiné. Aucun gabarit ne fixe de durée de cache pour les
  appels au service (le seul réglage de cache du plugin porte sur un tableau de réponses de
  formulaire).

### 2.2 Méthodes, paramètres et ce que le plugin en lit

Trois numéros circulent sous le même nom `Ref` : celui d'un département, celui d'un club, celui
d'un groupe de compétition. Le readme du plugin prévient d'ailleurs que l'écriture de `Ref` varie
(majuscule ou non) d'un exemple à l'autre.

| Méthode | Paramètres | Données lues | Sens de `Ref` |
|---|---|---|---|
| `Clubs.asmx/GetClubs` | `Ref` | par club : numéro interne, numéro FFE, nom, comité, ligue, commune, adresse, code postal, téléphone, télécopie, courriel, site, ouverture et jours ouverts, adresse et code postal de la salle de jeu, nombre de licenciés A et B, divisions adulte, jeune et féminine, nom, prénom et courriel du président et du correspondant | numéro de département (exemples du readme : 31, 32, 13) |
| `Joueurs.asmx/GetJoueurs` | `Ref` | par joueur du club : numéro interne, prénom, nom, courriel, catégorie, Elo, Rapide | numéro interne du club (celui de la fiche du club sur le site) |
| `Joueurs.asmx/GetJoueur` | `Ref` | nom, prénom, numéro FFE, type de licence, actif, titre FIDE, sexe, Elo, Rapide, catégorie, numéro FIDE, courriel, fédération, code FIDE, muté, club (numéro interne, code, nom) ; un commentaire du code liste aussi une clé `NeLe` | numéro interne du joueur, **distinct du numéro de licence** |
| `Joueurs.asmx/GetHisto` | `Ref` | Elo, FIDE, nombre de parties, date d'archive (le readme dit que ce n'est plus mis à jour depuis la généralisation du classement FIDE) | numéro interne du joueur |
| `Ligues.asmx/GetLigue` | `Abreviation` | coordonnées de la ligue et noms, prénoms et courriels de dix responsables (président, secrétaire, trésorier, technique, homologations, jeunes, scolaires, formation, arbitrage, féminines) | abréviation de la ligue (exemples : `mpy`, `occ`) |
| `Equipes.asmx/GetEquipesDuClub` | `Saison`, `Ref` | par équipe : identifiant, nom, groupe (identifiant, nom), division (identifiant, nom), compétition (identifiant, nom), place | numéro interne du club |
| `Equipes.asmx/GetEquipesDuGroupe` | `saison`, `Ref` | classement du groupe : place, équipe, points de match, rencontres jouées, différentiel, points pour et contre ; noms de la compétition, de la division et du groupe | identifiant du groupe |
| `Equipes.asmx/GetPVs` | `saison`, `ref` | les procès-verbaux du groupe (voir 2.3) | identifiant du groupe |

La saison est l'année de fin : `2016` désigne la saison 2015-2016 (commentaire du plugin).

Le plugin ne sait pas chercher un joueur par son numéro de licence. Pour appeler `GetJoueur`, il
faut le numéro interne, que notre domaine ne détient pas ([0015](../adr/0015-identifiant-ffe-de-la-personne.md)
ne retient que le numéro de licence).

### 2.3 Rondes et feuilles de match : `GetPVs`

Un appel renvoie **tous les procès-verbaux d'un groupe**, toutes rondes confondues. Le plugin
filtre ensuite par ronde et par équipe de son côté : le groupe est l'unité d'accès, pas l'équipe.

Par rencontre : ronde, date, lieu, équipe « blanche » (numéro interne, nom), équipe « noire »
(numéro interne, nom), points de parties de chaque équipe, nombre de joueurs. Par échiquier de 1 à
`NbrJoueurs` : un joueur « blanc », un joueur « noir » et un code de résultat.

Ce que le code permet de dire, et ce qu'il laisse ouvert :

- les joueurs d'une feuille sont des **textes** ; le plugin ne lit aucun identifiant de joueur sur la
  feuille, et nous ignorons si le service en renvoie ;
- le même appel sert à afficher les dates à venir d'une équipe : la liste contient donc, selon toute
  vraisemblance, des rencontres pas encore jouées (hypothèse, non vérifiée) ;
- « blanc » semble désigner l'équipe nommée en premier, celle qui reçoit (règle A02 : elle a les
  blancs aux échiquiers impairs). Le commentaire d'un utilisateur de la documentation relève que le
  modèle de dates ne dit pas quelle équipe a les blancs ;
- les codes de résultat sont interprétés ainsi par le plugin, sans aucun document officiel :

| Code | Affichage du plugin | Remarque |
|---|---|---|
| 1 | X-X | nulle, d'après l'affichage |
| 2 | 1-0 | 2-0 sur les six premiers échiquiers d'une équipe jeune de dix joueurs |
| 3 | 0-1 | 0-2 dans le même cas |
| 4 | 0-0 | |
| 5 | 1-F | forfait de l'adversaire, d'après l'affichage |
| 6 | F-1 | |
| 9 | F-F | |
| 15 | A-1 | sens de « A » inconnu |
| autre | rien d'affecté | le plugin ne prévoit pas de code inconnu |

- le plugin balaie les rondes de 1 à 10 ; la Nationale 4 en compte sept ou neuf ;
- le fichier `Todo.txt` du plugin signale que les équipes jeunes sont mal affichées (calcul des
  points différent).

### 2.4 Ce qui est accessible, par thème

| Thème | Disponible d'après le code | Observation |
|---|---|---|
| Clubs | liste par département, avec coordonnées, salle de jeu, nombre de licenciés, divisions | le readme avertit que ces données sont souvent incomplètes entre le 31 août et le premier match de la nouvelle saison |
| Équipes | équipes d'un club pour une saison, avec groupe, division, compétition et place ; classement d'un groupe | |
| Rondes | ronde, date et lieu de chaque rencontre d'un groupe | l'appariement vient de `GetPVs` |
| Feuilles de match | échiquier par échiquier : noms des joueurs et code de résultat | pas d'Elo ni d'identifiant de joueur dans ce que le plugin lit |
| Joueurs | identité, licence, catégorie, Elo, titre FIDE, club, courriel | données personnelles |
| Ligues | coordonnées et dix responsables | données personnelles |

### 2.5 Authentification

Aucune dans le code : ni clé, ni jeton, ni identifiant, ni mot de passe, ni en-tête. La seule
configuration du plugin est la saison, un numéro de club et un numéro de département. Cela décrit
le plugin, pas le service : celui-ci peut limiter les appels par adresse, par fréquence ou par
identification du client sans que le plugin le montre.

### 2.6 Dépendances anciennes ou non documentées

- un service ASP.NET de génération ancienne, sur un hôte à part, **sans document public** que le
  plugin, sa fiche ou sa documentation citent (pas de WSDL, de page d'aide ni de condition d'accès) ;
- un échange **en clair** (`http`) ;
- un format Microsoft (diffgram) qui impose un lecteur dédié ;
- des identifiants internes (`Ref`) qui ne sont pas les numéros publics (`NrFFE`) et qui
  désignent des objets différents selon la méthode ;
- des codes de résultat magiques, non documentés, et un cas particulier des équipes jeunes ;
- des données dites incomplètes entre deux saisons, et un historique Elo qui n'est plus mis à jour ;
- un plugin tenu par des particuliers, dont l'auteur anticipe l'obsolescence.

Le plugin appelle les mêmes adresses depuis 2015 : cela prouve que l'interface a été stable, pas
qu'elle fonctionne aujourd'hui, ni qu'un tiers y soit autorisé. Ni la fiche ni la documentation du
plugin ne mentionnent une autorisation de la FFE.

## 3. Les textes de la FFE

### 3.1 Les mentions légales

Le document compte onze rubriques. Deux concernent notre question : la 5, propriété intellectuelle,
et la 7, données personnelles. La 2 (conditions générales d'utilisation) dit que l'utilisation du
site vaut acceptation, et la 4 (limitations techniques) ne traite que du matériel et du navigateur
de l'utilisateur. Le mot « service web », les mots « API », « robot », « extraction » et
« moissonnage » n'apparaissent nulle part.

**Rubrique 5.**

- Objet protégé : « tous les éléments accessibles sur le site », notamment textes, images,
  graphismes, logo, icônes, sons, logiciels. Les données ne sont pas citées.
- Actes interdits sans autorisation écrite préalable de la FFE : la reproduction, la
  représentation, la modification, la publication et l'adaptation de tout ou partie de ces
  éléments, par quelque moyen que ce soit.
- Sanction annoncée : toute exploitation non autorisée du site ou d'un de ses éléments est traitée
  comme une contrefaçon.
- Ce qui n'est pas dit : ni la consultation, ni l'accès automatisé, ni le lien ne figurent parmi les
  actes interdits. « Exploitation » n'est pas définie. Le texte vise « le site », sans dire si le
  service sur un autre hôte en fait partie.

**Rubrique 7.** Elle traite pour l'essentiel des données personnelles *des utilisateurs du site* :
ce qui est recueilli à la navigation (adresse d'origine du lien, fournisseur d'accès, adresse IP),
leurs droits d'accès et de rectification, l'absence de cession à des tiers, l'absence de déclaration
à la CNIL. Une seule phrase touche à notre question : les bases de données sont protégées par la loi
du 1er juillet 1998, qui transpose la directive 96/9 sur la protection juridique des bases de
données.

Cette phrase ne pose aucune interdiction propre : elle renvoie à la loi. À notre lecture, non
juridique, ce droit « sui generis » du producteur de la base permet d'interdire l'extraction et la
réutilisation de la totalité ou d'une partie *substantielle* du contenu, et ne s'oppose pas à
l'utilisation de parties non substantielles par un utilisateur légitime, sauf extraction répétée et
systématique qui dépasserait un usage normal (Code de la propriété intellectuelle, articles L341-1
et suivants, à faire confirmer).

### 3.2 La politique de protection des données

Elle décrit les données de licence que les clubs saisissent (noms, dates de naissance, courriels),
dit qu'elles servent « uniquement » à gérer les compétitions par équipes et individuelles, et que le
nom, le classement et la catégorie d'âge sont affichés dans la liste des licenciés d'un club et dans
les résultats. Elles sont mises à la disposition des arbitres et des directeurs de groupe, et
transmises à la FIDE pour les compétitions homologuées. Elle nomme un responsable des données. Elle
ne dit rien d'une réutilisation par un tiers.

Elle confirme que la FFE est responsable de ces données personnelles : toute reprise de noms de
joueurs (y compris ceux d'adversaires sur une feuille de match) relève du RGPD pour nous aussi, avec
une finalité (la vie du club) qui n'est pas celle que la FFE annonce (la gestion des compétitions).
Que ces noms soient publics sur le site fédéral ne dit pas qu'on peut les republier à toute fin.

### 3.3 Quatre gestes, quatre lectures

| Geste | Rubrique 5 | Rubrique 7 | Lecture prudente |
|---|---|---|---|
| **Accès technique** : un programme demande une page au service et montre le résultat à l'utilisateur sans le garder | pas listé parmi les actes interdits ; « exploitation » reste à définir | rien | ni interdit ni permis par le texte. Incertain : les conditions du site couvrent-elles un service sur un autre hôte et un client automatique ? |
| **Extraction** : copier des données du service dans notre système | une copie des éléments du site est une « reproduction » ; les données elles-mêmes ne sont pas citées parmi les éléments | extraction d'une partie substantielle interdite par la loi, pas d'une partie non substantielle | dépend de l'ampleur. Les rencontres des équipes d'un club ne sont probablement pas une partie substantielle de la base fédérale. Un balayage de tous les clubs ou joueurs l'est probablement. À confirmer |
| **Stockage** : garder la copie (cache ou base) | « reproduction » | la loi vise aussi le transfert temporaire | plus risqué que l'affichage transitoire. Un cache court est un stockage ; une base durable est une extraction durable |
| **Republication** : montrer ces données sur une page publique | « publication » et « représentation » interdites sans autorisation écrite | réutilisation d'une partie substantielle interdite | c'est le geste qui demande l'autorisation écrite, d'autant que des noms de personnes sont en jeu |

Deux nuances que les textes imposent :

- une interdiction de reproduire ne dit pas qu'*aucune* consultation n'est permise : le texte ne
  l'affirme pas, et nous ne le déduisons pas ;
- ce qu'un club sait de ses propres rencontres n'a pas à venir du service : il les joue, et le
  club qui reçoit saisit lui-même le procès-verbal sur le site fédéral. La saisie manuelle dans
  notre application n'extrait rien de la base fédérale.

## 4. Faits, hypothèses, incertitudes

**Vérifié dans le code ou dans les textes**

- huit méthodes, sur quatre services `.asmx` d'un hôte `service.echecs.asso.fr`, appelées en GET
  `http`, avec les paramètres du tableau 2.2 ;
- réponse en XML de type diffgram ;
- aucun secret, jeton ni en-tête dans le plugin ;
- les mentions légales ne citent ni service web, ni API, ni extraction ; la rubrique 5 interdit
  reproduction, représentation, modification, publication et adaptation sans autorisation écrite,
  la rubrique 7 invoque la protection des bases de données ;
- la politique de protection des données ne prévoit pas de réutilisation par un tiers.

**Hypothèses, à vérifier si l'on obtient l'autorisation**

- `GetPVs` contient les rencontres à venir ;
- l'équipe « blanche » est celle qui reçoit ;
- `NeLe` est la date de naissance et peut être renseignée ;
- le service répond encore comme le plugin l'attend ;
- les parties non substantielles de la base restent librement utilisables dans notre cas.

**Incertitudes**

- le service est-il destiné aux tiers, ou seulement au site fédéral ?
- les conditions du site s'appliquent-elles à ce service ?
- limites d'usage, disponibilité, versions, fin de vie annoncée ?
- sens officiel des codes de résultat et des identifiants `Ref` ;
- les feuilles de match portent-elles un identifiant de joueur ?
- une réutilisation pour la vie des clubs est-elle compatible avec les finalités annoncées par la FFE ?

## 5. Architecture : fonctionner sans la FFE, l'accueillir si elle l'autorise

Principe : la plateforme fonctionne entièrement sans FFE. L'intégration est un accélérateur
facultatif, jamais une dépendance. Toute la saisie existe à la main ; l'import n'en est qu'un
raccourci.

### 5.1 Les pièces (hypothèses, rien n'est construit)

- **Un port**, possédé par le contexte qui détient les rencontres, dans le langage du club : « les
  rencontres d'une équipe pour une saison », « le résultat d'une rencontre », « les équipes d'un club
  pour une saison ». Aucun type FFE dans sa signature. Il renvoie des valeurs métier ou une erreur
  explicite (source indisponible, donnée incomplète, source non autorisée). Son nom reste à trouver
  au premier test, en parlant comme un administrateur de club ; `CompetitionDataProvider` est un
  candidat, un nom qui dit le service rendu serait meilleur.
- **Un adaptateur FFE** derrière une couche anticorruption : client HTTP, lecture du diffgram,
  traduction des codes de résultat et des `Ref`. Seul ce paquet connaît HTTP, XML et le vocabulaire
  fédéral. Les numéros ont chacun leur type (club, groupe, joueur interne), distincts du numéro de
  licence. Une règle ArchUnit l'enferme, comme prévu pour toute bibliothèque ou service externe.
- **Pas d'adaptateur** quand la FFE n'est pas configurée : le port n'a alors aucune implémentation,
  l'action d'import n'est pas proposée et toutes les autres fonctions restent intactes.
- **Une configuration**, désactivée par défaut. L'activer en production suppose une autorisation
  écrite consignée dans un ADR : le garde-fou est juridique avant d'être technique.

### 5.2 Trois façons de lire, trois niveaux de risque

| Mode | Principe | Gestes touchés (3.3) |
|---|---|---|
| A. À la demande | on demande au moment d'afficher, rien n'est gardé | accès technique |
| B. Cache court | on garde la réponse quelques minutes ou heures | accès, stockage temporaire |
| C. Import durable | on copie dans notre base, puis on publie | extraction, stockage, republication |

Si l'autorisation est partielle, A puis B sont les seuls modes. C demande une autorisation
explicite, et la publication de noms de joueurs adverses demande en plus un fondement RGPD
(`docs/registre-des-traitements.md`).

### 5.3 Identité

Trois notions restent distinctes, comme le demande le pivot :

- le **membre** (`Person`, `ClubRelationship`) ;
- le **participant à une rencontre** : un nom tel qu'il figure sur la feuille, éventuellement relié à
  un membre quand il joue pour notre club ;
- la **référence fédérale** : facultative, numéro de licence ([0015](../adr/0015-identifiant-ffe-de-la-personne.md))
  ou numéro interne du service.

Aucune fiche de membre n'est créée pour un adversaire. Un participant n'est rapproché d'un membre
que par un identifiant, jamais par le nom seul ; sans identifiant, c'est un choix du webmaster.

### 5.4 Tests sans service externe

- des réponses XML **synthétiques**, écrites par nous, de la forme décrite en 2.1, avec des valeurs
  fictives : aucune ne reproduit une réponse réelle (ADR 0018) ;
- des cas d'erreur : source indisponible (délai dépassé, erreur HTTP, XML invalide), ligne unique,
  champ absent, code de résultat inconnu rendu en « résultat inconnu » explicite, jamais deviné ;
- un test d'architecture qui interdit à tout autre paquet de connaître l'adaptateur.

La vraie forme des réponses ne sera vérifiée qu'après autorisation, par un appel unique en lecture,
hors du build.

### 5.5 Défaillances à prévoir

Service arrêté ou retiré, passage à `https` ou changement de format, données incomplètes entre deux
saisons, code de résultat nouveau, identifiant `Ref` modifié, retrait de l'autorisation (il faudra
savoir purger ce qui a été gardé).

## 6. Questions à adresser à la FFE

À poser par écrit. Le responsable des données et le webmaster sont nommés dans les deux PDF ; leurs
coordonnées n'ont pas été recopiées ici.

**Statut du service**

1. `service.echecs.asso.fr` est-il un service que la FFE met à la disposition de tiers, ou un service
   interne au site fédéral ? Existe-t-il une documentation, une description de contrat (WSDL), une
   politique de versions ?
2. Une évolution ou un remplacement est-il prévu (nouvelle version, passage à `https`, nouvelle API) ?
3. Les rubriques 5 et 7 des mentions légales s'appliquent-elles aux données renvoyées par ce service ?

**Portée de l'autorisation demandée**

4. Finalité : une plateforme libre et gratuite pour la vie des clubs (actualités, récits de rencontres),
   avec Orléans pour club pilote, puis d'autres clubs. La FFE y voit-elle une finalité compatible avec
   celles qu'elle annonce ?
5. Ressources visées : équipes d'un club, calendrier des rencontres d'un groupe, procès-verbaux et
   classement d'un groupe. Pas de liste de joueurs, de courriels, de dates de naissance ni de
   coordonnées de dirigeants. Est-ce acceptable, et que doit-on exclure d'autre ?
6. Volume : les seules équipes des clubs inscrits sur la plateforme, une fois par jour au plus. Cela
   reste-t-il une partie non substantielle à ses yeux ?
7. Stockage : peut-on garder le calendrier et les résultats des rencontres de nos équipes, et combien
   de temps ? Un cache court suffit-il ?
8. Publication : peut-on afficher sur la page publique d'un club le résultat et l'adversaire de ses
   rencontres, et les noms des joueurs adverses de la feuille ? Une mention de la source est-elle
   exigée ?

**Conditions d'accès**

9. Faut-il une identification (clé, compte, adresse déclarée) ? Quelles limites de fréquence, quels
   horaires de maintenance, quel contact en cas d'incident ?
10. Le service est-il disponible en `https` ?

**Sémantique des données**

11. Définition officielle des codes de résultat (dont le 15) et des cas d'équipes jeunes.
12. Sens exact de `Ref` pour chaque méthode, et correspondance entre numéro interne et numéro de
    licence, de club et de joueur.
13. Les feuilles de match portent-elles un identifiant de joueur ? Les rencontres à venir sont-elles
    servies par `GetPVs` ? Comment sont représentés un forfait, un report, une rencontre non jouée ?

**Données personnelles et cadre**

14. La FFE exige-t-elle une convention de partage de données ? Qui la signe : l'auteur du projet, un
    club, ou une association qui exploiterait la plateforme ?
15. Existe-t-il un export officiel (fichier) que chaque club peut tirer de son espace dédié pour ses
    propres équipes, et que la plateforme pourrait accepter en import, sans interroger le service ?
16. Procédure de retrait : si l'autorisation est retirée ou si la FFE modifie ses conditions, quel
    délai pour purger ce qui a été gardé ?

La question 15 ouvre une voie qui ne passe pas par le service : le club fournit lui-même ses
données, depuis son espace fédéral. Elle est à poser en premier si la réponse aux questions 1 à 3
est défavorable.

Il serait utile de mettre en copie la Ligue du Centre-Val de Loire, qui organise la Nationale 4 et
dont les directeurs de groupe gèrent les appariements, et le président du club pilote.

## 7. Suite

- Aucun appel au service tant qu'une autorisation écrite n'est pas obtenue.
- Pas d'implémentation du port ni de l'adaptateur dans la PR #31 : une slice d'import viendra quand
  la saisie manuelle des rencontres existera et qu'un besoin réel d'import sera constaté (YAGNI).
- L'ADR du pivot consignera le principe « la plateforme fonctionne sans la FFE » et la condition
  d'une autorisation écrite pour toute activation.
