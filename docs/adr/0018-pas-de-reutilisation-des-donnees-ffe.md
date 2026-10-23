# 18. Aucune donnée FFE réutilisée sans autorisation

Date : 2026-09-26 (consignée le 2026-10-05)

## Statut

Acceptée

## Contexte

Les listes des ligues, des comités et des clubs, ainsi que les identifiants FFE, se trouvent sur
echecs.asso.fr. Les mentions légales de ce site (sections 5 et 7) interdisent leur reproduction
sans autorisation écrite et protègent ses bases de données. Le dépôt GitHub est public.

## Décision

Nous ne commitons aucune donnée issue de la FFE (ligues, comités, clubs, identifiants réels) tant
que la FFE ne l'a pas autorisé par écrit. Les données de développement sont fictives (`DEMO01`,
`DEMO02`, `DEMO03`). L'application n'apprend qu'un joueur s'est licencié dans un club qu'elle ne
gère pas que si le joueur le dit.

## Conséquences

- Les référentiels viennent de sources ouvertes (INSEE, La Poste) quand elles existent.
- Toute importation FFE attend une autorisation écrite ou un fichier officiel.
- Cette contrainte ne vient d'aucun refus de la FFE.

## Options écartées

- **Commiter une extraction du site de la FFE** : republierait publiquement des données protégées.
