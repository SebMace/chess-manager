# Spike viewer PGN et licence

Date : 2026-11-01. Spike de la PR #31.

**Méthode.** Lecture des README et des métadonnées des bibliothèques (`gh api`, `npm view`) ;
aucune bibliothèque installée ni exécutée. Ce n'est pas un avis juridique.

## 1. Ce qu'on attend d'un viewer

Rejouer une partie à partir de son PGN, avec : navigation coup par coup, commentaires, variantes,
position de départ personnalisée (balises `SetUp` et `FEN`), échiquier orienté avec les Blancs ou
les Noirs en bas, affichage sur téléphone, accessibilité au clavier, intégration dans Angular 22.
Pas de saisie de coups ni d'analyse : c'est Lichess ([lichess.md](lichess.md)).

## 2. Ce qui existe (licence, version et activité vérifiées)

| Brique | Licence | Version, activité | Ce qu'elle fait |
|---|---|---|---|
| `@lichess-org/pgn-viewer` | **GPL-3.0-or-later** | 2.7.3 ; dépôt actif en octobre 2026 | viewer complet : arbre de variantes, commentaires, joueurs et pendules, mobile, accessibilité lecteur d'écran et clavier, orientation (option ou balise), FEN de départ, position initiale ; côté navigateur seulement. Dépend de Chessground et de chessops, eux aussi GPL |
| `@mliebelt/pgn-viewer` | **GPL-3.0-or-later**, à cause de Chessground | 1.6.11 sur npm (avril 2025) ; dépôt actif en octobre 2026 | variantes, commentaires, thèmes, mode édition |
| `chessground` | GPL-3.0-or-later | 9.2.1 | l'échiquier de Lichess, sans lecture de PGN |
| `chessops` | GPL-3.0-or-later | 0.15.1 | règles et lecture de PGN |
| `kokopu` | LGPL-3.0-or-later | 4.13.4 | lecture de PGN et dessin SVG ; la LGPL pose des conditions de remplacement de la bibliothèque, non analysées, non retenue |
| `cm-chessboard` | **MIT** | 8.15.4 ; actif en octobre 2026 | échiquier SVG adaptatif, sans dépendance, avec extensions (marqueurs, flèches, accessibilité) ; ni lecture de PGN ni navigation |
| `chess.js` | **BSD-2-Clause** | 1.4.0 | règles et lecture de PGN ; les variantes ne sont pas mentionnées dans son README (non vérifié) |
| `@mliebelt/pgn-parser` | **Apache-2.0** | 1.4.19 ; actif en octobre 2026 | lecture d'un PGN en structure (balises, coups) ; version utilisable dans un navigateur ; variantes et commentaires non vérifiés dans son README |
| `ngx-chess-board` | MIT | 3.0.0 ; dernière modification en décembre 2023 | composant Angular ; non examiné, écarté pour son ancienneté face à Angular 22 |

**Constat : aucun viewer complet n'est sous licence permissive.** Les deux viewers complets sont en
GPL-3.0 parce qu'ils reposent sur Chessground. Seules des briques séparées (échiquier, lecture de
PGN, règles) sont en MIT, BSD ou Apache.

## 3. Ce que la GPL change pour ChessManager

- Le README du viewer Lichess le dit lui-même : utilisé sur un site, l'œuvre combinée ne peut être
  distribuée que sous GPL, et le code source doit être publié pour les utilisateurs du site.
- Concrètement, le front-end Angular envoyé au navigateur contiendrait le viewer : **le front-end
  entier passerait sous GPL-3.0** (ou une licence compatible), avec son code source disponible. Le
  dépôt est déjà public : la publication n'est pas l'obstacle, c'est la licence.
- Le serveur est un autre programme, qui parle en HTTP : à notre lecture, il n'est pas une œuvre
  combinée avec le viewer. Le dépôt n'a pourtant qu'un fichier `LICENSE` (MIT) ; faire coexister MIT
  et GPL par dossier est possible mais complique tout, et nous ne le recommandons pas.
- Les dépendances actuelles (Spring Boot, pilote PostgreSQL, Flyway, Angular, RxJS) sont sous des
  licences permissives que nous croyons compatibles avec la GPL-3.0 : à vérifier avec un outil de
  licences avant tout changement.
