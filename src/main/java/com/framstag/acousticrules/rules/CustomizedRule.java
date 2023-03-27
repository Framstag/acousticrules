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

package com.framstag.acousticrules.rules;

import java.util.Map;

/**
 * A customized rule is a Rules that has possibly been changed. A changed rules is not only
 * changed and has different values it also holds additional information regarding its customization
 * and the reasons for customization.
 */
public interface CustomizedRule extends Rule {

  // Change-API

  CustomizedRule disable(String reason);

  CustomizedRule setSeverity(Severity to, String reason);

  CustomizedRule setParameter(String param, String value);

  // Additional information

  boolean isDisabled();

  boolean isActive();

  boolean hasParameter();

  Map<String, String> getParameter();

  boolean hasSeverityReason();

  String getSeverityReason();

  boolean hasStatusReason();

  String getStatusReason();
}
