# Business specifications only; not implemented.
# Affiliation is always for a given season.
# A club is external to a player, for a season, when the player is neither affiliated with it,
# nor its prospect, nor its partner for that season.
# An external club is not a club not managed by the application: the latter is a property of the
# club alone, whatever the player.
@club_management @to_implement
@change_club
Feature: Change club between seasons
  As a player changing club
  I want the club I leave to become external to me by default
  So that I keep a relationship with it only if I ask for it and its president approves

  Scenario: The club a player leaves becomes external to them by default
    Given Orléans and Olivet are clubs managed by the application
    And Camille is affiliated with Olivet for the 2025-2026 season
    When Camille is affiliated with Orléans for the 2026-2027 season
    Then Olivet is an external club for Camille for the 2026-2027 season

  Scenario: A player becomes a partner of the club they left on request, with its president's approval
    Given Orléans and Olivet are clubs managed by the application
    And Camille is affiliated with Olivet for the 2025-2026 season
    And Camille is affiliated with Orléans for the 2026-2027 season
    And Camille explicitly asks to become a partner of Olivet
    When the president of Olivet approves Camille's partnership request
    Then Camille is a partner of Olivet
    And Olivet is not an external club for Camille for the 2026-2027 season
    And Camille's club for the 2026-2027 season is "Orléans"

  Scenario: A request alone does not make a player a partner of the club they left
    Given Orléans and Olivet are clubs managed by the application
    And Camille is affiliated with Olivet for the 2025-2026 season
    And Camille is affiliated with Orléans for the 2026-2027 season
    When Camille explicitly asks to become a partner of Olivet
    Then Camille is not a partner of Olivet
    And Olivet is an external club for Camille for the 2026-2027 season
