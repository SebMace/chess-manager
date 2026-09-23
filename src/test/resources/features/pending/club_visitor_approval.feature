# Business specifications only; not implemented.
# Club Management owns the president's business approval of a visitor.
# Technical permissions and authentication belong to Rights and Authorization.
@club_management @to_implement
@visitor_approval
Feature: Approve a club visitor
  As the president of a club
  I want to approve a prospect or partner as a visitor
  So that the club explicitly recognizes that person's visitor status

  Scenario: Approve a prospect as a visitor
    Given Camille is an unlicensed prospect of Orléans
    When the president of Orléans approves Camille as a visitor
    Then Camille has approved visitor status for Orléans
    And Camille remains a prospect rather than a member

  Scenario: Approve a partner affiliated with another club
    Given Camille is a partner of Orléans
    And Camille is affiliated with Gien for the 2026-2027 season
    When the president of Orléans approves Camille as a visitor
    Then Camille has approved visitor status for Orléans
    And Camille remains affiliated with Gien for that season
