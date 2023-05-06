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

import com.framstag.acousticrules.rules.Rule;
import com.framstag.acousticrules.rules.definition.RuleDefinitions;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class FilterSteps {

  private final Map<String, Rule> filteredRules = new HashMap<>();
  private final RuleDefinitions ruleDefinitions;
  private Filter filter;

  public FilterSteps(RuleDefinitions ruleDefinitions) {
    this.ruleDefinitions = ruleDefinitions;
  }

  @Given("the filter RemoveDeprecated")
  public void createFilterRemoveDeprecated() {
    filter = new RemoveDeprecated();
  }

  @Given("the filter DropWithLang")
  public void createFilterDropWithLang() {
    filter = new DropWithLang(Collections.emptySet());
  }

  @Given("the filter DropWithLang with languages:")
  public void createFilterDropWithLangWithLangs(List<String> langs) {
    filter = new DropWithLang(new HashSet<>(langs));
  }

  @Given("the filter DropWithType")
  public void createFilterDropWithType() {
    filter = new DropWithType(Collections.emptySet());
  }

  @Given("the filter DropWithType with types:")
  public void createFilterDropWithTypeWithTypes(List<String> types) {
    filter = new DropWithType(new HashSet<>(types));
  }

  @Given("the filter DropNotWithType")
  public void createFilterDropNotWithType() {
    filter = new DropNotWithType(Collections.emptySet());
  }

  @Given("the filter DropNotWithType with types:")
  public void createFilterDropNotWithTypeWithTypes(List<String> types) {
    filter = new DropNotWithType(new HashSet<>(types));
  }

  @Given("the filter DropWithTag")
  public void createFilterDropWithTags() {
    filter = new DropWithTag(Collections.emptySet(),Collections.emptySet());
  }

  @Given("the filter DropWithTag with tags:")
  public void createFilterDropWithTagsWithTags(List<String> tags) {
    filter = new DropWithTag(new HashSet<>(tags), Collections.emptySet());
  }

  @Given("the filter DropWithTag with tag {word} but keys:")
  public void createFilterDropWithTagsWithTags(String tag, List<String> keys) {
    filter = new DropWithTag(Set.of(tag), new HashSet<>(keys));
  }

  @Given("the filter DropWithKey")
  public void createFilterDropWithKeys() {
    filter = new DropWithKey(Collections.emptySet());
  }

  @Given("the filter DropWithKey with keys:")
  public void createFilterDropWithKeysWithKeys(List<String> keys) {
    filter = new DropWithKey(new HashSet<>(keys));
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
    Assertions.assertArrayEquals(keys.stream().sorted().toArray(),
      filteredRules.keySet().stream().sorted().toArray(),
      "Filtered rules must exactly match");
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
