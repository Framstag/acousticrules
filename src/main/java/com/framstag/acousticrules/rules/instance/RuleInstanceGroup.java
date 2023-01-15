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

package com.framstag.acousticrules.rules.instance;

import com.framstag.acousticrules.rules.definition.RuleDefinitionGroup;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public final class RuleInstanceGroup {
  private final RuleDefinitionGroup definitionGroup;
  private final Map<String,RuleInstance> ruleInstances;

  private RuleInstanceGroup(RuleDefinitionGroup definitionGroup, Map<String,RuleInstance> ruleInstances) {
    this.definitionGroup = definitionGroup;
    this.ruleInstances = Map.copyOf(ruleInstances);
  }

  public static RuleInstanceGroup fromDefinitionGroup(RuleDefinitionGroup definitionGroup) {
    Map<String,RuleInstance> ruleInstances = definitionGroup
      .getRules()
      .stream()
      .map(RuleInstance::fromDefinition)
      .collect(Collectors.toUnmodifiableMap(RuleInstance::getKey,Function.identity()));

    return new RuleInstanceGroup(definitionGroup, ruleInstances);
  }

  public RuleDefinitionGroup getRuleDefinitionGroup() {
    return definitionGroup;
  }

  public Collection<RuleInstance> getRuleInstances() {
    return ruleInstances.values();
  }

  public int size() {
    return ruleInstances.size();
  }

  public RuleInstanceGroup filter(Iterable<RuleInstance> filteredInstances) {
    var newInstances = new HashMap<String,RuleInstance>(ruleInstances);

    filteredInstances.forEach(rule -> newInstances.remove(rule.getKey()));

    return new RuleInstanceGroup(definitionGroup,newInstances);
  }

  /**
   * Create a new RuleInstanceGroup with the given RuleInstances updated
   * @param instances instances to update
   * @return a new RuleInstance instance.
   */
  public RuleInstanceGroup update(Iterable<RuleInstance> instances) {
    HashMap<String,RuleInstance> newInstances = new HashMap<>(ruleInstances);

    instances.forEach(rule ->
      newInstances.put(rule.getKey(),rule)
    );

    return new RuleInstanceGroup(definitionGroup,newInstances);
  }

  /**
   * Return the RuleInstance with the given key or null
   * @param key key of the rule
   * @return instance or null
   */
  public RuleInstance getRuleInstance(String key) {
    return ruleInstances.get(key);
  }
}
