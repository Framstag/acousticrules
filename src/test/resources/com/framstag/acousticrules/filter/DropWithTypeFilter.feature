Feature: The DropWithType filter works as expected
  The DropWithType filter should remove rules with one
  of the given types.

  Scenario: The DropWithType filter removes rules with one of the given types
    Given the filter DropWithType with types:
      |BUG|
      |OTHER|
    And a rule definition 'ID1' of type 'BUG'
    And a rule definition 'ID2' of type 'CODE_SMELL'
    And a rule definition 'ID3' of type 'OTHER'
    When I pass the rules to the filter
    Then The following rule definitions have been filtered out:
      |ID1|
      |ID3|

  Scenario: DropWithType filter has description as defined if no type is given
    Given the filter DropWithType
    Then filter has description 'Dropping no rules'

  Scenario: DropWithType filter has description as defined if one type is given
    Given the filter DropWithType with types:
      |BUG|
    Then filter has description 'Dropping rules with type \'BUG\''

  Scenario: DropWithType filter has description as defined if multiple types are given
    Given the filter DropWithType with types:
      |BUG|
      |CODE_SMELL|
    Then filter has description 'Dropping rules with types \'BUG\', \'CODE_SMELL\''

  Scenario: DropWithType filter description is stable for multiple unsorted types
    Given the filter DropWithType with types:
      |CODE_SMELL|
      |ZZZ|
      |BUG|
      |XXX|
    Then filter has description 'Dropping rules with types \'BUG\', \'CODE_SMELL\', \'XXX\', \'ZZZ\''
