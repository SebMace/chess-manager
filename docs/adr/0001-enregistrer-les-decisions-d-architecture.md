# 1. Enregistrer les décisions d'architecture

Date : 2026-10-05

## Statut

Acceptée

## Contexte

Chess Manager sert aussi de terrain d'apprentissage du design. Ses décisions sont dispersées
entre le README (en anglais), `docs/slices.md`, `AGENTS.md`, un rapport de refactoring et plus de
450 commits. Le *comment* y figure, mais le *pourquoi* et les options écartées se perdent, alors
que ce sont eux qui permettent de remettre une décision en cause en connaissance de cause.

## Décision

Nous consignons chaque décision d'architecture significative dans un ADR au format de Michael
Nygard (Statut, Contexte, Décision, Conséquences), complété d'une section « Options écartées ».
Les ADR sont rédigés en français, numérotés, et rangés dans `docs/adr/`. Un ADR accepté n'est
plus réécrit : on le remplace par un nouvel ADR.

Une décision mérite un ADR quand elle touche une frontière (bounded context, agrégat, port), une
technologie, une dépendance de production, une source de données ou une règle de travail
difficile à défaire.

## Conséquences

- Le README et `docs/slices.md` décrivent le code ; ils renvoient aux ADR pour les raisons.
- Les ADR 0002 à 0020 sont rétroactifs : quand une alternative n'a pas été consignée au moment de
  la décision, l'ADR le dit plutôt que de l'inventer.
- Rédiger un ADR fait partie de la branche qui applique la décision.

## Options écartées

- **MADR** : plus structuré (critères, options pondérées), mais plus long à écrire et à lire
  pour un projet mené seul.
- **ADR en anglais** : aligné sur le code et le README, mais moins naturel pour discuter des
  raisons ; le backlog est déjà en français.
