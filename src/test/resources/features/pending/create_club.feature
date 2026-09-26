@club_management
Feature: Create a club
  As an administrator of Chess Manager
  I want to create a club
  So that its members can then be registered in it

  @acceptance
  Scenario: A created club keeps its information
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