- **GPL-3.0 ou AGPL-3.0 ?** L'AGPL étend l'obligation de publier le code aux modifications d'un
  service utilisé par le réseau, ce qui protège un site contre une version fermée. Les deux
  licences peuvent se combiner (à confirmer).
- **Ce qui est déjà publié en MIT le reste.** Seul le code à venir changerait de licence. Le fichier
  `LICENSE` nomme un seul titulaire : à vérifier qu'aucun tiers n'a contribué avant de changer.

## 4. Trois chemins

| Chemin | Licence de ChessManager | Viewer | Avantages | Inconvénients |
|---|---|---|---|---|
| **A. Assembler un viewer permissif** | MIT inchangée | `cm-chessboard` (échiquier) + `@mliebelt/pgn-parser` (lecture) + `chess.js` (calcul des positions) + notre navigation | aucun changement de licence ; pas de réécriture d'un moteur d'échecs | un composant à écrire et à tester (lecture, navigation, variantes, commentaires, orientation, FEN, clavier) ; à entretenir nous-mêmes |
| **B. Passer en copyleft** | GPL-3.0 ou AGPL-3.0 | `@lichess-org/pgn-viewer` | chemin le plus court ; variantes, commentaires et accessibilité déjà traités et entretenus par Lichess | changement de licence ; les réutilisateurs devront publier leurs modifications |
| C. Licences mixtes par dossier | MIT et GPL | viewer Lichess dans `front-end/` | licence du serveur inchangée | complexité juridique et de maintenance ; non recommandé |

Écarté : intégrer l'affichage de Lichess par cadre intégré. Il montre des parties hébergées chez
Lichess, pas le PGN que nous conservons, et dépend de sa disponibilité.

## 5. Nos besoins face aux deux chemins

| Besoin | B. Viewer Lichess | A. Assemblage permissif |
|---|---|---|
| Variantes | oui, arbre de variantes | à construire ; lecture par `pgn-parser` à vérifier |
| Commentaires | oui | à construire |
| FEN de départ | oui (option, ou balise du PGN) | à construire ; `cm-chessboard` et `chess.js` acceptent une FEN |
| Orientation | oui (option, ou balise `Orientation`) | à vérifier dans `cm-chessboard` |
| Téléphone | oui | oui pour l'échiquier, à construire pour la navigation |
| Accessibilité | oui (ARIA, clavier) | extension d'accessibilité de l'échiquier ; navigation à écrire |
| Angular 22 | bibliothèque indépendante d'un framework, montée dans un élément du DOM ; aucun adaptateur Angular examiné | idem |
| Entretien | l'équipe Lichess | nous |

## 6. Recommandation

Le choix de la licence est celui de l'auteur, et il décide du chemin.

- Si l'auteur veut de toute façon un copyleft (protéger un site hébergeant des contenus de clubs
  contre une reprise fermée, par exemple), **B** est cohérent et nettement plus rapide.
- Sinon, **A** évite de changer la licence du projet pour un composant d'affichage, au prix de plus
  de travail. Il se fait par étapes : ligne principale, orientation et FEN d'abord ; commentaires
  et variantes ensuite.

Dans les deux cas : le serveur conserve le PGN comme un texte et ne l'interprète pas en V1 ; le
viewer ne valide rien.

## 7. Précision pour l'ADR 0009

L'ADR [0009](../adr/0009-front-end-angular-separe.md) interdit les règles métier dans le front-end.
Afficher une partie n'est pas une règle métier : le viewer est une brique de présentation. Il ne
décide de rien, n'ajoute aucune validation de coups, et affiche une erreur si le PGN est illisible.

## 8. Reste à vérifier

- les variantes et commentaires de `@mliebelt/pgn-parser`, et les variantes de `chess.js` (un test
  avec un PGN écrit par nous suffit) ;
- l'orientation de `cm-chessboard` ;
- que le viewer Lichess lit une partie dont les balises `SetUp` et `FEN` sont dans le PGN ;
- la compatibilité de licences des dépendances actuelles, avec un outil de licences.
