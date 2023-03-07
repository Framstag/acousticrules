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
package com.framstag.acousticrules.selector;

import com.framstag.acousticrules.rules.Ruleable;
import jakarta.json.bind.annotation.JsonbCreator;
import jakarta.json.bind.annotation.JsonbProperty;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

public class SelectWithKey extends AbstractSelector {
  private final Set<String> keys;

  @JsonbCreator
  public SelectWithKey(@JsonbProperty("keys") Set<String> keys) {
    if (keys != null) {
      this.keys = Set.copyOf(keys);
    } else {
      this.keys = Collections.emptySet();
    }
  }

  @Override
  public String getDescription() {
    String header;

    if (keys.size() == 1) {
      header = "Selecting rules with key ";
    } else {
      header = "Selecting rules with keys ";
    }

    return header +
      keys.stream().map(key -> "'"+key+"'").sorted().collect(Collectors.joining(", "));
  }

  @Override
  public boolean select(Ruleable rule) {
    return keys.contains(rule.getKey());
  }

}
