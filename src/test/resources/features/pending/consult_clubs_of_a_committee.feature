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
