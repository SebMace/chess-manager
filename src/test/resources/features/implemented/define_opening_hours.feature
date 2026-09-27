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

  @acceptance
  Scenario: A club opens during several sessions, each for its activity
    Given "Loiret" is a departmental committee of the FFE
    And an administrator has created the club "U.S. Orléans.Echecs" with:
      | departmental committee     | Loiret                   |
      | FFE identifier             | G45001                   |
      | commune                    | Orléans                  |
      | registered office street   | 12 rue des Échecs        |
      | registered office postcode | 45000                    |
      | registered office town     | Orléans                  |
      | playing venue              | at the registered office |
    When an administrator defines the opening hours of "U.S. Orléans.Echecs":
      | day      | from  | to    | activity           |
      | Friday   | 20:00 | 22:00 | free play          |
      | Saturday | 15:00 | 17:00 | adult lessons      |
      | Monday   | 20:00 | 22:00 | children's lessons |
    Then "U.S. Orléans.Echecs" opens during these sessions:
      | day      | from  | to    | activity           |
      | Friday   | 20:00 | 22:00 | free play          |
      | Saturday | 15:00 | 17:00 | adult lessons      |
      | Monday   | 20:00 | 22:00 | children's lessons |

  @acceptance
  Scenario: A session takes place at the playing venue of the club by default
    Given "Loiret" is a departmental committee of the FFE
    And an administrator has created the club "U.S. Orléans.Echecs" with:
      | departmental committee     | Loiret            |
      | FFE identifier             | G45001            |
      | commune                    | Orléans           |
      | registered office street   | 12 rue des Échecs |
      | registered office postcode | 45000             |
      | registered office town     | Orléans           |
      | playing venue street       | 5 rue du Roi      |
      | playing venue postcode     | 45100             |
      | playing venue town         | Orléans           |
    When an administrator defines that "U.S. Orléans.Echecs" opens every Friday from 20:00 to 22:00 for free play
    Then the session of "U.S. Orléans.Echecs" every Friday from 20:00 to 22:00 takes place at its playing venue

  @acceptance
  Scenario: A session takes place at another address than the playing venue
    Given "Loiret" is a departmental committee of the FFE
    And an administrator has created the club "U.S. Orléans.Echecs" with:
      | departmental committee     | Loiret            |
      | FFE identifier             | G45001            |
      | commune                    | Orléans           |
      | registered office street   | 12 rue des Échecs |
      | registered office postcode | 45000             |
      | registered office town     | Orléans           |
      | playing venue street       | 5 rue du Roi      |
      | playing venue postcode     | 45100             |
      | playing venue town         | Orléans           |
    When an administrator defines that "U.S. Orléans.Echecs" opens every Monday from 20:00 to 22:00 for children's lessons at:
      | street   | 3 rue de l'École |
      | postcode | 45000            |
      | town     | Orléans          |
    Then the session of "U.S. Orléans.Echecs" every Monday from 20:00 to 22:00 takes place at:
      | street   | 3 rue de l'École |
      | postcode | 45000            |
      | town     | Orléans          |
