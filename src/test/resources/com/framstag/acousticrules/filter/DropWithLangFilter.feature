@filter
Feature: The DropWithLang filter works as expected
  The DropWithLang filter should remove rules with one
  of the given types.

  Scenario: The DropWithLang filter removes rules with one of the given languages
    Given the filter DropWithLang with languages:
      |JAVA|
      |OTHER|
    And a rule definition ID1 of language JAVA
    And a rule definition ID2 of language KOTLIN
    And a rule definition ID3 of language OTHER
    When I pass the rules to the filter
    Then The following rule definitions have been filtered out:
      |ID1|
      |ID3|

  Scenario: DropWithLang filter has description as defined if no language is given
    Given the filter DropWithLang
    Then filter has description 'Dropping no rules'

  Scenario: DropWithLang filter has description as defined if one language is given
    Given the filter DropWithLang with languages:
      |JAVA|
    Then filter has description 'Dropping rules with language \'JAVA\''

  Scenario: DropWithLang filter has description as defined if multiple languages are given
    Given the filter DropWithLang with languages:
      |CPP|
      |JAVA|
    Then filter has description 'Dropping rules with languages \'CPP\', \'JAVA\''

  Scenario: DropWithLang filter description is stable for multiple unsorted languages
    Given the filter DropWithLang with languages:
      |JAVA|
      |CPP|
      |KOTLIN|
      |AWK|
    Then filter has description 'Dropping rules with languages \'AWK\', \'CPP\', \'JAVA\', \'KOTLIN\''
