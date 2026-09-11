# Only scenarios tagged @acceptance are connected to Cucumber step definitions.
# Missing-license rejection is implemented; the remaining scenarios still await bindings.
@club_management @to_implement
Feature: Register a licensed member of a club
  As a club administrator
  I want to register licensed people affiliated with my club as members
  So that membership reflects their license and seasonal affiliation

  Scenario Outline: Register a member with an FFE license
    Given Orléans is a club managed by the application
    And Camille has an FFE identifier and a license of type <type>
    And Camille is affiliated with Orléans for the 2026-2027 season
    When Camille is registered as a member of Orléans for that season
    Then Camille is a member of Orléans for that season
    And Camille retains the same internal identity and FFE identifier

    Examples:
      | type |
      | A    |
      | B    |

  @acceptance
  Scenario: Reject construction of a member without a license
    Given Camille has neither an A license nor a B license
    When an attempt is made to create Camille as a member
    Then member creation is rejected
    And no member is created

  Scenario: Reject construction of a member without an FFE identifier
    Given an A license is supplied for Camille without an FFE identifier
    When an attempt is made to create Camille as a member
    Then member creation is rejected
    And no member is created
