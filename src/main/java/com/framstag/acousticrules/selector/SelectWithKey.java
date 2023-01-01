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

import com.framstag.acousticrules.rules.Rule;

import java.util.Set;
import java.util.stream.Collectors;

public class SelectWithKey extends AbstractSelector {
  private Set<String> keys;

  public void setKeys(Set<String> keys) {
    this.keys = keys;
  }

  @Override
  public String getDescription() {
    return "Select rules with keys(s) " +
      keys.stream().map(key -> "'"+key+"'").collect(Collectors.joining(", "));
  }

  @Override
  public boolean select(Rule rule) {
    return keys.contains(rule.getKey());
  }

}
