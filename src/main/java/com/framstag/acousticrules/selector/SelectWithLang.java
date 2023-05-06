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

@Description("Selects a rule based on the language")
public class SelectWithLang extends AbstractSelector {
  private final Set<String> langs;

  @JsonbCreator
  public SelectWithLang(@JsonbProperty("langs") Set<String> langs) {
    if (langs != null) {
      this.langs = Set.copyOf(langs);
    } else {
      this.langs = Collections.emptySet();
    }
  }

  @Override
  public String getDescription() {
    String header;

    if (langs.size() == 1) {
      header = "Selecting rules with language ";
    } else {
      header = "Selecting rules with languages ";
    }

    return header +
      langs.stream().map(lang -> "'"+lang+"'").sorted().collect(Collectors.joining(", "));
  }

  @Override
  public boolean select(Rule rule) {
    return langs.contains(rule.getLang());
  }

}
