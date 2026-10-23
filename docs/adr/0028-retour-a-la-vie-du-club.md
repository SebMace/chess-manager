# 28. Retour à la vie du club et aux compétitions

Date : 2026-10-23

## Statut

Acceptée. Remplace la priorité donnée à Chess Decision par
[0020](0020-priorite-a-chess-decision.md).

## Contexte

L'ADR [0020](0020-priorite-a-chess-decision.md) (2 octobre 2026) a fait de Chess Decision le cœur
du produit : un outil personnel de progression aux échecs. L'ADR
[0021](0021-reprise-de-la-gestion-de-club.md) (23 octobre) a levé la pause de la gestion de club,
pour présenter l'application au président et au secrétaire d'un club.

Le même jour, l'auteur a tranché : Chess Decision n'est plus le produit phare, et le travail
revient à la vie du club, avec les compétitions. Le premier besoin est le championnat interclubs de
Nationale 4 du Loiret (voir [0027](0027-competition-contexte-distinct.md)). Aucun code de Chess
Decision n'existe sur `main`.

## Décision

Nous remplaçons la priorité de 0020 : le produit est d'abord un outil pour la vie d'un club. Ses
deux domaines sont la gestion de club (membres, licences, horaires d'ouverture) et les compétitions
par équipes. Les prochaines slices viennent de l'un ou de l'autre, et chacune passe d'abord au
crible YAGNI.

Chess Decision n'est plus le produit phare. Il n'est ni supprimé ni planifié : aucune slice ne le
démarre tant que l'auteur ne le décide pas. S'il reprend, les principes de 0020 restent valables :
les thèmes sont des hypothèses, jamais des vérités ; le domaine, le moteur, le LLM et un éventuel
serveur MCP restent distincts ; aucune infrastructure n'est construite avant un premier usage
utile ; faire de Chess Decision un bounded context distinct reste à valider.

## Conséquences

- Le README, `docs/slices.md`, le registre des traitements et `AGENTS.md` (§10.1 et §10.2) disent
  la nouvelle priorité. `AGENTS.md` ne change que sur demande explicite d'évoluer le playbook, ce
  que l'auteur a fait.
- Le registre des traitements n'attend de traitement de Chess Decision que s'il reprend.
- Une branche non fusionnée, `feat/confront-candidate-moves`, prépare la première slice de Chess
  Decision et son ADR 0026. Elle repose sur la priorité remplacée ici : son sort reste à décider.
- La gestion de club et les compétitions avancent toutes deux ; le principe d'une seule pull
  request à la fois s'applique entre elles.
- Le code de Chess Decision n'existant pas, rien n'est à retirer ni à déplacer.

## Options écartées

- **Garder Chess Decision comme cœur et traiter les compétitions en second** : contredit la
  décision de l'auteur.
- **Retirer Chess Decision de toute la documentation** : ses principes restent utiles s'il reprend,
  et l'ADR 0020 se lit comme l'historique de cette décision.
- **Réécrire l'ADR 0020** : un ADR accepté ne se réécrit pas ; seul son statut annonce le
  remplacement (voir [le README des ADR](README.md)).
