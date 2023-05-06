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

import com.framstag.acousticrules.rules.CustomizedRule;
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
public final class RuleInstance implements CustomizedRule, Comparable<RuleInstance> {
  private final RuleDefinition definition;
  private final UseStatus status;
  private final String statusReason;
  private final Map<String, String> parameter;
  private final Severity severity;
  private final String severityReason;

  RuleInstance(RuleDefinition definition,
               UseStatus status,
               String statusReason,
               Severity severity,
               String severityReason,
               Map<String, String> parameter) {
    this.definition = definition;
    this.status = status;
    this.statusReason = statusReason;
    this.severity = severity;
    this.severityReason = severityReason;
    this.parameter = Map.copyOf(parameter);
  }

  public static RuleInstance fromDefinition(RuleDefinition definition) {
    return new RuleInstance(definition,
      UseStatus.ACTIVE,
      null,
      definition.getSeverity(),
      null,
      Collections.emptyMap());
  }

  public String getName() {
    return definition.getName();
  }

  public RuleInstance disable(String reason) {
    return new RuleInstance(definition,
      UseStatus.DISABLED,
      reason,
      severity,
      severityReason,
      parameter);
  }

  public RuleInstance setSeverity(Severity severity, String reason) {
    return new RuleInstance(definition,
      status,
      statusReason,
      severity,
      reason,
      parameter);
  }

  public RuleInstance setParameter(String key, String value) {
    if (!definition.hasParam(key)) {
      throw new IllegalArgumentException("The param '"+key+"' is not a known parameter for the rule "+getKey());
    }

    Map<String,String> newParameter = new HashMap<>(parameter);
    newParameter.put(key,value);

    return new RuleInstance(definition,
      status,
      statusReason,
      severity,
      severityReason,
      newParameter);
  }

  public boolean isDisabled() {
    return status == UseStatus.DISABLED;
  }

  public boolean isActive() {
    return !isDisabled();
  }

  public boolean hasParameter() {
    return !parameter.isEmpty();
  }

  public Map<String, String> getParameter() {
    // Make sure the Map we hand out is immutable and our map cannot be changes via a side effect
    return Map.copyOf(parameter);
  }

  public boolean hasSeverityReason() {
    return severityReason != null && !severityReason.isBlank();
  }

  public String getSeverityReason() {
    return severityReason;
  }

  public boolean hasStatusReason() {
    return statusReason != null && !statusReason.isBlank();
  }

  public String getStatusReason() {
    return statusReason;
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

  @Override
  public String getLang() {
    return definition.getLang();
  }

  public String getRepo() {
    return definition.getRepo();
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
