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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.HashMap;
import java.util.Map;

class PropertizerValidateTest {

  @Test
  void testEmptyString() {
    var propertiesMap = new HashMap<String,String>();
    var propertizer = new Propertizer(propertiesMap);

    var result = propertizer.validate("");

    Assertions.assertTrue(result,"Empty string is always valid");
  }

  @Test
  void testNaiveString() {
    var propertiesMap = new HashMap<String,String>();
    var propertizer = new Propertizer(propertiesMap);

    var result = propertizer.validate("Naive String");

    Assertions.assertTrue(result,"String without property is always valid");
  }

  @ParameterizedTest(name = "{index} - validate(\"{0}\")==true")
  @ValueSource(strings = {
    "${property}",
    "Before${property}",
    "${property}After",
    "Before${property}After"
  })
  void testStringContainsExistingProperty(String text) {
    var propertiesMap = Map.of("property","value");
    var propertizer = new Propertizer(propertiesMap);

    var result = propertizer.validate(text);

    Assertions.assertTrue(result,"String as property is valid");
  }

  @ParameterizedTest(name = "{index} - validate(\"{0}\")==true")
  @ValueSource(strings = {
    "$",
    "${}",
    "}${",
    "${#}",
    "${not.valid}"
  })
  void testStringContainsCharactersPartOfPropertyButNotAProperty(String text) {
    var propertiesMap = new HashMap<String,String>();
    var propertizer = new Propertizer(propertiesMap);

    var result = propertizer.validate(text);

    Assertions.assertTrue(result,"String as property is valid");
  }

  @ParameterizedTest(name = "{index} - validate(\"{0}\")==true")
  @ValueSource(strings = {
    "${propertyA}${propertyB}",
    "|${propertyA}|${propertyB}|"
  })
  void testStringContainsMultipleExistingProperty(String text) {
    var propertiesMap = Map.of("propertyA","valueA","propertyB","valueB");
    var propertizer = new Propertizer(propertiesMap);

    var result = propertizer.validate(text);

    Assertions.assertTrue(result,"String with property is valid");
  }

  @Test
  void testStringIsNotExistingProperty() {
    var propertiesMap = Map.of("property","value");
    var propertizer = new Propertizer(propertiesMap);

    var result = propertizer.validate("${prop}");

    Assertions.assertFalse(result,"String with unknown property is invalid");
  }

  @Test
  void testStringWithAlsoANotExistingProperty() {
    var propertiesMap = Map.of("property","value");
    var propertizer = new Propertizer(propertiesMap);

    var result = propertizer.validate("${property}${prop}");

    Assertions.assertFalse(result,"String with unknown property is invalid");
  }
}
