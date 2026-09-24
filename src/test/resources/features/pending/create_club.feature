@club_management
Feature: Create a club
  As an administrator of Chess Manager
  I want to create a club
  So that its members can then be registered in it

  @acceptance
  Scenario: A created club is managed by Chess Manager
    When an administrator creates the club "Montargis"
    Then "Montargis" is a club managed by Chess Manager
