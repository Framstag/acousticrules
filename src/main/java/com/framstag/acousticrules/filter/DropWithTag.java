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
import com.framstag.acousticrules.rules.Ruleable;
import jakarta.json.bind.annotation.JsonbCreator;
import jakarta.json.bind.annotation.JsonbProperty;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

@Description("Drop rules with one of the given tags")
public class DropWithTag extends AbstractFilter {
  private final Set<String> tags;
  private final Set<String> but;

  @JsonbCreator
  public DropWithTag(
    @JsonbProperty("tags") Set<String> tags,
    @JsonbProperty("but") Set<String> but) {
    this.tags = Set.copyOf(tags);

    if (but != null) {
      this.but = Set.copyOf(but);
    } else {
      this.but = Collections.emptySet();
    }
  }

  @Override
  public String getDescription() {
    String tagString;

    if (tags.size() == 1) {
      tagString = "Dropping rules with tag " +
       tags.stream().map(tag -> "'" + tag + "'").collect(Collectors.joining(", "));
    } else {
      tagString = "Dropping rules with tags " +
       tags.stream().map(tag -> "'" + tag + "'").collect(Collectors.joining(", "));
    }

    String butString;

    if (but.isEmpty()) {
      butString = "";
    } else if (but.size() == 1) {
      butString = " but not key " + but.stream().map(key -> "'" + key + "'").collect(Collectors.joining(", "));
    } else {
      butString = " but not keys " + but.stream().map(key -> "'" + key + "'").collect(Collectors.joining(", "));
    }

    return tagString + butString;
  }

  @Override
  public boolean filter(Ruleable rule) {
    for (String tag : rule.getSysTags()) {
      if (tags.contains(tag)) {
        return !but.contains(rule.getKey());
      }
    }

    return false;
  }
}
