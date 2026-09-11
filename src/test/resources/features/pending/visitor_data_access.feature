# Business specifications only; not implemented.
# This feature belongs to Rights and Authorization, not member registration.
# Exact visible data, approval integration and revocation remain undecided.
@authorization @to_implement
Feature: Allow an approved visitor to view club data
  As a visitor approved by the club president
  I want to consult the club data made available to visitors
  So that I can follow the club without changing my affiliation

  Scenario: An approved prospect can consult visitor data
    Given Camille is a prospect of Orléans
    And the president of Orléans has approved Camille as a visitor
    When Camille requests Orléans data available to visitors
    Then access to that data is allowed

  Scenario: External affiliation does not prevent approved access
    Given Camille is affiliated with Gien for the 2026-2027 season
    And Camille is a partner of Orléans
    And the president of Orléans has approved Camille as a visitor
    When Camille requests Orléans data available to visitors
    Then access to that data is allowed
    And Camille's affiliation remains unchanged

  Scenario Outline: A relationship does not automatically grant visitor access
    Given Camille is a <relationship> of Orléans
    And Camille has not been approved as a visitor by the president of Orléans
    And Camille has no other authorization to view the requested data
    When Camille requests Orléans data available to visitors
    Then access to that data is denied

    Examples:
      | relationship |
      | prospect     |
      | partner      |
