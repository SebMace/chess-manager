# 31. Passer ChessManager de la licence MIT à la licence AGPL-3.0

Date : 2026-11-02

## Statut

Acceptée. Tranche la question de licence laissée ouverte par
[0030](0030-recentrer-sur-la-vie-des-clubs.md).

## Contexte

Le dépôt est sous licence MIT depuis 2024. ChessManager doit rejouer des parties à partir de leur
PGN, avec variantes, commentaires et accessibilité ([spike](../spikes/pgn-viewer.md)). Les viewers
complets, celui de Lichess comme celui de mliebelt, sont sous GPL-3.0-or-later. Utilisé sur un site,
un tel viewer impose que l'œuvre combinée, ici le front-end envoyé au navigateur, soit sous GPL avec
son code source disponible. Assembler un viewer à partir de briques MIT, BSD et Apache aurait évité
ce changement, au prix d'un composant à écrire et à entretenir.

Ce qui a été vérifié le 2026-11-02 :

- **un seul auteur** : les commits de toutes les branches portent trois identités Git, mais une
  seule adresse électronique ; aucun tiers n'a contribué ;
- **le front-end** : les dépendances livrées au navigateur sont sous MIT (8), Apache-2.0 (1) et
  0BSD (1), compatibles avec l'AGPL. Les dépendances de développement ne sont pas distribuées ;
- **le serveur** : Spring Boot, le pilote PostgreSQL et Flyway sont connus pour être sous
  Apache-2.0 ou BSD ; cela n'a pas été vérifié avec un outil de licences ;
- **la licence MIT n'est écrite que dans `LICENSE` et dans le README** : ni `pom.xml` ni
  `package.json` n'en portent.

## Décision

Nous plaçons le code de ChessManager sous la licence publique générale Affero GNU, version 3 ou
toute version ultérieure (AGPL-3.0-or-later), la même formule que celle du viewer de Lichess pour la
GPL.

- `LICENSE` contient le texte officiel de l'AGPL-3.0, tel que la Free Software Foundation le publie,
  sans modification. Le README nomme le titulaire et la licence. Nous n'ajoutons pas d'en-tête dans
  chaque fichier (YAGNI).
- **Ce qui a été publié sous MIT le reste.** Seul le code publié à partir de la fusion de cette
  pull request l'est sous AGPL.
- Les contributions sont acceptées sous la même licence que le projet.
- **L'ajout du viewer de Lichess n'est pas décidé ici.** La licence le permet, mais c'est une
  dépendance de production : elle se décide à la slice *Consult a game*.
- Toute nouvelle dépendance doit avoir une licence compatible avec l'AGPL-3.0.

## Conséquences

**Ce qui devient plus facile.** Le viewer de Lichess (variantes, commentaires, accessibilité)
devient utilisable, sans composant d'affichage à écrire. Un site hébergé est protégé : qui héberge
une version modifiée comme service doit en publier le code (article 13 de l'AGPL).

**Ce qui devient plus difficile, ou à surveiller.**

- le code ne peut plus entrer dans un produit fermé, et certaines organisations écartent l'AGPL par
  politique ;
- un club ou une société qui héberge sa propre version modifiée doit publier ses modifications ;
- les futures dépendances sont à vérifier une à une, un outil de licences pourra s'imposer ;
- « ou toute version ultérieure » permet aux utilisateurs de suivre de futures versions de la FSF.
  Pour garder la main, il suffirait d'écrire « version 3 seulement » dans le README, tant que
  rien n'est publié sous la première mention.

## Options écartées

- **Rester en MIT avec un viewer assemblé** (échiquier `cm-chessboard`, lecture `pgn-parser`, règles
  `chess.js`) : possible, mais un composant à écrire et à entretenir, alors que l'auteur veut un
  copyleft.
- **GPL-3.0** : même famille que le viewer, mais elle n'oblige pas l'hébergeur d'une version
  modifiée à en publier le code, ce qui compte pour un site.
- **Des licences différentes par dossier** (MIT pour le serveur, GPL pour le front-end) : une
  complexité juridique et de maintenance sans bénéfice.
- **Une double licence MIT et AGPL** : personne ne la demande.
