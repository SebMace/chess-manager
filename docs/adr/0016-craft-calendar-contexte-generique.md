# 16. Craft Calendar, un contexte générique

Date : 2026-09-27 (consignée le 2026-10-05)

## Statut

Acceptée. L'extraction du contexte est différée.

## Contexte

Les horaires d'ouverture d'un club demandent un calendrier. Un calendrier générique servirait
aussi d'autres applications (conseil en gestion de patrimoine, agenda personnel). Aucun exemple
ne demande pourtant encore un vrai comportement de calendrier (dates d'occurrence, exclusions,
export).

## Décision

Craft Calendar sera un bounded context générique qui ignore tout des échecs. Dans la carte des
contextes, il est en amont, comme Open Host Service avec un Published Language ; Club Management
est en aval, derrière une couche anticorruption : son port `ClubCalendar` et un adaptateur
traduisent les sessions d'un club dans le langage publié.

En attendant un exemple qui le demande, Club Management enregistre lui-même les sessions
(`JdbcClubCalendar`) derrière ce même port. L'extraction suivra quatre étapes : appel en
processus, module Maven séparé, API HTTP vérifiée par contrat, déploiement séparé.

## Conséquences

- Le cas d'usage et le domaine ne changeront pas lors de l'extraction ; seul le câblage changera.
- Seul l'adaptateur pourra dépendre de Craft Calendar, et Craft Calendar jamais de
  `clubmanagement` ; ArchUnit le vérifiera quand le package existera.
- Le passage du temps (dates de relance des licences) sera signalé par Craft Calendar dans son
  langage, et traduit en décisions par Club Management.

## Options écartées

- **Conformist** : le modèle du club finirait par parler de récurrences.
- **Shared Kernel** : lierait les cycles de vie des deux contextes et empêcherait l'extraction.
- **Partnership** : suppose deux équipes qui livrent ensemble, absurde pour un contexte générique.
- **Separate Ways** : le besoin est réel.
- **Créer Craft Calendar tout de suite** : il n'aurait stocké que les mêmes données sous d'autres
  noms.
