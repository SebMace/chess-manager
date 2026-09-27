# Base officielle des codes postaux

`laposte_hexasmal.csv` is La Poste's official list of postcodes, reused unmodified. It links each
postcode to the INSEE code of a commune, the name of that commune and its delivery town
(`Libellé_d_acheminement`), sometimes with a locality or delegated commune (`Ligne_5`).

- Producer: La Poste
- Source: https://www.data.gouv.fr/datasets/base-officielle-des-codes-postaux/
  (file: https://data.laposte.fr/data-fair/api/v1/datasets/laposte-hexasmal/raw)
- Downloaded: 2026-09-27 (file last modified on 2026-09-08)
- Licence: Licence Ouverte / Open Licence version 2.0 (Etalab)

The file is encoded in ISO-8859-1 and separated by semicolons. Names are written in capitals,
without accents, as La Poste expects them on the last line of an address. A postcode may serve
several delivery towns, and a delivery town may have several postcodes.

To update: replace the file with the new edition.
