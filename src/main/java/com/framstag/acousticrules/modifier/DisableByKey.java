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
package com.framstag.acousticrules.modifier;

import com.framstag.acousticrules.annotation.Description;
import com.framstag.acousticrules.rules.instance.RuleInstance;
import jakarta.json.bind.annotation.JsonbCreator;
import jakarta.json.bind.annotation.JsonbProperty;

import java.util.Set;
import java.util.stream.Collectors;

@Description("Disable rules with the given keys")
public class DisableByKey extends AbstractModifier {
  private final Set<String> keys;

  @JsonbCreator
  public DisableByKey(@JsonbProperty("keys") Set<String> keys) {
    this.keys = Set.copyOf(keys);
  }

  @Override
  public String getDescription() {
    String header;

    if (keys.size() == 1) {
      header = "Disabled rule with key ";
    } else {
      header = "Disable rules with keys ";
    }

    return header +
      keys.stream().map(tag -> "'"+tag+"'").collect(Collectors.joining(", "));
  }

  @Override
  public RuleInstance modify(RuleInstance rule) {
    if (keys.contains(rule.getKey())) {
      return rule.disable(getReason());
    }

    return null;
  }
}
