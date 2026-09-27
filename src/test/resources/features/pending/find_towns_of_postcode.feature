@club_management
Feature: Find the towns of a postcode
  As an administrator of Chess Manager
  I want to be offered the towns a postcode serves
  So that the postal addresses I give are those La Poste delivers to

  @acceptance
  Scenario: The towns offered for a postcode are those La Poste delivers with it
    When an administrator looks for the towns of the postcode "45240"
    Then the administrator is offered the town "LA FERTE ST AUBIN"
    And the administrator is offered the town "SENNELY"
    And the administrator is not offered the town "ORLEANS"
