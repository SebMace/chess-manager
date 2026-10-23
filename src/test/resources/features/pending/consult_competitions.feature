# Specified before it is implemented: the scenarios join the build gate (@acceptance) once they pass.
@competition @to_implement
Feature: Consult the competitions of the season
  As an administrator of Chess Manager
  I want to see the competitions of the season
  So that I know where the season stands

  Scenario: The administrator sees the competitions of the season
    Given the competition "Nationale 4", organized by the league "Centre-Val de Loire" and played on 6 boards, has been created for the season 2026-2027
    When the administrator consults the competitions of the season 2026-2027
    Then the administrator sees the competition "Nationale 4", organized by the league "Centre-Val de Loire" and played on 6 boards, for the season 2026-2027

  Scenario: A competition of another season is not shown
    Given the competition "Nationale 4", organized by the league "Centre-Val de Loire" and played on 6 boards, has been created for the season 2025-2026
    When the administrator consults the competitions of the season 2026-2027
    Then the administrator sees no competition for the season 2026-2027

  Scenario: The competitions are shown in the order of their names
    Given the competition "Régionale 1", organized by the league "Centre-Val de Loire" and played on 4 boards, has been created for the season 2026-2027
    And the competition "Nationale 4", organized by the league "Centre-Val de Loire" and played on 6 boards, has been created for the season 2026-2027
    And the competition "Départementale 2", organized by the league "Centre-Val de Loire" and played on 4 boards, has been created for the season 2026-2027
    When the administrator consults the competitions of the season 2026-2027
    Then the administrator sees the competitions in this order: "Départementale 2", "Nationale 4", "Régionale 1"
