@club_management
Feature: Define the opening hours of a club
  As an administrator of Chess Manager
  I want to define the opening hours of a club
  So that everyone knows when the club is open

  @acceptance
  Scenario: A club opens every week on the same day
    Given "Loiret" is a departmental committee of the FFE
    And an administrator has created the club "U.S. Orléans.Echecs" with:
      | departmental committee     | Loiret                   |
      | FFE identifier             | G45001                   |
      | commune                    | Orléans                  |
      | registered office street   | 12 rue des Échecs        |
      | registered office postcode | 45000                    |
      | registered office town     | Orléans                  |
      | playing venue              | at the registered office |
    When an administrator defines that "U.S. Orléans.Echecs" opens every Friday from 15:00 to 17:00
    Then "U.S. Orléans.Echecs" opens every Friday from 15:00 to 17:00

  @acceptance
  Scenario: A session of the club is for an activity
    Given "Loiret" is a departmental committee of the FFE
    And an administrator has created the club "U.S. Orléans.Echecs" with:
      | departmental committee     | Loiret                   |
      | FFE identifier             | G45001                   |
      | commune                    | Orléans                  |
      | registered office street   | 12 rue des Échecs        |
      | registered office postcode | 45000                    |
      | registered office town     | Orléans                  |
      | playing venue              | at the registered office |
    When an administrator defines that "U.S. Orléans.Echecs" opens every Friday from 20:00 to 22:00 for free play
    Then "U.S. Orléans.Echecs" opens every Friday from 20:00 to 22:00 for free play
