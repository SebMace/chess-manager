# Business specifications only. FfeMembershipTests and FfeIdTests cover the core rules.
# Their unlicensed Member fixture is superseded by register_member.feature.
# Identifiers are synthetic: no official FFE pattern is asserted.
# Identifier replacement, renewal, expiry and license season scope remain undecided.
@club_management @existing_domain_rules
Feature: Associate FFE identification with an A or B license
  As a club administrator
  I want every recorded FFE license to include an identifier and category
  So that FFE registration cannot be incomplete

  Scenario Outline: Record either supported license category
    Given Camille's FFE identifier is "A12345"
    When Camille's FFE registration is recorded with category <type>
    Then the recorded FFE identifier is "A12345"
    And the recorded license category is <type>

    Examples:
      | type |
      | A    |
      | B    |

  Scenario Outline: Reject an incomplete registration
    Given no FFE registration is recorded for Camille
    When registration is requested without the <missing_information>
    Then the request is rejected
    And no partial FFE registration is recorded

    Examples:
      | missing_information  |
      | FFE identifier       |
      | FFE license category |

  Scenario Outline: Reject an identifier without a value
    When an FFE identifier is created with <value>
    Then the identifier is rejected

    Examples:
      | value            |
      | no value         |
      | an empty string  |
      | only spaces      |
      | only tabs        |
      | only line breaks |

  Scenario: Preserve the identifier as supplied
    When the FFE identifier "A00123" is recorded
    Then its value remains exactly "A00123"

  Scenario Outline: Preserve registration after an invalid request
    Given Camille has recorded FFE identifier "A12345" and category A
    When a new registration is requested without the <missing_information>
    Then the request is rejected
    And Camille's FFE identifier remains "A12345"
    And Camille's license category remains A

    Examples:
      | missing_information  |
      | FFE identifier       |
      | FFE license category |
