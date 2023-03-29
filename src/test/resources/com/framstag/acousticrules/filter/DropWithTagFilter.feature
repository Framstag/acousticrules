Feature: The DropWithTag filter works as expected
  The DropWithTag filter should remove rules with one
  of the given tags.

  Scenario: The DropWithTag filter removes rules with one of the given tags
    Given the filter DropWithTag with tags:
      |BUG|
      |OTHER|
    And a rule definition 'ID1' with tag 'BUG'
    And a rule definition 'ID2' with tag 'NO_BUG'
    And a rule definition 'ID3' with tag 'OTHER'
    When I pass the rules to the filter
    Then The following rule definitions have been filtered out:
      |ID1|
      |ID3|

  Scenario: DropWithTag filter has description as defined if no type is given
    Given the filter DropWithTag
    Then filter has description 'Dropping no rules'

  Scenario: DropWithTag filter has description as defined if one type is given
    Given the filter DropWithTag with tags:
      |BUG|
    Then filter has description 'Dropping rules with tag \'BUG\''

  Scenario: DropWithTag filter has description as defined if multiple types are given
    Given the filter DropWithTag with tags:
      |BUG|
      |CODE_SMELL|
    Then filter has description 'Dropping rules with tags \'BUG\', \'CODE_SMELL\''

  Scenario: DropWithTag filter description is stable for multiple unsorted types
    Given the filter DropWithTag with tags:
      |CODE_SMELL|
      |ZZZ|
      |BUG|
      |XXX|
    Then filter has description 'Dropping rules with tags \'BUG\', \'CODE_SMELL\', \'XXX\', \'ZZZ\''

