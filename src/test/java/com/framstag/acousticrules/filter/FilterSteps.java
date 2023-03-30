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

package com.framstag.acousticrules.filter;

import com.framstag.acousticrules.rules.RuleDefinitions;
import com.framstag.acousticrules.rules.definition.RuleDefinition;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class FilterSteps {

  private final Map<String, RuleDefinition> filteredRules = new HashMap<>();
  private final RuleDefinitions ruleDefinitions;
  private Filter filter;

  public FilterSteps(RuleDefinitions ruleDefinitions) {
    this.ruleDefinitions = ruleDefinitions;
  }

  @Given("the filter RemoveDeprecated")
  public void createFilterRemoveDeprecated() {
    filter = new RemoveDeprecated();
  }

  @Given("the filter DropWithType")
  public void createFilterDropWithType() {
    filter = new DropWithType(Collections.emptySet());
  }

  @Given("the filter DropWithType with types:")
  public void createFilterDropWithTypeWithTypes(List<String> types) {
    filter = new DropWithType(types.stream().collect(Collectors.toUnmodifiableSet()));
  }

  @Given("the filter DropNotWithType")
  public void createFilterDropNotWithType() {
    filter = new DropNotWithType(Collections.emptySet());
  }

  @Given("the filter DropNotWithType with types:")
  public void createFilterDropNotWithTypeWithTypes(List<String> types) {
    filter = new DropNotWithType(types.stream().collect(Collectors.toUnmodifiableSet()));
  }

  @Given("the filter DropWithTag")
  public void createFilterDropWithTags() {
    filter = new DropWithTag(Collections.emptySet(),Collections.emptySet());
  }

  @Given("the filter DropWithTag with tags:")
  public void createFilterDropWithTagsWithTags(List<String> tags) {
    filter = new DropWithTag(tags.stream().collect(Collectors.toUnmodifiableSet()), Collections.emptySet());
  }

  @Given("the filter DropWithTag with tag {word} but keys:")
  public void createFilterDropWithTagsWithTags(String tag, List<String> keys) {
    filter = new DropWithTag(Set.of(tag), keys.stream().collect(Collectors.toUnmodifiableSet()));
  }

  @Given("the filter DropWithKey")
  public void createFilterDropWithKeys() {
    filter = new DropWithKey(Collections.emptySet());
  }

  @Given("the filter DropWithKey with keys:")
  public void createFilterDropWithKeysWithKeys(List<String> keys) {
    filter = new DropWithKey(keys.stream().collect(Collectors.toUnmodifiableSet()));
  }

  @When("I pass the rules to the filter")
  public void filterRules() {
    for (var rule : ruleDefinitions.rules) {
      if (filter.filter(rule)) {
        filteredRules.put(rule.getKey(),rule);
      }
    }
  }

  @Then("The following rule definitions have been filtered out:")
  public void checkFilteredRules(List<String> keys) {
    for (var key : keys) {
      Assertions.assertTrue(filteredRules.containsKey(key),"rule with key has to be filtered out");
    }
  }

  @Then("no rule definitions have been filtered out")
  public void checkAnyFilteredRules() {
    Assertions.assertTrue(filteredRules.isEmpty(),"no rules have been filtered out");
  }

  @Then("filter has description {string}")
  public void checkFilterDescription(String description) {
    Assertions.assertEquals(description,filter.getDescription(),"Expect description");
  }
}
