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

package com.framstag.acousticrules.modifier;

import com.framstag.acousticrules.rules.Rule;
import jakarta.json.bind.annotation.JsonbSubtype;
import jakarta.json.bind.annotation.JsonbTypeInfo;

import java.io.Serializable;

@JsonbTypeInfo(key = "@processor", value = {
  @JsonbSubtype(alias = "ChangeSeverity", type = ChangeSeverity.class),
  @JsonbSubtype(alias = "SetSeverity", type = SetSeverity.class),
  @JsonbSubtype(alias = "SetParamForKey", type = SetParamForKey.class)
})
public interface Modifier extends Serializable {
  String getReason();

  String getReasonString(String prefix);

  String getDescription();

  boolean modify(Rule rule);
}
