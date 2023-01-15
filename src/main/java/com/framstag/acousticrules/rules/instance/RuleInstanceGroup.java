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
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public final class RuleInstanceGroup {
  private final RuleDefinitionGroup definitionGroup;
  private final Set<RuleInstance> ruleInstances;

  private RuleInstanceGroup(RuleDefinitionGroup definitionGroup, Set<RuleInstance> ruleInstances) {
    this.definitionGroup = definitionGroup;
    this.ruleInstances = Set.copyOf(ruleInstances);
  }

  public static RuleInstanceGroup fromDefinitionGroup(RuleDefinitionGroup definitionGroup) {
    Set<RuleInstance> ruleInstances = definitionGroup
      .getRules()
      .stream()
      .map(RuleInstance::fromDefinition)
      .collect(Collectors.toUnmodifiableSet());

    return new RuleInstanceGroup(definitionGroup, ruleInstances);
  }

  public RuleDefinitionGroup getDefinitionGroup() {
    return definitionGroup;
  }

  public Set<RuleInstance> getRuleInstances() {
    return ruleInstances;
  }

  public int size() {
    return ruleInstances.size();
  }

  public RuleInstanceGroup filter(Iterable<RuleInstance> filteredInstances) {
    Set<RuleInstance> newInstances = new HashSet<>(ruleInstances);

    filteredInstances.forEach(newInstances::remove);

    return new RuleInstanceGroup(definitionGroup,newInstances);
  }

  /**
   * Create a new RuleInstanceGroup with the given RuleInstances updated
   * @param instances instances to update
   * @return a new RuleInstance instance.
   */
  public RuleInstanceGroup update(Collection<RuleInstance> instances) {
    Set<RuleInstance> newInstances = new HashSet<>(ruleInstances);

    newInstances.removeAll(instances);
    newInstances.addAll(instances);

    return new RuleInstanceGroup(definitionGroup,newInstances);
  }

}
