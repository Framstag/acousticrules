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
package com.framstag.acousticrules.filter;

import com.framstag.acousticrules.rules.Ruleable;
import com.framstag.acousticrules.rules.Status;
import jakarta.json.bind.annotation.JsonbCreator;

public class RemoveDeprecated extends AbstractFilter {
  @JsonbCreator
  public RemoveDeprecated() {
    // To keep the code in line with the other Filter
  }

  @Override
  public String getDescription() {
    return "Removing deprecated rules";
  }

  @Override
  public boolean filter(Ruleable rule) {
    return rule.getStatus() == Status.DEPRECATED;
  }
}
