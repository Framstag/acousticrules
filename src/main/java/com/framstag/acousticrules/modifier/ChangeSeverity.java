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
import com.framstag.acousticrules.rules.Severity;

public class ChangeSeverity extends Modifier {
  private Severity from;
  private Severity to;

  public Severity getFrom() {
    return from;
  }

  public void setFrom(Severity from) {
    this.from = from;
  }

  public Severity getTo() {
    return to;
  }

  public void setTo(Severity to) {
    this.to = to;
  }

  @Override
  public String getDescription() {
    return "Setting severity from " + from + " to "+ to;
  }

  @Override
  public boolean modify(Rule rule) {
    if (rule.getSeverity()==from) {
      rule.setSeverity(to);
      return true;
    }

    return false;
  }
}
