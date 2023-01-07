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
package com.framstag.acousticrules.rules;

import jakarta.json.bind.annotation.JsonbCreator;
import jakarta.json.bind.annotation.JsonbProperty;

import java.util.Objects;

/**
 * Immutable class for rule parameter.
 * equals() and hashCode() are implemented based on the key attribute.
 */
public record Parameter(String key, String defaultValue, String value) {
  @JsonbCreator
  public Parameter(
    @JsonbProperty("key") String key,
    @JsonbProperty("defaultValue") String defaultValue,
    @JsonbProperty("value") String value) {
    this.key = key;
    this.defaultValue = defaultValue;
    this.value = value;
  }

  /**
   * Creates a copy of the Parameter class with the new value assigned.
   * The instance the method is called on stays unmodified.
   * @param value new value
   * @return copy of the instance
   */
  public Parameter setValue(String value) {
    return new Parameter(key,defaultValue,value);
  }

  public boolean hasOverwrittenDefaultValue() {
    if (defaultValue == null && value == null) {
      return false;
    }

    // One value is null, one not
    if (defaultValue == null || value == null) {
      return true;
    }

    return !defaultValue.equals((value));
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }

    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    var parameter = (Parameter) o;
    return key.equals(parameter.key);
  }

  @Override
  public int hashCode() {
    return Objects.hash(key);
  }
}
