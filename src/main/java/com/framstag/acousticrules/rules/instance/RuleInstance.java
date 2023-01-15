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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class RuleInstance implements Ruleable {
  private final RuleDefinition definition;
  private final Map<String, String> parameter;
  private Severity severity;

  private RuleInstance(RuleDefinition definition) {
    this.definition = definition;
    this.severity = definition.getSeverity();
    this.parameter = new HashMap<>();
  }

  public static RuleInstance fromDefinition(RuleDefinition definition) {
    return new RuleInstance(definition);
  }

  public void setParam(String key, String value) {
    if (!definition.hasParam(key)) {
      throw new IllegalArgumentException("The param '"+key+"' is not a known parameter for the rule "+getKey());
    }

    parameter.put(key,value);
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

  public void setSeverity(Severity severity) {
    this.severity = severity;
  }

  public String getName() {
    return definition.getName();
  }
}
