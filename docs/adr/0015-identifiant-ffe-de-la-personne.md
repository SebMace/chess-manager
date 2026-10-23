# 15. L'identifiant FFE appartient à la personne

Date : 2026-09-28 (consignée le 2026-10-05)

## Statut

Acceptée

## Contexte

L'identifiant FFE était rangé dans `FfeLicense`, par club et par saison. Le modèle permettait
donc deux identifiants pour un même joueur, et ne permettait pas de reconnaître un joueur déjà
connu. Or un joueur reçoit son identifiant FFE avec sa première licence A ou B et le garde
ensuite, quels que soient la saison et le club.

## Décision

Nous rattachons l'identifiant FFE à `Person`, comme une valeur optionnelle : son absence (une
personne jamais licenciée) est distincte d'une valeur invalide. Une personne qui arrive déjà
licenciée est créée par une fabrique nommée (`Person.licensedPlayer`), et un prospect le reçoit
par `takesFirstLicense`. La relation au club ne garde que le type de licence par saison ;
`FfeLicense` est supprimée.

## Conséquences

- Un joueur déjà connu est retrouvé par son identité, jamais par son nom.
- Seul un joueur licencié peut être membre.
- Un renouvellement se fait dans le même club ; une licence prise ailleurs est une mutation, qui
  n'a pas encore d'opération propre.

## Options écartées

- **L'identifiant dans chaque licence** : le modèle d'origine, pour la raison ci-dessus.
