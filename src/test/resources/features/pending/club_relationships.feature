# Prospect registration and license conversion are connected to application use cases.
# A license affiliates the person for the current season.
# Other prospect relationships end unless a partnership is explicitly requested.
# External clubs and standalone partnership registration remain future scenarios.
@club_management
@relationships
Feature: Describe a person's relationship with each club
  As a club administrator
  I want to distinguish prospects, members and partners
  So that relationships do not change a person's identity or affiliation

  @acceptance
  Scenario: Register an unlicensed prospect with an email address
    Given the administrator acts on behalf of Orléans
    And Camille Martin has no FFE license
    When the administrator registers prospect "Camille" "Martin" with email "camille@example.org"
    Then prospect "Camille" "Martin" is recorded for Orléans
    And the recorded prospect's email is "camille@example.org"

  @acceptance
  Scenario: Reject prospect registration for a person with an A license
    Given Orléans is a club managed by the application
    And Camille has an FFE identifier and an A license at Orléans for the current season
    When an attempt is made to record Camille as a prospect of Orléans
    Then prospect registration is rejected

  @acceptance
  Scenario Outline: Licensing ends other prospect relationships by default
    Given Camille is a prospect of Orléans and Olivet
    When Camille registers an FFE license of type "<type>" at Orléans for the current season
    Then Camille is a member of Orléans for the current season
    And Camille is no longer a prospect of Orléans
    And Camille has no relationship with Olivet

    Examples:
      | type |
      | A    |
      | B    |

  @acceptance
  Scenario: Keep another relationship as a partner only on explicit request
    Given Camille is a prospect of Orléans and Olivet
    And Camille explicitly wishes to remain a partner of Olivet
    When Camille registers an FFE license of type "A" at Orléans for the current season
    Then Camille is a member of Orléans for the current season
    And Camille is a partner of Olivet
    And Camille is no longer a prospect of Olivet
    And Camille is not affiliated with Olivet for the current season

  @to_implement
  Scenario: A member of one managed club can be a partner of another
    Given Orléans and Olivet are clubs managed by the application
    And Camille has an FFE identifier and a B license
    And Camille is a member of Olivet for the 2026-2027 season
    When a partnership between Camille and Orléans is recorded
    Then Camille is a partner of Orléans
    And Camille remains a member of Olivet for that season
    And both relationships refer to the same person

  @to_implement
  Scenario: Recognize a player affiliated with an unmanaged club
    Given Gien is a club not managed by the application
    And Camille has an FFE identifier and an A license
    And Camille is affiliated with Gien for the 2026-2027 season
    When Camille's club profile is consulted
    Then Camille is represented as an external Player
    And Camille's affiliation with Gien is retained
    And Gien does not become a club managed by the application

  @to_implement
  Scenario: An external player can become a partner without changing affiliation
    Given Orléans is a club managed by the application
    And Gien is a club not managed by the application
    And Camille is an external Player affiliated with Gien for the 2026-2027 season
    When a partnership between Camille and Orléans is recorded
    Then Camille is a partner of Orléans
    And Camille is not a member of Orléans for that season
    And Camille remains an external Player affiliated with Gien
    And the partnership refers to Camille's existing identity

  @to_implement
  Scenario: One external player can have partnerships with several clubs
    Given Orléans and Olivet are clubs managed by the application
    And Gien is a club not managed by the application
    And Camille is an external Player affiliated with Gien for the 2026-2027 season
    And Camille has requested a partnership with Orléans
    When a partnership between Camille and Olivet is recorded
    Then Camille is a partner of Orléans and Olivet
    And both partnerships refer to the same person
    And Camille remains affiliated with Gien
