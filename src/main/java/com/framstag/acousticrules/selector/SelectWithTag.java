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

import com.framstag.acousticrules.annotation.Description;
import com.framstag.acousticrules.rules.Rule;
import jakarta.json.bind.annotation.JsonbCreator;
import jakarta.json.bind.annotation.JsonbProperty;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

@Description("Selects a rule based on one or more rule tags")
public class SelectWithTag extends AbstractSelector {
  private final Set<String> tags;

  @JsonbCreator
  public SelectWithTag(@JsonbProperty("tags") Set<String> tags) {
    if (tags != null) {
      this.tags = Set.copyOf(tags);
    } else {
      this.tags = Collections.emptySet();
    }
  }

  @Override
  public String getDescription() {
    String header;

    if (tags.size() == 1) {
      header = "Selecting rules with tag ";
    } else {
      header = "Selecting rules with tags ";
    }

    return header +
      tags.stream().map(tag -> "'"+tag+"'").sorted().collect(Collectors.joining(", "));
  }

  @Override
  public boolean select(Rule rule) {
    for (String tag : rule.getSysTags()) {
      if (tags.contains(tag)) {
        return true;
      }
    }

    return false;
  }
}
