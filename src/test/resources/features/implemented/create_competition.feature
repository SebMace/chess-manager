@competition
Feature: Create a competition
  As an administrator of Chess Manager
  I want to create a competition for the season
  So that clubs can later engage their teams in it

  @acceptance
  Scenario: A league creates its Nationale 4 competition
    Given the current season is 2026-2027
    When an administrator creates the competition "Nationale 4" organized by the league "Centre-Val de Loire", played on 6 boards
    Then the administrator is told that the competition "Nationale 4" has been created for the season 2026-2027
    And the competition "Nationale 4" is organized by the league "Centre-Val de Loire"
    And the competition "Nationale 4" is played on 6 boards
