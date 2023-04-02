/*
 * AcousticRuler
 * Copyright 2023 Tim Teulings
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.framstag.acousticrules.modifier;

import com.framstag.acousticrules.rules.CustomizedRule;
import com.framstag.acousticrules.rules.Severity;
import com.framstag.acousticrules.rules.instance.CustomizedRules;
import io.cucumber.java.ParameterType;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ModifierSteps {
  private final Map<String, CustomizedRule> modifiedRules = new HashMap<>();
  private final CustomizedRules customizedRules;
  private Modifier modifier;

  public ModifierSteps(CustomizedRules customizedRules) {
    this.customizedRules = customizedRules;
  }

  @ParameterType("(INFO|MINOR|MAJOR|CRITICAL|BLOCKER)")
  public Severity severity(String severity) {
    return Severity.valueOf(severity);
  }

  @Given("the modifier 'ChangeSeverity' with 'from' {severity} and 'to' {severity}")
  public void createModifierChangeSeverity(Severity from, Severity to) {
    this.modifier = new ChangeSeverity(from, to);
  }

  @Given("the modifier 'ChangeSeverity' with 'from' {severity} and 'to' {severity} and reason {string}")
  public void createModifierChangeSeverityWithReason(Severity from, Severity to, String reason) {
    var modifier = new ChangeSeverity(from, to);
    modifier.setReason(reason);

    this.modifier = modifier;
  }

  @Given("the modifier 'SetSeverity' to {severity} for keys:")
  public void createModifierSetSeverity(Severity to, List<String> keys) {
    this.modifier = new SetSeverity(new HashSet<>(keys), to);
  }

  @Given("the modifier 'SetSeverity' to {severity} with reason {string} for keys:")
  public void createModifierSetSeverity(Severity to, String reason, List<String> keys) {
    var modifier = new SetSeverity(new HashSet<>(keys), to);
    modifier.setReason(reason);

    this.modifier = modifier;
  }

  @Given("the modifier 'DisableByKey' for keys:")
  public void createModifierDisableByKey(List<String> keys) {
    this.modifier = new DisableByKey(new HashSet<>(keys));
  }

  @Given("the modifier 'DisableByKey' with reason {string} for keys:")
  public void createModifierDisableByKeWithReason(String reason, List<String> keys) {
    var modifier = new DisableByKey(new HashSet<>(keys));
    modifier.setReason(reason);

    this.modifier = modifier;
  }

  @When("the modifier is executed for all customized rules")
  public void executeModifier() {
    for (var rule : customizedRules.rules) {
      var changedRule = modifier.modify(rule);

      modifiedRules.put(rule.getKey(), Objects.requireNonNullElse(changedRule, rule));
    }
  }

  @Then("customized rule {word} has severity {severity}")
  public void ruleHasSeverity(String key, Severity severity) {
    var rule = modifiedRules.get(key);

    Assertions.assertNotNull(rule,"Queried rule must exist");
    Assertions.assertEquals(severity,rule.getSeverity(),"Rule must have expected severity");
  }

  @Then("customized rule {word} has severity reason {string}")
  public void ruleHasSeverityReason(String key, String reason) {
    var rule = modifiedRules.get(key);

    Assertions.assertNotNull(rule,"Queried rule must exist");
    Assertions.assertEquals(reason,rule.getSeverityReason(),"Rule must have expected severity reason");
  }

  @Then("customized rule {word} has status reason {string}")
  public void ruleHasStatusReason(String key, String reason) {
    var rule = modifiedRules.get(key);

    Assertions.assertNotNull(rule,"Queried rule must exist");
    Assertions.assertEquals(reason,rule.getStatusReason(),"Rule must have expected status reason");
  }

  @Then("customized rule {word} is disabled")
  public void ruleIsDisabled(String key) {
    var rule = modifiedRules.get(key);

    Assertions.assertNotNull(rule,"Queried rule must exist");
    Assertions.assertTrue(rule.isDisabled(),"Rule must be disabled");
  }

  @Then("customized rule {word} is not disabled")
  public void ruleIsotDisabled(String key) {
    var rule = modifiedRules.get(key);

    Assertions.assertNotNull(rule,"Queried rule must exist");
    Assertions.assertFalse(rule.isDisabled(),"Rule must be disabled");
  }

  @Then("modifier has description {string}")
  public void checkFilterDescription(String description) {
    Assertions.assertEquals(description,modifier.getDescription(),"Expect description");
  }
}
