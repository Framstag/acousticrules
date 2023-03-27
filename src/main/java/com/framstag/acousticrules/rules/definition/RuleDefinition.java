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

import com.framstag.acousticrules.rules.Parameter;
import com.framstag.acousticrules.rules.Rule;
import com.framstag.acousticrules.rules.Severity;
import com.framstag.acousticrules.rules.Status;
import jakarta.json.bind.annotation.JsonbCreator;
import jakarta.json.bind.annotation.JsonbProperty;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * A rule definition as defined by the Sonar server.
 * The identity attribute is the rule definition key. The class is deserializable from a corresponding
 * Sonar JSON file containing rule definitions.
 *
 */
public class RuleDefinition implements Rule, Comparable<RuleDefinition> {

  private final String key;
  private final String name;
  private final String repo;
  private final Status status;
  private final String lang;
  private final String type;
  private final List<String> sysTags;
  private final Map<String, Parameter> params;
  private final Severity severity;

  @JsonbCreator
  public RuleDefinition(
    @JsonbProperty("key") String key,
    @JsonbProperty("name") String name,
    @JsonbProperty("repo") String repo,
    @JsonbProperty("severity") Severity severity,
    @JsonbProperty("status") Status status,
    @JsonbProperty("lang") String lang,
    @JsonbProperty("type") String type,
    @JsonbProperty("sysTags") List<String> sysTags,
    @JsonbProperty("params") Set<Parameter> params) {
    this.key = key;
    this.name = name;
    this.repo = repo;
    this.severity = severity;
    this.status = status;
    this.lang = lang;
    this.type = type;
    this.sysTags = List.copyOf(sysTags);
    this.params = params.stream().collect(Collectors.toMap(Parameter::key, Function.identity()));
  }

  public String getName() {
    return name;
  }

  public String getLang() {
    return lang;
  }

  public Collection<Parameter> getParams() {
    return params.values();
  }

  public String getKey() {
    return key;
  }

  public String getType() {
    return type;
  }

  public Severity getSeverity() {
    return severity;
  }

  public Status getStatus() {
    return status;
  }

  public List<String> getSysTags() {
    return sysTags;
  }

  public String getRepo() {
    return repo;
  }

  public boolean hasParams() {
    return !params.isEmpty();
  }

  public boolean hasParam(String key) {
    return params != null && params.containsKey(key);
  }

  @Override
  public int compareTo(RuleDefinition other) {
    return key.compareTo(other.key);
  }

  @Override
  public int hashCode() {
    return Objects.hash(key);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }

    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    var rule = (RuleDefinition) o;
    return key.equals(rule.key);
  }
}
