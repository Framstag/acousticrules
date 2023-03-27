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
package com.framstag.acousticrules.filter;

import com.framstag.acousticrules.annotation.Description;
import com.framstag.acousticrules.rules.Rule;
import jakarta.json.bind.annotation.JsonbCreator;
import jakarta.json.bind.annotation.JsonbProperty;

import java.util.Set;
import java.util.stream.Collectors;

@Description("Drop rules with one of the given keys")
public class DropWithKey extends AbstractFilter {
  private final Set<String> keys;

  @JsonbCreator
  public DropWithKey(@JsonbProperty("keys") Set<String> keys) {
    this.keys = Set.copyOf(keys);
  }

  @Override
  public String getDescription() {
    String header;

    if (keys.size() == 1) {
      header = "Dropping rules with key ";
    } else {
      header = "Dropping rules with keys ";
    }

    return header + keys.stream().map(key -> "'"+key+"'").collect(Collectors.joining(", "));
  }

  @Override
  public boolean filter(Rule rule) {
    return keys.contains(rule.getKey());
  }
}
