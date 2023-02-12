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

package com.framstag.acousticrules.properties;

import com.framstag.acousticrules.exceptions.ParameterException;

import java.util.Map;
import java.util.regex.Pattern;

/**
 * Validates and resolves properties in strings.
 * <p>
 * Property syntax for resolving of properties is "...${PropertyName}..."
 */
public class Propertizer {
  private static final int PROPERTY_NAME_OFFSET = 2;
  private static final int PROPERTY_DECORATION_LENGTH = 3;

  private static final Pattern pattern = Pattern.compile("\\$\\{\\w+}", Pattern.UNICODE_CHARACTER_CLASS);
  private final Map<String, String> properties;

  public Propertizer(Map<String, String> properties) {
    this.properties = Map.copyOf(properties);
  }

  /**
   * Returns true, if either
   * - text does not container property syntax
   * - text holds property syntax and property values are configured
   *
   * @param text text to validate
   * @return true, if OK, else false
   */
  public boolean validate(String text) {
    var matcher = pattern.matcher(text);

    while (matcher.find()) {
      var propertyName = text.substring(matcher.start() + PROPERTY_NAME_OFFSET, matcher.end() - 1);

      if (!properties.containsKey(propertyName)) {
        return false;
      }
    }

    return true;
  }

  public String resolve(String text) {
    var matcher = pattern.matcher(text);
    var resolvedValue = new StringBuilder(text);
    // Since we modify the text with search at the same time we have to calculate a correcting offset to fix positions
    var offset = 0;

    while (matcher.find()) {
      var propertyName = text.substring(matcher.start() + PROPERTY_NAME_OFFSET, matcher.end() - 1);

      if (!properties.containsKey(propertyName)) {
        throw new ParameterException(String.format("Cannot resolve property '%s' in text '%s'", propertyName, text));
      }

      var value = properties.get(propertyName);

      resolvedValue.replace(matcher.start()+offset, matcher.end()+offset, value);

      var lengthDiff = value.length() - (propertyName.length()+PROPERTY_DECORATION_LENGTH);

      offset = offset + lengthDiff;
    }

    return resolvedValue.toString();
  }
}
