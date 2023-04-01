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

package com.framstag.acousticrules.rules.instance;

import com.framstag.acousticrules.rules.CustomizedRule;
import com.framstag.acousticrules.rules.Severity;
import com.framstag.acousticrules.rules.Status;
import com.framstag.acousticrules.rules.definition.RuleDefinition;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class CustomizedRuleBuilder {

  private final List<String> sysTags = new LinkedList<>();
  private final UseStatus useStatus = UseStatus.ACTIVE;
  private final Status status = Status.READY;
  private Severity severity = Severity.BLOCKER;
  private String key="";
  private String type="";

  public static CustomizedRuleBuilder rule() {
    return new CustomizedRuleBuilder();
  }

  public CustomizedRuleBuilder withKey(String key) {
    this.key = key;

    return this;
  }

  public CustomizedRuleBuilder withTag(String tag) {
    sysTags.add(tag);

    return this;
  }

  public CustomizedRuleBuilder withType(String type) {
    this.type = type;

    return this;
  }

  public CustomizedRuleBuilder withSeverity(Severity severity) {
    this.severity = severity;

    return this;
  }

  public CustomizedRule build() {
    RuleDefinition definition = new RuleDefinition(key,
      key,
      "",
      severity,
      status,
      "",
      type,
      sysTags,
      Collections.emptySet()
    );

    return new RuleInstance(definition,
      useStatus,
      "",
      severity,
      "",
      new HashMap<>()
      );
  }
}
