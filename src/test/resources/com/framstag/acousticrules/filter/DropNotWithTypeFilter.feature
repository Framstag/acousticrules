@filter
Feature: The DropNotWithType filter works as expected
  The DropNotWithType filter should remove rules with *not* one
  of the given types.

  Scenario: The DropNotWithType filter removes rules with one of the given types
    Given the filter DropNotWithType with types:
      |BUG|
      |OTHER|
    And a rule definition ID1 of type BUG
    And a rule definition ID2 of type NO_BUG
    And a rule definition ID3 of type OTHER
    When I pass the rules to the filter
    Then The following rule definitions have been filtered out:
      |ID2|

  Scenario: The DropNotWithType filter removes all rules if no type is given
    Given the filter DropNotWithType
    And a rule definition ID1 of type 'BUG
    And a rule definition ID2 of type NO_BUG
    And a rule definition ID3 of type OTHER
    When I pass the rules to the filter
    Then The following rule definitions have been filtered out:
      |ID1|
      |ID2|
      |ID3|

  Scenario: DropNotWithType filter has description as defined if no type is given
    Given the filter DropNotWithType
    Then filter has description 'Dropping all rules'

  Scenario: DropNotWithType filter has description as defined if one type is given
    Given the filter DropNotWithType with types:
      |BUG|
    Then filter has description 'Dropping rules not having type \'BUG\''

  Scenario: DropNotWithType filter has description as defined if multiple types are given
    Given the filter DropNotWithType with types:
      |BUG|
      |CODE_SMELL|
    Then filter has description 'Dropping rules not having types \'BUG\', \'CODE_SMELL\''

  Scenario: DropNotWithType filter description is stable for multiple unsorted types
    Given the filter DropNotWithType with types:
      |CODE_SMELL|
      |ZZZ|
      |BUG|
      |XXX|
    Then filter has description 'Dropping rules not having types \'BUG\', \'CODE_SMELL\', \'XXX\', \'ZZZ\''
