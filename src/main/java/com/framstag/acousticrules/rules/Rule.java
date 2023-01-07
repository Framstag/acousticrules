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
package com.framstag.acousticrules.rules;

import jakarta.json.bind.annotation.JsonbCreator;
import jakarta.json.bind.annotation.JsonbProperty;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Rule implements Comparable<Rule> {

  private final String key;
  private final String name;
  private final String repo;
  private final Status status;
  private final String lang;
  private final String type;
  private final List<String> sysTags;
  private final List<Parameter> params;
  private Severity severity;

  @JsonbCreator
  public Rule(
    @JsonbProperty("key") String key,
    @JsonbProperty("name") String name,
    @JsonbProperty("repo") String repo,
    @JsonbProperty("severity") Severity severity,
    @JsonbProperty("status") Status status,
    @JsonbProperty("lang") String lang,
    @JsonbProperty("type") String type,
    @JsonbProperty("sysTags") List<String> sysTags,
    @JsonbProperty("params") List<Parameter> params) {
    this.key = key;
    this.name = name;
    this.repo = repo;
    this.severity = severity;
    this.status = status;
    this.lang = lang;
    this.type = type;
    this.sysTags = List.copyOf(sysTags);
    this.params = List.copyOf(params);
  }

  public String getKey() {
    return key;
  }

  public String getName() {
    return name;
  }

  public String getRepo() {
    return repo;
  }

  public Severity getSeverity() {
    return severity;
  }

  public void setSeverity(Severity severity) {
    this.severity = severity;
  }

  public Status getStatus() {
    return status;
  }

  public String getLang() {
    return lang;
  }

  public String getType() {
    return type;
  }

  public List<String> getSysTags() {
    return Collections.unmodifiableList(sysTags);
  }

  public List<Parameter> getParams() {
    return Collections.unmodifiableList(params);
  }

  public void setParam(String key, String value) {
    // TODO: Errorhandling
    if (!hasParams()) {
      return;
    }

    for (Parameter param : params) {
      if (param.getKey().equals(key)) {
        param.setValue(value);

        return;
      }
    }
  }

  public boolean hasParams() {
    return params != null && !params.isEmpty();
  }

  @Override
  public int compareTo(Rule other) {
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

    var rule = (Rule) o;
    return key.equals(rule.key);
  }
}
