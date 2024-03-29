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

import com.framstag.acousticrules.rules.CustomizedRule;
import jakarta.json.bind.annotation.JsonbSubtype;
import jakarta.json.bind.annotation.JsonbTypeInfo;

@JsonbTypeInfo(key = "@processor", value = {
  @JsonbSubtype(alias = "ChangeSeverity", type = ChangeSeverity.class),
  @JsonbSubtype(alias = "SetSeverity", type = SetSeverity.class),
  @JsonbSubtype(alias = "SetParamForKey", type = SetParamForKey.class),
  @JsonbSubtype(alias = "DisableByKey", type = DisableByKey.class)
})
public interface Modifier {
  String getReason();

  String getReasonString(String prefix);

  String getDescription();

  /**
   * Returns a new RuleInstance if the instance was modified, else null
   * @param rule The RuleInstance to modify
   * @return a new instance or null
   */
  CustomizedRule modify(CustomizedRule rule);
}
