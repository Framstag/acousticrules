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

@Description("Drop rules with one of the given tags")
public class DropWithLang extends AbstractFilter {
  private final Set<String> langs;

  @JsonbCreator
  public DropWithLang(
    @JsonbProperty("langs") Set<String> langs) {
    this.langs = Set.copyOf(langs);
  }

  @Override
  public String getDescription() {
    String description;

    if (langs.isEmpty()) {
      description = "Dropping no rules";
    } else if (langs.size() == 1) {
      description = "Dropping rules with language " +
       langs.stream().map(lang -> "'" + lang + "'").sorted().collect(Collectors.joining(", "));
    } else {
      description = "Dropping rules with languages " +
       langs.stream().map(lang -> "'" + lang + "'").sorted().collect(Collectors.joining(", "));
    }

    return description;
  }

  @Override
  public boolean filter(Rule rule) {
    return langs.contains(rule.getLang());
  }
}
