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
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.HashMap;
import java.util.Map;

class PropertizerResolveTest {

  @Test
  void testEmptyString() {
    var propertiesMap = new HashMap<String,String>();
    var propertizer = new Propertizer(propertiesMap);

    var result = propertizer.resolve("");

    Assertions.assertEquals("",result,"Empty string should resolve to empty string");
  }

  @Test
  void testNaiveString() {
    var propertiesMap = new HashMap<String,String>();
    var propertizer = new Propertizer(propertiesMap);
    var naiveString = "Naive String";

    var result = propertizer.resolve(naiveString);

    Assertions.assertEquals(naiveString,result,"Naive string should resolve to itself");
  }

  @ParameterizedTest(name = "{index} - resolve(\"{0}\")=={1}")
  @CsvSource({
    "${property},value",
    "Before${property},Beforevalue",
    "${property}After,valueAfter",
    "Before${property}After,BeforevalueAfter"
  })
  void testStringContainsExistingProperty(String text, String expected) {
    var propertiesMap = Map.of("property","value");
    var propertizer = new Propertizer(propertiesMap);

    var result = propertizer.resolve(text);

    Assertions.assertEquals(expected,
      result,
      "String matches to resolved property value");
  }

  @ParameterizedTest(name = "{index} - resolve(\"{0}\")=={0}")
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

      var result = propertizer.resolve(text);

      Assertions.assertEquals(text,result,"Invalid Property definition are untouched");
  }

  @ParameterizedTest(name = "{index} - resolve(\"{0}\")==\"{1}\"")
  @CsvSource({
    "${propertyA}${propertyB},valueAvalueB",
    "|${propertyA}|${propertyB}|,|valueA|valueB|"
  })
  void testStringContainsMultipleExistingProperty(String text, String expected) {
    var propertiesMap = Map.of("propertyA","valueA","propertyB","valueB");
    var propertizer = new Propertizer(propertiesMap);

    var result = propertizer.resolve(text);

    Assertions.assertEquals(expected,result,"String with property is valid");
  }

  @Test
  void testStringIsNotExistingProperty() {
    var propertiesMap = Map.of("property","value");
    var propertizer = new Propertizer(propertiesMap);

    Assertions.assertThrows(ParameterException.class,
      () -> propertizer.resolve("${prop}"),
      "Expect method to throw exception"
    );
  }

  @Test
  void testStringWithAlsoANotExistingProperty() {
    var propertiesMap = Map.of("property","value");
    var propertizer = new Propertizer(propertiesMap);

    Assertions.assertThrows(ParameterException.class,
      () -> propertizer.resolve("${property}${prop}"),
      "Expect method to throw exception"
    );
  }
}
