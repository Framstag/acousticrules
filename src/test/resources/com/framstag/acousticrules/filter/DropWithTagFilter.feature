@filter
Feature: The DropWithTag filter works as expected
  The DropWithTag filter should remove rules with one
  of the given tags.

  Scenario: The DropWithTag filter removes rules with one of the given tags
    Given the filter DropWithTag with tags:
      |BUG|
      |OTHER|
    And a rule definition ID1 with tag BUG
    And a rule definition ID2 with tag NO_BUG
    And a rule definition ID3 with tag OTHER
    When I pass the rules to the filter
    Then The following rule definitions have been filtered out:
      |ID1|
      |ID3|

  Scenario: The DropWithTag filter removes rules with one of the given tags, if not excluded via "but"
    Given the filter DropWithTag with tag BUG but keys:
      |ID1|
    And a rule definition ID1 with tag BUG
    And a rule definition ID2 with tag BUG
    When I pass the rules to the filter
    Then The following rule definitions have been filtered out:
      |ID2|

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

  Scenario: DropWithTag filter description for one tag and one but
    Given the filter DropWithTag with tag BUG but keys:
      |ID1|
    Then filter has description 'Dropping rules with tag \'BUG\' but not key \'ID1\''

  Scenario: DropWithTag filter description for one tag and multiple buts
    Given the filter DropWithTag with tag BUG but keys:
      |ID1|
      |ID2|
    Then filter has description 'Dropping rules with tag \'BUG\' but not keys \'ID1\', \'ID2\''

  Scenario: DropWithTag filter description is stable regarding but sort order
    Given the filter DropWithTag with tag BUG but keys:
      |C|
      |A|
      |B|
    Then filter has description 'Dropping rules with tag \'BUG\' but not keys \'A\', \'B\', \'C\''
