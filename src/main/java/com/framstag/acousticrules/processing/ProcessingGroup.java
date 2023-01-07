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
package com.framstag.acousticrules.processing;

import com.framstag.acousticrules.filter.Filter;
import com.framstag.acousticrules.modifier.Modifier;
import com.framstag.acousticrules.selector.Selector;
import jakarta.json.bind.annotation.JsonbCreator;
import jakarta.json.bind.annotation.JsonbProperty;

import java.util.Collections;
import java.util.List;

public class ProcessingGroup {
  private final String name;
  private final List<Selector> selectors;
  private final List<Filter> filters;

  private List<Modifier> modifier;

  @JsonbCreator
  public ProcessingGroup(
    @JsonbProperty("name") String name,
    @JsonbProperty("selectors") List<Selector> selectors,
    @JsonbProperty("filters") List<Filter> filters,
    @JsonbProperty("modifier") List<Modifier> modifier) {
    this.name = name;

    if (selectors != null) {
      this.selectors = List.copyOf(selectors);
    } else {
      this.selectors = Collections.emptyList();
    }

    if (filters != null) {
      this.filters = List.copyOf(filters);
    } else {
      this.filters = Collections.emptyList();
    }

    if (modifier != null) {
      this.modifier = List.copyOf(modifier);
    } else {
      this.modifier = Collections.emptyList();
    }
  }

  public String getName() {
    return name;
  }

  public List<Selector> getSelectors() {
    return Collections.unmodifiableList(selectors);
  }

  public List<Filter> getFilters() {
    return Collections.unmodifiableList(filters);
  }

  public List<Modifier> getModifier() {
    return Collections.unmodifiableList(modifier);
  }

  public void setModifier(List<Modifier> modifier) {
    this.modifier = List.copyOf(modifier);
  }
}
