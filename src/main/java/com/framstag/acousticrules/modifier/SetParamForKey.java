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
import com.framstag.acousticrules.rules.instance.RuleInstance;
import jakarta.json.bind.annotation.JsonbCreator;
import jakarta.json.bind.annotation.JsonbProperty;

@Description("Set a parameter value for the rule with the given key")
public class SetParamForKey extends AbstractModifier {
  private final String key;

  private final String param;

  private final String value;

  @JsonbCreator
  public SetParamForKey(@JsonbProperty("key") String key,
                        @JsonbProperty("param") String param,
                        @JsonbProperty("value") String value) {
    this.key = key;
    this.param = param;
    this.value = value;
  }

  @Override
  public String getDescription() {
    return "Setting param "+param + " for key " + key + " to value '" + value + "'";
  }

  @Override
  public RuleInstance modify(RuleInstance rule) {
    if (key.equals(rule.getKey())) {
      return rule.setParameter(param,value);
    }

    return null;
  }
}
