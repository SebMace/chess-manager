# Code officiel géographique (COG) — communes

`v_commune_2026.csv` is the INSEE list of communes, municipal arrondissements, delegated
communes and associated communes at 1 January 2026, reused unmodified.

- Producer: Institut national de la statistique et des études économiques (Insee)
- Source: https://www.insee.fr/fr/information/8740222 (also listed on
  https://www.data.gouv.fr/datasets/code-officiel-geographique-1)
- Downloaded: 2026-09-26
- Licence: Licence Ouverte / Open Licence (Etalab)

Chess Manager only keeps the rows whose `TYPECOM` is `COM`: delegated communes (`COMD`) share
the code of their commune nouvelle. Communes of the overseas collectivities (e.g. Saint-Pierre-
et-Miquelon, New Caledonia) are published in a separate file and are not included yet.

To update: replace the file with the new yearly edition and adjust `InseeCommunes`.
