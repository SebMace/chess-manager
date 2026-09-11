# Business specifications only. AffiliationsTests covers the underlying rules.
# The licensed Player vocabulary awaits the model revision.
# Valid season lengths have not been agreed yet.
@club_management @existing_domain_rules
Feature: Preserve seasonal club affiliations
  As a club administrator
  I want one club per player and season
  So that affiliation history remains consistent

  Background:
    Given Camille is a licensed player

  Scenario: Affiliate a player for a season
    Given Camille has no affiliation for the 2026-2027 season
    When Camille is affiliated with Orléans for that season
    Then Camille's club for the 2026-2027 season is Orléans

  Scenario: Preserve history when joining another club next season
    Given Camille is affiliated with Orléans for the 2026-2027 season
    When Camille is affiliated with Olivet for the 2027-2028 season
    Then Camille's club for the 2026-2027 season is Orléans
    And Camille's club for the 2027-2028 season is Olivet

  Scenario: Do not implicitly renew an affiliation
    Given Camille is affiliated with Orléans for the 2026-2027 season
    When Camille's club for the 2027-2028 season is requested
    Then no affiliation is returned for that season

  Scenario: Repeating the same affiliation has no effect
    Given Camille is affiliated with Orléans for the 2026-2027 season
    When that affiliation is requested again
    Then the request succeeds without changing the affiliation

  Scenario: Reject another club for the same season
    Given Camille is affiliated with Orléans for the 2026-2027 season
    When an affiliation with Olivet is requested for the same season
    Then the request is rejected
    And Camille's club for that season remains Orléans

  Scenario Outline: Reject an incomplete affiliation
    Given Camille has no recorded affiliations
    When an affiliation is requested without a <missing_information>
    Then the request is rejected
    And Camille still has no recorded affiliations

    Examples:
      | missing_information |
      | club                |
      | season              |
