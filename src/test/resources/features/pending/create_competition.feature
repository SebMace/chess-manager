# Specified before it is implemented: the scenario joins the build gate (@acceptance) once it passes.
@competition @to_implement
Feature: Create a competition
  As an administrator of Chess Manager
  I want to create a competition for the season
  So that clubs can later engage their teams in it

  Scenario: A league creates its Nationale 4 competition
    Given the current season is 2026-2027
    When an administrator creates the competition "Nationale 4 Centre-Val de Loire" played on 6 boards
    Then the administrator is told that the competition "Nationale 4 Centre-Val de Loire" has been created for the season 2026-2027
    And the competition "Nationale 4 Centre-Val de Loire" is played on 6 boards
