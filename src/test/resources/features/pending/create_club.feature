@club_management
Feature: Create a club
  As an administrator of Chess Manager
  I want to create a club
  So that its members can then be registered in it

  @acceptance
  Scenario: A created club belongs to its departmental committee
    Given "Loiret" is a departmental committee of the FFE
    When an administrator creates the club "U.S. Orléans.Echecs" in the departmental committee "Loiret"
    Then "U.S. Orléans.Echecs" is a club managed by the application
    And "U.S. Orléans.Echecs" belongs to the departmental committee "Loiret"
