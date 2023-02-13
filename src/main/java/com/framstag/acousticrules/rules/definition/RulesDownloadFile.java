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

import jakarta.json.bind.annotation.JsonbCreator;
import jakarta.json.bind.annotation.JsonbProperty;

import java.util.Collections;
import java.util.List;

/**
 * A List of RuleDefinitions as loaded from a file. USed for 1:1 mapping of the JSON data in the file
 * into a Java object.
 * @param rules
 */
public record RulesDownloadFile(List<RuleDefinition> rules) {

  @JsonbCreator
  public RulesDownloadFile(@JsonbProperty("rules") List<RuleDefinition> rules) {
    this.rules = List.copyOf(rules);
  }

  @Override
  public List<RuleDefinition> rules() {
    return Collections.unmodifiableList(rules);
  }

  public boolean hasRules() {
    return rules != null && !rules.isEmpty();
  }

  public int getRuleCount() {
    if (rules == null) {
      return 0;
    }

    return rules.size();
  }
}
