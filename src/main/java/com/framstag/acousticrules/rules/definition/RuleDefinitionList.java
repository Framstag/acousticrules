/*
 * AcousticRuler
 * Copyright 2022 Tim Teulings
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

import java.util.Collection;
import java.util.List;

/**
 * An immutable list of RuleDefinition instance.
 */
public final class RuleDefinitionList {
  private final List<RuleDefinition> ruleDefinitions;

  private RuleDefinitionList(List<RuleDefinition> ruleDefinitions) {
    // Make n immutable copy
    this.ruleDefinitions = List.copyOf(ruleDefinitions);
  }

  public static RuleDefinitionList fromRuleDownloadFiles(Collection<RulesDownloadFile> files) {
    List<RuleDefinition> rules = files
      .stream()
      .filter(RulesDownloadFile::hasRules)
      .flatMap(file -> file.rules().stream())
      .toList();

    return new RuleDefinitionList(rules);
  }

  public int size() {
    return ruleDefinitions.size();
  }

  public Collection<RuleDefinition> getRules() {
    return ruleDefinitions;
  }
}
