@modifier
Feature: The SetSeverity modifier changes the severity
  for rules referenced by key to severity `to`

  Scenario: Changes severity to `to` for rules referenced by key
    Given a customized rule ID1 with severity MINOR
    Given a customized rule ID2 with severity MINOR
    Given a customized rule ID3 with severity BLOCKER
    Given the modifier 'SetSeverity' to MAJOR for keys:
      |ID1|
      |ID2|
    When the modifier is executed for all customized rules
    Then customized rule ID1 has severity MAJOR
    Then customized rule ID2 has severity MAJOR
    Then customized rule ID3 has severity BLOCKER

  Scenario: Changing severity sets reason
    Given a customized rule ID1 with severity MINOR
    Given the modifier 'SetSeverity' to MAJOR with reason 'Let\'s change it' for keys:
      |ID1|
      |ID2|
    When the modifier is executed for all customized rules
    Then customized rule ID1 has severity reason 'Let\'s change it'

  Scenario:Modifier has a given description for one key
    Given the modifier 'SetSeverity' to MAJOR for keys:
      |ID1|
    Then modifier has description 'Setting severity for key(s) \'ID1\' to MAJOR'

  Scenario:Modifier has a given description for multiple keys
    Given the modifier 'SetSeverity' to MAJOR for keys:
      |ID1|
      |ID2|
    Then modifier has description 'Setting severity for key(s) \'ID1\', \'ID2\' to MAJOR'
