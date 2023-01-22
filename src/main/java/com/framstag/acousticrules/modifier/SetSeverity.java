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
package com.framstag.acousticrules.modifier;

import com.framstag.acousticrules.rules.Severity;
import com.framstag.acousticrules.rules.instance.RuleInstance;
import jakarta.json.bind.annotation.JsonbCreator;
import jakarta.json.bind.annotation.JsonbProperty;

import java.util.Set;
import java.util.stream.Collectors;

public class SetSeverity extends AbstractModifier {
  private final Set<String> keys;
  private final Severity to;

  @JsonbCreator
  public SetSeverity(@JsonbProperty("keys") Set<String> keys,
                     @JsonbProperty("to") Severity to) {
    this.keys = Set.copyOf(keys);
    this.to = to;
  }

  @Override
  public String getDescription() {
    return "Setting severity for key(s) " +
      keys.stream().map(tag -> "'"+tag+"'").collect(Collectors.joining(", ")) +
      " to "+ to;
  }

  @Override
  public RuleInstance modify(RuleInstance rule) {
    if (keys.contains(rule.getKey())) {
      return rule.setSeverity(to,getReason());
    }

    return null;
  }
}
