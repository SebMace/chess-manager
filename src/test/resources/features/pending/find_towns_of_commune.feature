@club_management
Feature: Find the towns of a commune
  As an administrator of Chess Manager
  I want to be offered the towns and postcodes of the commune of a club
  So that I do not have to type its postal addresses again

  @acceptance
  Scenario: The town offered for a commune is the one La Poste delivers it as, with its postcodes
    When an administrator looks for the towns of the commune "Orléans"
    Then the administrator is offered the town "ORLEANS" with the postcodes "45000, 45100"

  @acceptance
  Scenario: The town offered for a commune ignores the communes of the same name
    When an administrator looks for the towns of the commune "Olivet"
    Then the administrator is offered the town "OLIVET" with the postcodes "45160"
