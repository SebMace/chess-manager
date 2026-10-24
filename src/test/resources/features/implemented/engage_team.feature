@competition
Feature: Engage a team in a competition
  As an administrator of Chess Manager
  I want to engage a team of a club in a competition
  So that the club plays that competition during the season

  @acceptance
  Scenario: The administrator engages a team of a club in a competition
    Given the competition "Nationale 4", organized by the league "Centre-Val de Loire" and played on 6 boards, has been created for the season 2026-2027
    When the administrator engages a team of the club "Les Cavaliers de la Loire" in the competition "Nationale 4"
    Then the club "Les Cavaliers de la Loire" has the team 1 engaged in the competition "Nationale 4"
