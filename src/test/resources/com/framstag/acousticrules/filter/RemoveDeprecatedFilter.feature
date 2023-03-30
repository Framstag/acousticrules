Feature: The RemoveDeprecated filter works as expected
  The RemoveDeprecated filter removes deprecated rules
  and no other rules.

  Scenario: RemovedDeprecated filter removed deprecated rules
    Given the filter RemoveDeprecated
    And a deprecated rule definition ID1
    And a rule definition ID2
    When I pass the rules to the filter
    Then The following rule definitions have been filtered out:
      |ID1|

  Scenario: RemovedDeprecated filter keeps non-deprecated rules
    Given the filter RemoveDeprecated
    Given a rule definition ID1
    When I pass the rules to the filter
    Then no rule definitions have been filtered out

  Scenario: RemovedDeprecated filter has description as defined
    Given the filter RemoveDeprecated
    Then filter has description 'Removing deprecated rules'
