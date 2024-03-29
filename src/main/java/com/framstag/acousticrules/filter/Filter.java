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

package com.framstag.acousticrules.filter;

import com.framstag.acousticrules.rules.Rule;
import jakarta.json.bind.annotation.JsonbSubtype;
import jakarta.json.bind.annotation.JsonbTypeInfo;

@JsonbTypeInfo(key = "@processor", value = {
  @JsonbSubtype(alias = "DropWithTag", type = DropWithTag.class),
  @JsonbSubtype(alias = "DropWithKey", type = DropWithKey.class),
  @JsonbSubtype(alias = "DropWithLang", type = DropWithLang.class),
  @JsonbSubtype(alias = "DropWithType", type = DropWithType.class),
  @JsonbSubtype(alias = "DropNotWithType", type = DropNotWithType.class),
  @JsonbSubtype(alias = "RemoveDeprecated", type = RemoveDeprecated.class)
})
public interface Filter {
  boolean hasReason();
  String getReason();

  String getReasonString(String prefix);

  String getDescription();

  boolean filter(Rule rule);
}
