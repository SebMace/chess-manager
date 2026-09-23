# Correction du modèle des relations de club

## Décisions retenues

Les précisions de cette session remplacent les décisions initiales qui permettaient
à une personne de rester prospect ailleurs après sa licence et excluaient `PARTNER` :

- La licence affilie au club pour la saison en cours, même en cours de saison.
- Les autres liens de prospect sont rompus par défaut.
- Seul un souhait explicite maintient un autre lien comme `PARTNER`.
- Une personne affiliée ne peut être enregistrée comme prospect dans un autre club.

## Inventaire initial

| Zone | Éléments trouvés |
| --- | --- |
| Production spécialisée prématurément | `domain/prospect/ProspectRelationship.java`, record avec UUID personnel brut et ClubId. Pas de classe `Prospect` ni de repository de production. |
| Application | `application/prospect/RegisterProspect.java`, vide. |
| Identité et affiliations | `domain/member/entities/Member.java`, `MemberId`, `ClubId`, `Season`, `FfeLicense`, `FfeId`, `FfeLicenseType`, `FideId`, `EloRating`. `Member` mélangeait données personnelles, licence et affiliations. Aucun `Person` ni `Club`. |
| Tests du prospect | `ProspectRelationshipTests` (3), `InMemoryProspectRepositoryTests` (5) et leur fake. |
| Autres tests JUnit | `MemberTests` (12), `FfeMembershipTests` (9), `FfeIdTests` (6), `AffiliationsTests` (7), `SeasonTests` (1). |
| Cucumber | `RunCucumberTests`, `ProspectRegistrationSteps`, `MemberRegistrationSteps`. Deux actions levaient `PendingException` ; l'ancien rejet d'un membre sans licence construisait directement `Member`. |
| Gherkin lié | `club_relationships`, `register_member`, `player_identity`, `ffe_licenses`, `seasonal_affiliations`, `club_visitor_approval`, `visitor_data_access`. Seuls trois scénarios étaient sélectionnés par `@acceptance`. |

`AGENTS.md` a été lu ; aucun `tdd-seb.md` n'a été trouvé dans le dépôt.
Les modifications préexistantes ont été prises comme point de départ. Les changements
préexistants du POM et les sections SonarQube/JaCoCo du README sont conservés.

## Changements

- Renommage progressif de `ProspectRelationship` en `ClubRelationship`, puis passage
  à une classe d'agrégat immuable dont l'égalité repose sur les deux identifiants.
- `PersonId` remplace `MemberId`. Extraction des données personnelles dans `Person`,
  migration des tests correspondants puis suppression de l'ancien `Member` devenu inutilisé.
- Ajout de `Club`, avec identité propre et nom, sans repository inutilisé.
- Ajout des statuts `PROSPECT`, `MEMBER`, `PARTNER`, de `registerLicense` et de l'historique
  des licences par saison. Aucun setter de statut, aucune collection de relations sur
  `Person` ou `Club`, aucun objet Person/Club dans la relation.
- `RegisterProspect` conserve son nom métier et enregistre les données personnelles
  ainsi que la relation. `RegisterLicense` orchestre affiliation et conséquences sur
  les autres liens. `ClubAffiliations` exprime les règles portant sur plusieurs clubs.
- Seuls les agrégats dont un use case sauvegarde l'état ont un repository : Person
  et ClubRelationship. Les implémentations in-memory restent dans les tests.
- Les steps Cucumber passent par un driver de scénario qui appelle les use cases.
  Le vocabulaire « prospect » et les deux classes de steps sont conservés.
- Les spécifications de clubs externes, partenariats autonomes et droits visiteurs
  restent hors de cette tranche.

Les tests d'identité sont désormais dans `person/PersonTests`, ceux de licence dans
`relationship/FfeMembershipTests`. Les sept tests d'affiliation passent maintenant
par le use case et relisent les relations persistées. Le rejet d'une licence absente
est couvert par `ClubRelationshipTests` et par Cucumber, en remplacement du test de
l'ancien constructeur `Member`.

## Commandes et preuves TDD

Tous les appels Maven concluants ont utilisé le JDK déjà installé, sans modifier le POM :

