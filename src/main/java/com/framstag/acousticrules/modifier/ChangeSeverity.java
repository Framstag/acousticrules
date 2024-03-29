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
package com.framstag.acousticrules.modifier;

import com.framstag.acousticrules.annotation.Description;
import com.framstag.acousticrules.rules.CustomizedRule;
import com.framstag.acousticrules.rules.Severity;
import jakarta.json.bind.annotation.JsonbCreator;
import jakarta.json.bind.annotation.JsonbProperty;

@Description("Change the severity of all rules with the given severity to a new one")
public class ChangeSeverity extends AbstractModifier {
  private final Severity from;
  private final Severity to;

  @JsonbCreator
  public ChangeSeverity(@JsonbProperty("from") Severity from,
                        @JsonbProperty("to") Severity to) {
    this.from = from;
    this.to = to;
  }

  @Override
  public String getDescription() {
    return "Setting severity from " + from + " to "+ to;
  }

  @Override
  public CustomizedRule modify(CustomizedRule rule) {
    if (rule.getSeverity()==from) {
      return rule.setSeverity(to,getReason());
    }

    return null;
  }
}
