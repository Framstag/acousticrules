Feature: The DropWithKey filter works as expected
  The DropWithKey filter should remove rules with one
  of the given keys.

  Scenario: The DropWithKey filter removes rules with one of the given keys
    Given the filter DropWithKey with keys:
      |ID1|
      |ID3|
    And a rule definition 'ID1'
    And a rule definition 'ID2'
    And a rule definition 'ID3'
    When I pass the rules to the filter
    Then The following rule definitions have been filtered out:
      |ID1|
      |ID3|

  Scenario: DropWithKey filter has description as defined if no type is given
    Given the filter DropWithKey
    Then filter has description 'Dropping no rules'

  Scenario: DropWithKey filter has description as defined if one type is given
    Given the filter DropWithKey with keys:
      |ID1|
    Then filter has description 'Dropping rules with key \'ID1\''

  Scenario: DropWithKey filter has description as defined if multiple types are given
    Given the filter DropWithKey with keys:
      |ID1|
      |ID2|
    Then filter has description 'Dropping rules with keys \'ID1\', \'ID2\''

  Scenario: DropWithKey filter description is stable for multiple unsorted types
    Given the filter DropWithKey with keys:
      |C|
      |ZZZ|
      |B|
      |XXX|
    Then filter has description 'Dropping rules with keys \'B\', \'C\', \'XXX\', \'ZZZ\''