```sh
JAVA_HOME=/Users/SMA/Library/Java/JavaVirtualMachines/jdk-26.0.2.1+1/Contents/Home mvn -o test
```

Dans le tableau, `mvn` abrège ce même préfixe JAVA_HOME ; les exécutions répétées
après les petits refactorings sont regroupées.

| Commande | Résultat observé |
| --- | --- |
| `git status --short`, `git rev-parse --show-toplevel`, `rg --files` et lectures ciblées | Inventaire des instructions, sources, tests et changements existants. Aucun commit ni changement de branche. |
| `mvn -version` | Maven 3.9.16, Java 25 par défaut ; le POM demande Java 26. |
| `mvn test` initial sans ajustement | Échec d'écriture des métadonnées dans le cache Maven, avant exécution des tests. |
| `mvn -o test` initial avec Java 26 | 46 tests, 44 réussis, 2 erreurs `PendingException` Cucumber. |
| `mvn -o '-Dtest=prospect.*,member.*,affiliation.*' test` | Sélecteur incorrect : aucun test sélectionné. Remplacé par le sélecteur suivant, sans désactiver l'erreur Maven. |
| `mvn -o '-Dtest=*Tests,!RunCucumberTests' test` | Base de 43 tests JUnit verte ; relancée après les petits refactorings et cycles internes pendant la boucle Cucumber initialement rouge. |
| `mvn -o -Dtest=ClubRelationshipTests test` | RED sur les API manquantes de statut/licence, puis GREEN. RED comportemental : les paramètres absents n'étaient pas rejetés ; GREEN après validation. RED sur l'historique perdu ; GREEN après conservation par saison. |
| `mvn -o -Dtest=RegisterLicenseTests test` | RED sur le use case manquant ; puis RED comportemental sur le lien d'Olivet non rompu et sur la seconde affiliation non rejetée. Ajout du maintien explicite comme partenaire. GREEN après chaque cycle. |
| `mvn -o -Dtest=RegisterProspectTests test` | RED sur l'enregistrement absent, puis sur le rejet manquant des personnes déjà licenciées A/B. GREEN après chaque cycle. |
| `mvn -o -Dtest=ClubTests test` | RED sur le type manquant, puis sur l'identité absente non rejetée ; GREEN. |
| `mvn -o -Dtest=RegisterLicenseTests#should_save_membership_for_the_current_season test` avec `save()` temporairement retiré | Échec attendu : `expected: <MEMBER> but was: <PROSPECT>`. Code restauré dans un bloc `finally`, puis suite complète verte. |
| `mvn -o clean test` | Suite verte après suppression de l'ancien Member ; aucune dépendance à des classes compilées résiduelles. |
| `mvn -o test` après raccordement Cucumber et refactorings | 64 tests réussis : 58 JUnit et 6 scénarios Cucumber. |
| `mvn -o clean verify` final | 64 tests réussis, JAR construit et rapport JaCoCo généré. Aucune analyse Sonar distante exécutée. |
| `git diff --check` final et comparaison des fichiers avec l'état initial | Pas d'erreur d'espacement ; POM et documentation Sonar préexistante inchangés. |

La migration mécanique des tests FFE a occasionné une erreur de parenthésage des
appels ; elle a été corrigée et la suite remise au vert avant le cycle sur l'historique.
Aucune assertion n'a été affaiblie pour corriger cette erreur.

## Limites métier explicites

- La saison en cours est un argument de configuration des use cases. Ses bornes
  calendaires attendent une réponse métier ; aucun calendrier n'a été inventé.
- Le statut enregistré décrit la dernière transition de la relation ; l'affiliation
  d'une saison se vérifie par la licence de cette saison. Aucune transition automatique
  de statut à l'expiration d'une saison n'est définie.
- Cette tranche couvre le maintien explicite d'un ancien lien de prospect comme
  partenaire lors de la prise de licence. Elle ne définit pas le cycle de vie général
  des partenaires, ni une compatibilité MEMBER/PARTNER dans un même club.
- Aucun stockage de production, migration de données, événement ou dépendance ajouté.
