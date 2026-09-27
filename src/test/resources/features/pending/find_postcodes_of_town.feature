@club_management
Feature: Find the postcodes of a town
  As an administrator of Chess Manager
  I want to be offered the postcodes that serve a town
  So that the postal addresses I give are those La Poste delivers to

  @acceptance
  Scenario: The postcodes offered for a town are those La Poste delivers it with
    When an administrator looks for the postcodes of the town "ORLEANS"
    Then the administrator is offered the postcode "45000"
    And the administrator is offered the postcode "45100"
    And the administrator is not offered the postcode "45240"
