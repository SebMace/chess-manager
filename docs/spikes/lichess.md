# Spike Lichess : saisir ses parties sur Lichess, les importer dans ChessManager

Date : 2026-11-01. Spike de la PR #31.

**Méthode.** Lecture de la documentation officielle de l'API (dépôt `lichess-org/api`, cloné et lu,
jamais exécuté). **Aucun appel à Lichess, aucun compte utilisé.** Tout ce qui suit vient de la
spécification, pas d'un essai : la section 7 dit ce qu'il restera à éprouver.

## 1. Le parcours visé

1. Le joueur saisit et commente sa partie sur Lichess, dans une étude (un chapitre par partie).
2. Il revient dans ChessManager et choisit la rencontre.
3. Il apporte le PGN de sa partie.
4. ChessManager le conserve ; le joueur ajoute éventuellement un commentaire ; le webmaster valide.

ChessManager ne saisit pas les coups et n'analyse rien : c'est le travail de Lichess. Rien dans la
documentation n'indique que Lichess renvoie le PGN tout seul au retour dans ChessManager : le
joueur le rapporte lui-même.

## 2. Ce que la documentation permet (vérifié dans la spécification)

| Besoin | Point d'accès | Remarques |
|---|---|---|
| Un chapitre d'étude en PGN | `GET /api/study/{studyId}/{chapterId}.pgn` | identifiants de 8 caractères ; options `comments`, `variations`, `clocks` (vraies par défaut) et `orientation` (fausse par défaut : ajoute la balise `Orientation`) |
| Toute l'étude en PGN | `GET /api/study/{studyId}.pgn` | mêmes options ; `Last-Modified` en réponse ; `HEAD` pour les seules métadonnées |
| Les études d'un utilisateur | `GET /api/study/by/{username}` | nom et dates, en flux ndjson |
| Le profil de l'utilisateur connecté | `GET /api/account` | demande un jeton |

Qui peut lire quoi :

- **sans jeton** : seuls les chapitres des études **publiques** (les études « non listées » sont
  exclues) ;
- **avec un jeton de portée `study:read`** : études publiques, non listées et privées.

Authentification, si elle est nécessaire :

- un **jeton personnel** que l'utilisateur crée lui-même sur son compte ;
- ou le **code d'autorisation OAuth2 avec PKCE**, qui permet à un utilisateur de « se connecter avec
  Lichess » : client public non enregistré, identifiant de client choisi librement, méthode de
  défi `S256` seule acceptée, jetons valables environ un an, pas de jeton de rafraîchissement ;
- dans les deux cas, un jeton ne doit jamais se trouver dans le code envoyé au navigateur ni dans
  un dépôt public. Aucun mot de passe Lichess n'est jamais demandé ni stocké.

Limites d'usage : toutes les requêtes sont limitées ; la documentation demande de n'en faire
**qu'une à la fois** et, en cas de réponse 429, d'attendre au moins une minute avant de réessayer.

## 3. Trois niveaux d'accès, du plus simple au plus lourd

| Niveau | Principe | Ce qu'il permet | Coût |
|---|---|---|---|
| 0. Collage | le joueur copie le PGN depuis Lichess et le colle dans ChessManager | tout PGN, de n'importe quelle origine | aucune intégration ; **toujours disponible**, sert de repli |
| 1. Adresse | le joueur colle l'adresse de son chapitre ; le serveur télécharge le PGN sans jeton | les études **publiques** | un port, un adaptateur HTTP, la gestion des pannes et de la limite d'usage |
| 2. Compte associé | le joueur associe son compte Lichess (OAuth2 PKCE) ; le serveur lit avec son jeton | aussi les études non listées et privées | stocker un secret par joueur, le protéger, gérer l'expiration et la révocation, l'inscrire au registre des traitements |

**Proposition.** La V1 commence au niveau 0, puis ajoute le niveau 1. Le niveau 2 attend qu'un
joueur ait vraiment besoin d'importer une étude privée (YAGNI) : associer un compte Lichess n'est
pas nécessaire pour importer une étude publique.

## 4. Où se fait l'appel

Le plus conforme à l'architecture hexagonale est un **adaptateur sortant du serveur**, derrière un
port dont le nom vient du besoin du club (« le PGN d'une partie à partir de son adresse Lichess »),
sans type Lichess dans sa signature. Le serveur contrôle ainsi la taille, le délai d'attente et la
limite d'usage, et ne dépend pas du navigateur.

Une variante plus simple existe : laisser le navigateur du joueur télécharger le PGN, puis l'envoyer
à ChessManager comme un collage. Le serveur n'appellerait jamais Lichess. La spécification annonce
l'en-tête `Access-Control-Allow-Origin: *` pour l'export d'une étude entière et pour la liste des
études, mais pas pour l'export d'un chapitre : à vérifier avant d'envisager cette variante.

## 5. Ce que ChessManager conserve

- le **PGN complet**, tel que reçu, balises `SetUp`, `FEN` et `Orientation` comprises : la partie
  reste consultable même si l'étude disparaît ;
- facultativement, l'identifiant de l'étude et celui du chapitre (8 caractères chacun), comme
  simples références ;
- le nom d'utilisateur Lichess seulement au niveau 2.

Rien d'autre n'est dupliqué : la couleur du joueur et la position de départ se lisent dans le PGN.

## 6. Droits, personnes et pannes

- Une étude est le contenu de son auteur. Le joueur n'importe que **ses propres parties** et le
  déclare ; le webmaster valide avant toute publication.
- Un PGN nomme les deux joueurs, donc l'adversaire. La règle de publication de ces noms (les
  garder, les abréger, demander l'accord) reste à décider avant la première publication, et le
  registre des traitements (`docs/registre-des-traitements.md`) recevra le traitement.
- **Lichess indisponible, limite atteinte (429), étude introuvable ou privée** : le joueur reçoit un
  message clair et se replie sur le collage (niveau 0). Les codes de réponse exacts de ces cas ne
  sont pas dans ce que nous avons lu.
- Les conditions d'utilisation de Lichess n'ont pas été lues : à faire avant le premier import
  réel. L'API est publique et documentée pour les tiers, ce qui la distingue d'un service interne.
- Les tests ne font jamais d'appel réel : un faux de Lichess (port simulé, ou serveur HTTP local
  qui rejoue un PGN écrit par nous).

## 7. Ce qu'il reste à éprouver (plus tard, hors du build)

À faire avec l'accord de l'auteur, sur une étude publique qu'il crée lui-même :

- le contenu réel d'un export : balises `Orientation`, `SetUp` et `FEN` d'un chapitre à position de
  départ personnalisée, variantes et commentaires ;
- les en-têtes de réponse, dont l'accès depuis un navigateur ;
- les codes d'erreur d'une étude inexistante, privée ou non listée ;
- le comportement sous la limite d'usage.

Un compte Lichess de l'auteur sera nécessaire pour tester une étude privée (niveau 2).
