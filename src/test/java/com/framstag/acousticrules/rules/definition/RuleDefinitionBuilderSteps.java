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

package com.framstag.acousticrules.rules.definition;

import io.cucumber.java.en.Given;

public class RuleDefinitionBuilderSteps {

  private final RuleDefinitions ruledefinitions;

  public RuleDefinitionBuilderSteps(RuleDefinitions ruledefinitions) {
    this.ruledefinitions = ruledefinitions;
  }

  @Given("a rule definition {word}")
  public void ruleWithId(String id) {
    var rule = new RuleDefinitionBuilder()
      .withKey(id)
      .build();
    ruledefinitions.rules.add(rule);
  }

  @Given("a rule definition {word} of language {word}")
  public void ruleWithIdAndLang(String id, String lang) {
    var rule = new RuleDefinitionBuilder()
      .withKey(id)
      .withLang(lang)
      .build();
    ruledefinitions.rules.add(rule);
  }

  @Given("a rule definition {word} of type {word}")
  public void ruleWithIdAndType(String id, String type) {
    var rule = new RuleDefinitionBuilder()
      .withKey(id)
      .withType(type)
      .build();
    ruledefinitions.rules.add(rule);
  }

  @Given("a rule definition {word} with tag {word}")
  public void ruleWithIdAndTag(String id, String tag) {
    var rule = new RuleDefinitionBuilder()
      .withKey(id)
      .withTag(tag)
      .build();
    ruledefinitions.rules.add(rule);
  }

  @Given("a deprecated rule definition {word}")
  public void ruleDeprecatedWithId(String key) {
    var rule = new RuleDefinitionBuilder()
      .withKey(key)
      .isDeprecated()
      .build();
    ruledefinitions.rules.add(rule);
  }
}
