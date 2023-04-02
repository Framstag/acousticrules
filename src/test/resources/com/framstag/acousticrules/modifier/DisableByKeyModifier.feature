Feature: The DisableByKey Modifier disables rules with a given key

  Scenario: Disables rules with one of the given keys
    Given a customized rule ID1
    Given a customized rule ID2
    Given a customized rule ID3
    Given the modifier 'DisableByKey' for keys:
      |ID1|
      |ID2|
    When the modifier is executed for all customized rules
    Then customized rule ID1 is disabled
    Then customized rule ID2 is disabled
    Then customized rule ID3 is not disabled

  Scenario: Disabling a key sets reason
    Given a customized rule ID1
    Given the modifier 'DisableByKey' with reason 'Let\'s change it' for keys:
      |ID1|
    When the modifier is executed for all customized rules
    Then customized rule ID1 has status reason 'Let\'s change it'

  Scenario:Modifier has a given description for one key
    Given the modifier 'DisableByKey' for keys:
      |ID1|
    Then modifier has description 'Disabled rule with key \'ID1\''

  Scenario:Modifier has a given description for multiple keys
    Given the modifier 'DisableByKey' for keys:
      |ID1|
      |ID2|
    Then modifier has description 'Disabled rules with keys \'ID1\', \'ID2\''
