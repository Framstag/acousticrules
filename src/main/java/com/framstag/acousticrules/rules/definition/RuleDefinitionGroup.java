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
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * An immutable group of RuleDefinitions. In spite of the RuleDefinitionList these rule definitions are disjunctive.
 * There a functions for doing simple set operations on the group.
 */
public final class RuleDefinitionGroup {
  private final Set<RuleDefinition> ruleDefinitions;

  private RuleDefinitionGroup(Set<RuleDefinition> ruleDefinitions) {
    // make an immutable copy
    this.ruleDefinitions = Set.copyOf(ruleDefinitions);
  }

  public static RuleDefinitionGroup fromRuleDefinitionSet(Set<RuleDefinition> definitions) {
    return new RuleDefinitionGroup(definitions);
  }

  public static RuleDefinitionGroup fromRuleDefinitionList(RuleDefinitionList definitions) {
    return new RuleDefinitionGroup(new HashSet<>(definitions.getRules()));
  }

  public static RuleDefinitionGroup fromRuleDefinitionGroups(Collection<RuleDefinitionGroup> definitions) {
    Set<RuleDefinition> overallRules = definitions
      .stream()
      .flatMap(group -> group.getRules().stream())
      .collect(Collectors.toSet());

    return new RuleDefinitionGroup(overallRules);
  }

  public Collection<RuleDefinition> getRules() {
    return ruleDefinitions;
  }

  public int size() {
    return ruleDefinitions.size();
  }

  public RuleDefinitionGroup filter(Iterable<RuleDefinition> filteredDefinitions) {
    Set<RuleDefinition> newDefinitions = new HashSet<>(ruleDefinitions);

    filteredDefinitions.forEach(newDefinitions::remove);

    return new RuleDefinitionGroup(newDefinitions);
  }

  /**
   * Create a new RuleDefinitionGroup with the given RuleDefinitions updated
   * @param definitions definitions to update
   * @return a new RuleDefinition instance.
   */
  public RuleDefinitionGroup update(Iterable<RuleDefinition> definitions) {
    Set<RuleDefinition> newDefinitions = new HashSet<>(ruleDefinitions);

    definitions.forEach(newDefinitions::add);

    return new RuleDefinitionGroup(newDefinitions);
  }

  public boolean contains(RuleDefinition ruleDefinition) {
    return ruleDefinitions.contains(ruleDefinition);
  }
}
