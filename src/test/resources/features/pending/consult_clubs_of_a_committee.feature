@club_management
Feature: Consult the clubs of a departmental committee
  As an administrator of Chess Manager
  I want to see the clubs managed by the application in a departmental committee
  So that I can find the club I have to work on

  @acceptance
  Scenario: A club created in a committee is among the clubs of that committee
    Given "Loiret" is a departmental committee of the FFE
    And an administrator has created the club "U.S. Orléans.Echecs" with:
      | departmental committee     | Loiret                   |
      | FFE identifier             | G45001                   |
      | commune                    | Orléans                  |
      | registered office street   | 12 rue des Échecs        |
      | registered office postcode | 45000                    |
      | registered office town     | Orléans                  |
      | playing venue              | at the registered office |
    When an administrator consults the clubs of the departmental committee "Loiret"
    Then the administrator is shown the club "U.S. Orléans.Echecs"

  @acceptance
  Scenario: A club created in another committee is not among the clubs of the committee
    Given "Loiret" is a departmental committee of the FFE
    And "Mayenne" is a departmental committee of the FFE
    And an administrator has created the club "Échiquier fictif de Mayenne" with:
      | departmental committee     | Mayenne                  |
      | FFE identifier             | G53999                   |
      | commune                    | Olivet (Mayenne)         |
      | registered office street   | 2 rue de l'Église        |
      | registered office postcode | 53410                    |
      | registered office town     | Olivet                   |
      | playing venue              | at the registered office |
    When an administrator consults the clubs of the departmental committee "Loiret"
    Then the administrator is not shown the club "Échiquier fictif de Mayenne"

  @acceptance
  Scenario: The administrator is shown the commune and the FFE identifier of a club
    Given "Loiret" is a departmental committee of the FFE
    And an administrator has created the club "U.S. Orléans.Echecs" with:
      | departmental committee     | Loiret                   |
      | FFE identifier             | G45001                   |
      | commune                    | Orléans                  |
      | registered office street   | 12 rue des Échecs        |
      | registered office postcode | 45000                    |
      | registered office town     | Orléans                  |
      | playing venue              | at the registered office |
    When an administrator consults the clubs of the departmental committee "Loiret"
    Then the administrator is shown the club "U.S. Orléans.Echecs" located in "Orléans" with the FFE identifier "G45001"
