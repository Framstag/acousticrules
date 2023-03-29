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

package com.framstag.acousticrules.helper;

import com.framstag.acousticrules.rules.Severity;
import com.framstag.acousticrules.rules.Status;
import com.framstag.acousticrules.rules.definition.RuleDefinition;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class RuleDefinitionBuilder {

  private final List<String> sysTags = new LinkedList<>();
  private String key="";
  private String type="";

  private Status status = Status.READY;

  public static RuleDefinitionBuilder rule() {
    return new RuleDefinitionBuilder();
  }

  public RuleDefinitionBuilder withKey(String key) {
    this.key = key;

    return this;
  }

  public RuleDefinitionBuilder withTag(String tag) {
    sysTags.add(tag);

    return this;
  }

  public RuleDefinitionBuilder withType(String type) {
    this.type = type;

    return this;
  }

  public RuleDefinitionBuilder isDeprecated() {
    this.status = Status.DEPRECATED;

    return this;
  }

  public RuleDefinition build() {
    return new RuleDefinition(key,
      key,
      "",
      Severity.BLOCKER,
      status,
      "",
      type,
      sysTags,
      Collections.emptySet()
    );
  }
}
