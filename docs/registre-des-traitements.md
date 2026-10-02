# Registre des traitements de données personnelles

Ce registre décrit les traitements de données personnelles de Chess Manager, au sens de
l'article 30 du RGPD. Il suit le code : chaque slice qui ajoute, stocke ou expose une donnée
personnelle le met à jour dans la même branche.

Chess Manager n'est pas en production. Ce registre décrit l'état du code et les décisions encore
ouvertes ; il n'est pas un avis juridique. Il sera relu avant la mise en production.

## Rôles

- **Responsable du traitement** : à décider. Si chaque club utilise Chess Manager pour gérer ses
  adhérents, chaque club (l'association) est responsable des traitements de ses adhérents.
- **Sous-traitant** : à décider. L'éditeur ou l'hébergeur de Chess Manager serait alors
  sous-traitant des clubs, avec un contrat de sous-traitance (article 28).

Ce choix dépend du mode de distribution de l'application et sera fait avant la mise en production.

Depuis le pivot vers Chess Decision (voir `docs/slices.md`), la gestion de club est en pause : les
traitements 1 et 2 restent décrits tant que leur code existe. Chess Decision ajoutera son propre
traitement dès qu'une slice stockera des données d'un joueur, par exemple ses analyses.

## Traitement 1 — Annuaire des clubs

| Rubrique | Contenu |
|---|---|
| Finalité | Recenser les clubs d'un comité, leur siège, leur salle de jeu et leurs horaires d'ouverture. |
| Slices | *Create a club*, *Consult the clubs of a committee*, *Define the opening hours of a club*, *Consult the opening hours of a club* |
| Personnes concernées | Aucune directement : les données décrivent des associations (personnes morales). |
| Données | Nom du club, comité départemental, identifiant FFE du club, commune, adresse du siège social, adresse de la salle de jeu, sessions (jour, heures, activité, lieu). |
| Stockage | PostgreSQL : tables `club` et `club_session`. |
| Destinataires | Toute personne qui consulte les clubs d'un comité. |
| Durée de conservation | À décider : tant que le club existe ? |
| Transferts hors UE | Aucun. |

**Point de vigilance.** Le siège social d'une petite association est souvent le domicile de son
président ou de son secrétaire. Son adresse devient alors une donnée personnelle, et elle est
consultable par tous. Question ouverte : faut-il rendre publique l'adresse du siège, ou seulement
celle de la salle de jeu ?

## Traitement 2 — Personnes en relation avec un club

| Rubrique | Contenu |
|---|---|
| Finalité | Suivre les personnes en relation avec un club : prospects, membres licenciés, partenaires. |
| Slices | *Record a person*, *Update a person*, *Register a prospect*, *Register a license*, *Register a partnership*, *Recognize an external player* |
| Personnes concernées | Prospects, membres, partenaires ; probablement des mineurs. |
| Données | Prénom, nom, adresse électronique (facultative), identifiant FIDE, classement Elo et classement précédent, statut dans le club (prospect, membre, partenaire), identifiant FFE personnel, licence FFE par saison (type A ou B). |
| Stockage | Aucun pour l'instant : les cas d'usage n'existent qu'avec des fakes en mémoire. |
| Destinataires | À décider avec le bounded context des droits et autorisations. |
| Base légale | À décider. Hypothèse : exécution du contrat d'adhésion pour un membre ; intérêt légitime ou consentement pour un prospect. |
| Durée de conservation | À décider, en particulier pour un prospect qui ne prend jamais de licence et pour un membre qui quitte le club. |
| Transferts hors UE | Aucun. |

## Données qui ne sont pas des traitements de données personnelles

- **Référentiel des communes (INSEE)** et **base officielle des codes postaux (La Poste)** :
  données publiques, sans donnée personnelle.

## Mesures de sécurité en place

- Accès SQL par JDBC, sans ORM.
- Intégration continue avec des actions épinglées par empreinte, des permissions en lecture seule
  et sans conservation des identifiants Git.
- Identifiants de la base réservés au conteneur de développement local (`compose.yaml`).

Pas encore en place : authentification, autorisations, chiffrement en transit, journalisation des
accès. Ces mesures relèvent du bounded context des droits et autorisations et du déploiement.

## Questions à trancher avant la mise en production

1. Qui est responsable du traitement : chaque club, ou l'éditeur de Chess Manager ?
2. Où l'application sera-t-elle hébergée ? Un hébergement dans l'Union européenne évite les
   transferts hors UE.
3. **Les mineurs.** Une date de naissance ou une catégorie d'âge révélera l'âge des joueurs. Les
   données de mineurs font partie des critères de risque retenus par la CNIL : il faudra vérifier
   si une analyse d'impact (AIPD) est nécessaire avant de stocker ces données.
4. Quelles durées de conservation pour les prospects, les anciens membres et les partenaires ?
5. Comment une personne exerce-t-elle ses droits (accès, rectification, effacement, opposition) ?
6. Les données de référence issues du site de la FFE ne peuvent pas être reprises sans son
   autorisation écrite (droit des bases de données, distinct du RGPD).

## Références

- Guide RGPD du développeur, CNIL : <https://www.cnil.fr/fr/guide-rgpd-du-developpeur>
- Le registre des activités de traitement, CNIL : <https://www.cnil.fr/fr/RGPD-le-registre-des-activites-de-traitement>
