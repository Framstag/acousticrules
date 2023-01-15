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

import com.framstag.acousticrules.rules.Ruleable;
import com.framstag.acousticrules.rules.Severity;
import com.framstag.acousticrules.rules.Status;
import com.framstag.acousticrules.rules.definition.RuleDefinition;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Immutable class for holding information of the instantiation of a RuleDefinition. The instantiation
 * process allows to overwrite some of the information in the RuleDefinition.
 */
public final class RuleInstance implements Ruleable,Comparable<RuleInstance> {
  private final RuleDefinition definition;
  private final Map<String, String> parameter;
  private final Severity severity;

  private RuleInstance(RuleDefinition definition, Severity severity, Map<String,String> parameter) {
    this.definition = definition;
    this.severity = severity;
    this.parameter = Map.copyOf(parameter);
  }

  public static RuleInstance fromDefinition(RuleDefinition definition) {
    return new RuleInstance(definition, definition.getSeverity(), Collections.emptyMap());
  }

  public String getName() {
    return definition.getName();
  }

  public RuleInstance setParameter(String key, String value) {
    if (!definition.hasParam(key)) {
      throw new IllegalArgumentException("The param '"+key+"' is not a known parameter for the rule "+getKey());
    }

    Map<String,String> newParameter = new HashMap<>(parameter);
    newParameter.put(key,value);

    return new RuleInstance(definition,severity,newParameter);
  }

  public String getKey() {
    return definition.getKey();
  }

  @Override
  public String getType() {
    return definition.getType();
  }

  public Severity getSeverity() {
    return severity;
  }

  @Override
  public Status getStatus() {
    return definition.getStatus();
  }

  @Override
  public List<String> getSysTags() {
    return definition.getSysTags();
  }

  public RuleInstance setSeverity(Severity severity) {
    return new RuleInstance(definition,severity,parameter);
  }

  @Override
  public int compareTo(RuleInstance other) {
    return getKey().compareTo(other.getKey());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getKey());
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }

    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    var rule = (RuleInstance) o;
    return getKey().equals(rule.getKey());
  }
}
