Feature: The ChangeSeverityModifier changes the severity
  for all passed customized rules with severity `from`
  to severity `to`

  Scenario: Changes severity for all customized rules matching severity `from` to `to`, but no other
    Given a customized rule ID1 with severity MINOR
    Given a customized rule ID2 with severity MINOR
    Given a customized rule ID3 with severity BLOCKER
    Given the modifier 'ChangeSeverity' with 'from' MINOR and 'to' MAJOR
    When the modifier is executed for all customized rules
    Then customized rule ID1 has severity MAJOR
    Then customized rule ID2 has severity MAJOR
    Then customized rule ID3 has severity BLOCKER

  Scenario: Changing severity sets reason
    Given a customized rule ID1 with severity MINOR
    Given the modifier 'ChangeSeverity' with 'from' MINOR and 'to' MAJOR and reason 'Let\'s change it'
    When the modifier is executed for all customized rules
    Then customized rule ID1 has severity reason 'Let\'s change it'

  Scenario:Modifier has a given description
    Given the modifier 'ChangeSeverity' with 'from' MINOR and 'to' MAJOR
    Then modifier has description 'Setting severity from MINOR to MAJOR'
