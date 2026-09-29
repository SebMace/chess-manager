# Executable acceptance specifications. FfeMembershipTests, PersonTests and FfeIdTests cover the core rules.
# The FFE identifier belongs to the person; a license is recorded with a club for a season.
# Identifiers are synthetic: no official FFE pattern is asserted.
@club_management @existing_domain_rules
@licenses
Feature: Identify a licensed player by their personal FFE identifier
  As a club administrator
  I want every licensed player to have a personal and stable FFE identifier
  So that a player keeps the same FFE identity whatever their club and season

  Scenario Outline: A first license of either type gives the player their FFE identifier
    Given Camille is a prospect of Orléans
    When Camille takes her first license, of type <type>, at Orléans under the FFE identifier "A12345"
    Then Camille's FFE identifier is "A12345"
    And Camille is a member of Orléans for the season with a license of type <type>

    Examples:
      | type |
      | A    |
      | B    |

  Scenario: A licensed player keeps their FFE identifier for good
    Given Camille has taken her first license under the FFE identifier "A12345"
    When a first license is requested again for Camille under the FFE identifier "B54321"
    Then the request is rejected
    And Camille's FFE identifier remains "A12345"

  Scenario: A renewal keeps the FFE identifier
    Given Camille has taken her first license at Orléans under the FFE identifier "A12345"
    When Camille renews her license at Orléans for the next season
    Then Camille's FFE identifier remains "A12345"

  Scenario: Only a licensed player can be a member
    Given Camille is a prospect of Orléans who has never been licensed
    Then Camille has no FFE identifier
    And Camille is not a member of Orléans

  Scenario Outline: Reject an identifier without a value
    When an FFE identifier is created with "<value>"
    Then the FFE identifier is rejected

    Examples:
      | value            |
      | no value         |
      | an empty string  |
      | only spaces      |
      | only tabs        |
      | only line breaks |

  Scenario: Preserve the identifier as supplied
    When the FFE identifier "A00123" is recorded
    Then its value remains exactly "A00123"
