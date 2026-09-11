# Business specifications only. MemberTests covers the underlying identity,
# FIDE and rating rules; moving them to Player is not implemented yet.
@club_management @existing_domain_rules
Feature: Identify a licensed person independently of club relationships
  As a club administrator
  I want a stable internal identity for each player
  So that names and club relationships do not define their identity

  Scenario: Record a player's name with an internal identity
    Given a licensed player with a supplied internal identity
    When the player is recorded with first name "Camille" and last name "Martin"
    Then the supplied internal identity is retained
    And the recorded first name is "Camille"
    And the recorded last name is "Martin"

  Scenario: FIDE identification is optional
    Given a player has an FFE identifier and an A license
    And the player has no FIDE identifier
    When the player is recorded
    Then the player has an internal identity
    And no FIDE identifier is required

  Scenario: Reject a missing internal identity
    When a player is created without an internal identity
    Then creation is rejected

  Scenario: Reject an internal identity without a value
    When an internal identity is created without a UUID
    Then the identity is rejected

  Scenario: Equal internal identities identify the same person
    Given two player representations have the same internal identity
    And they have different names
    When their identities are compared
    Then they represent the same person

  Scenario: Homonyms with different identities remain distinct
    Given two players are both named "Camille Martin"
    And they have different internal identities
    When their identities are compared
    Then they represent different people

  Scenario: Reject replacement of an assigned FIDE identifier
    Given a player has FIDE identifier 641839
    When FIDE identifier 1503014 is assigned to that player
    Then the assignment is rejected

  Scenario Outline: Reject a non-positive FIDE identifier
    When a FIDE identifier is created with value <value>
    Then the identifier is rejected

    Examples:
      | value |
      | -1    |
      | 0     |

  Scenario: Update a rating
    Given a player has an Elo rating of 1500
    When an Elo rating of 1600 is recorded
    Then the player's Elo rating is 1600

  Scenario: Reject a negative rating
    When an Elo rating of -1 is created
    Then the rating is rejected
