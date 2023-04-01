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

package com.framstag.acousticrules.rules.instance;

import com.framstag.acousticrules.rules.Severity;
import io.cucumber.java.en.Given;

public class CustomizedRuleBuilderSteps {

  private final CustomizedRules customizedRules;

  public CustomizedRuleBuilderSteps(CustomizedRules customizedRules) {
    this.customizedRules = customizedRules;
  }

  @Given("a customized rule {word}")
  public void ruleWithId(String id) {
    var rule = new CustomizedRuleBuilder()
      .withKey(id)
      .build();
    customizedRules.rules.add(rule);
  }

  @Given("a customized rule {word} of type {word}")
  public void ruleWithIdAndType(String id, String type) {
    var rule = new CustomizedRuleBuilder()
      .withKey(id)
      .withType(type)
      .build();
    customizedRules.rules.add(rule);
  }

  @Given("a customized rule {word} with tag {word}")
  public void ruleWithIdAndTag(String id, String tag) {
    var rule = new CustomizedRuleBuilder()
      .withKey(id)
      .withTag(tag)
      .build();
    customizedRules.rules.add(rule);
  }

  @Given("a customized rule {word} with severity {severity}")
  public void ruleWithIdAndTag(String id, Severity severity) {
    var rule = new CustomizedRuleBuilder()
      .withKey(id)
      .withSeverity(severity)
      .build();
    customizedRules.rules.add(rule);
  }
}
