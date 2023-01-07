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
package com.framstag.acousticrules.qualityprofile;

import com.framstag.acousticrules.filter.Filter;
import com.framstag.acousticrules.modifier.Modifier;
import jakarta.json.bind.annotation.JsonbCreator;
import jakarta.json.bind.annotation.JsonbProperty;

import java.util.Collections;
import java.util.List;

public record QualityGroup(String name,
                           List<Filter> filters,
                           List<Modifier> modifier) {
  @JsonbCreator
  public QualityGroup(@JsonbProperty("name") String name,
                      @JsonbProperty("filters") List<Filter> filters,
                      @JsonbProperty("modifier") List<Modifier> modifier) {
    this.name = name;

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

  @Override
  public List<Filter> filters() {
    return Collections.unmodifiableList(filters);
  }

  @Override
  public List<Modifier> modifier() {
    return Collections.unmodifiableList(modifier);
  }
}
