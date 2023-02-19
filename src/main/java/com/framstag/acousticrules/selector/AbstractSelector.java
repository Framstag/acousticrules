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

public abstract class AbstractSelector implements Selector {
  private String reason;

  @Override
  public boolean hasReason() {
    return reason != null && !reason.isBlank();
  }

  @Override
  public String getReason() {
    return reason;
  }

  public String getReasonString(String prefix) {
    String reasonValue = getReason();
    if (reasonValue == null) {
      return "";
    } else {
      return prefix + reasonValue;
    }
  }

  public void setReason(String reason) {
    this.reason = reason;
  }
}
