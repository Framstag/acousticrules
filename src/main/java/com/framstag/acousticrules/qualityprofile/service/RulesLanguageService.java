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

package com.framstag.acousticrules.qualityprofile.service;

import com.framstag.acousticrules.exception.ParameterException;
import com.framstag.acousticrules.rules.definition.RuleDefinitionGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RulesLanguageService {
  private static final Logger log = LoggerFactory.getLogger(RulesLanguageService.class);

  public String verifyAndReturnLanguage(RuleDefinitionGroup allRuleDefinitions) {
    var language="";

    for (var rule : allRuleDefinitions.getRules()) {
      if (language.isEmpty()) {
        language = rule.getLang();
      } else {
        if (!rule.getLang().equals(language)) {
          throw new ParameterException(String.format("Not all rules have the same language: '%s' vs. '%s'",
            language,rule.getLang()));
        }
      }
    }

    log.info("Detected language: `{}`", language);

    return language;
  }
}
