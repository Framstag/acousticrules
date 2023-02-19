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

import com.framstag.acousticrules.helper.RuleDefinitionBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Set;

class SelectWithKeyTest {

  @Test
  void testNoSelectIfNullSet() {
    var selector = new SelectWithKey(null);
    var rule = RuleDefinitionBuilder.rule()
      .withKey("MATCH")
      .build();

    var selected = selector.select(rule);

    Assertions.assertFalse(selected,"No match on empty filter set");
  }

  @Test
  void testNoSelectIfEmptySet() {
    var selector = new SelectWithKey(Set.of());
    var rule = RuleDefinitionBuilder.rule()
      .withKey("MATCH")
      .build();

    var selected = selector.select(rule);

    Assertions.assertFalse(selected,"No match on empty filter set");
  }

  @Test
  void testSelectIfOneOnOne() {
    var selector = new SelectWithKey(Set.of("MATCH"));
    var rule = RuleDefinitionBuilder.rule()
      .withKey("MATCH")
      .build();

    var selected = selector.select(rule);

    Assertions.assertTrue(selected,"Matching rule gets selected");
  }

  @Test
  void testSelectIfMatchMultipleOnOne() {
    var selector = new SelectWithKey(Set.of("MATCH","OTHER"));
    var rule = RuleDefinitionBuilder.rule()
      .withKey("MATCH")
      .build();

    var selected = selector.select(rule);

    Assertions.assertTrue(selected,"Matching rule gets selected");
  }

  @Test
  void testNoSelectIfNoMatchOneOnOne() {
    var selector = new SelectWithKey(Set.of("MATCH"));
    var rule = RuleDefinitionBuilder.rule()
      .withKey("NO_MATCH")
      .build();

    var selected = selector.select(rule);

    Assertions.assertFalse(selected,"Not matching rule gets selected");
  }
}
