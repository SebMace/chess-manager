# Business specifications only; these relations are not yet implemented in Java.
# Player represents a licensed person; member and partner describe club relations.
# A person who is a partner of a club is distinct from a club managed by the application.
# Entity design, prospect conversion and partner organizations remain open decisions.
@club_management @to_implement
Feature: Describe a person's relationship with each club
  As a club administrator
  I want to distinguish prospects, members and partners
  So that relationships do not change a person's identity or affiliation

  Background:
    Given Orléans and Olivet are clubs managed by the application
    And Gien is a club not managed by the application

  Scenario: Record an unlicensed person as a prospect
    Given Camille has neither an A license nor a B license
    When a prospect relationship between Camille and Orléans is recorded
    Then Camille is a prospect of Orléans
    And Camille is not a member of Orléans

  Scenario: A member of one managed club can be a partner of another
    Given Camille has an FFE identifier and a B license
    And Camille is a member of Olivet for the 2026-2027 season
    When a partnership between Camille and Orléans is recorded
    Then Camille is a partner of Orléans
    And Camille remains a member of Olivet for that season
    And both relationships refer to the same person

  Scenario: Recognize a player affiliated with an unmanaged club
    Given Camille has an FFE identifier and an A license
    And Camille is affiliated with Gien for the 2026-2027 season
    When Camille is recorded in the application
    Then Camille is represented as an external Player
    And Camille's affiliation with Gien is retained
    And Gien does not become a club managed by the application

  Scenario: An external player can become a partner without changing affiliation
    Given Camille is an external Player affiliated with Gien for the 2026-2027 season
    When a partnership between Camille and Orléans is recorded
    Then Camille is a partner of Orléans
    And Camille is not a member of Orléans for that season
    And Camille remains an external Player affiliated with Gien
    And the partnership refers to Camille's existing identity

  Scenario: One external player can have partnerships with several clubs
    Given Camille is an external Player affiliated with Gien for the 2026-2027 season
    And Camille is a partner of Orléans
    When a partnership between Camille and Olivet is recorded
    Then Camille is a partner of Orléans and Olivet
    And both partnerships refer to the same person
    And Camille remains affiliated with Gien
