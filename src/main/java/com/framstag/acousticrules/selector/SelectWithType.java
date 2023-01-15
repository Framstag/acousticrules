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
package com.framstag.acousticrules.selector;

import com.framstag.acousticrules.rules.Ruleable;
import jakarta.json.bind.annotation.JsonbCreator;
import jakarta.json.bind.annotation.JsonbProperty;

import java.util.Set;
import java.util.stream.Collectors;

public class SelectWithType extends AbstractSelector {
  private final Set<String> types;

  @JsonbCreator
  public SelectWithType(@JsonbProperty("types") Set<String> types) {
    this.types = Set.copyOf(types);
  }

  @Override
  public String getDescription() {
    return "Select rules with types(s) " +
      types.stream().map(tag -> "'"+tag+"'").collect(Collectors.joining(", "));
  }

  @Override
  public boolean select(Ruleable rule) {
    return types.contains(rule.getType());
  }

}
