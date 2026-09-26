@club_management
Feature: Create a club
  As an administrator of Chess Manager
  I want to create a club
  So that its members can then be registered in it

  @acceptance
  Scenario: A club is created with its information
    Given "Loiret" is a departmental committee of the FFE
    When an administrator creates the club "U.S. Orléans.Echecs" with:
      | departmental committee | Loiret  |
      | FFE identifier         | G45001  |
      | commune                | Orléans |
    Then "U.S. Orléans.Echecs" is a club managed by the application
    And "U.S. Orléans.Echecs" belongs to the departmental committee "Loiret"
    And the FFE identifier of "U.S. Orléans.Echecs" is "G45001"
    And the commune of "U.S. Orléans.Echecs" is "Orléans"

  @acceptance
  Scenario: A club cannot be created without its FFE identifier
    Given "Loiret" is a departmental committee of the FFE
    When an administrator creates the club "U.S. Orléans.Echecs" with:
      | departmental committee | Loiret  |
      | commune                | Orléans |
    Then the club "U.S. Orléans.Echecs" is not created

  @acceptance
  Scenario: Two clubs cannot share the same FFE identifier
    Given "Loiret" is a departmental committee of the FFE
    And an administrator has created the club "U.S. Orléans.Echecs" with:
      | departmental committee | Loiret  |
      | FFE identifier         | G45001  |
      | commune                | Orléans |
    When an administrator creates the club "Échiquier Orléanais" with:
      | departmental committee | Loiret  |
      | FFE identifier         | g45001  |
      | commune                | Orléans |
    Then the club "Échiquier Orléanais" is not created
    And the administrator is told that the FFE identifier "G45001" is already used

  @acceptance
  Scenario: A club cannot be created without its commune
    Given "Loiret" is a departmental committee of the FFE
    When an administrator creates the club "U.S. Orléans.Echecs" with:
      | departmental committee | Loiret |
      | FFE identifier         | G45001 |
    Then the club "U.S. Orléans.Echecs" is not created

  @acceptance
  Scenario: A club cannot be created in a commune outside the department of its committee
    Given "Loiret" is a departmental committee of the FFE
    When an administrator creates the club "Olivet – La Tour prend garde" with:
      | departmental committee | Loiret           |
      | FFE identifier         | G45002           |
      | commune                | Olivet (Mayenne) |
    Then the club "Olivet – La Tour prend garde" is not created

  @acceptance
  Scenario: The communes offered for a club are those of the department of its committee
    Given "Loiret" is a departmental committee of the FFE
    When an administrator looks for the communes of a club of the departmental committee "Loiret"
    Then the administrator is offered the commune "Olivet"
    And the administrator is offered the commune "Saint-Pryvé-Saint-Mesmin"
    And the administrator is not offered the commune "Olivet (Mayenne)"
