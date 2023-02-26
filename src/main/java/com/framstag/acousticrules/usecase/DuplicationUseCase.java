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

package com.framstag.acousticrules.usecase;

import com.framstag.acousticrules.rules.definition.RuleDefinition;
import com.framstag.acousticrules.rules.definition.RuleDefinitionGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class DuplicationUseCase {
  private static final Logger log = LoggerFactory.getLogger(DuplicationUseCase.class);

  public boolean run(RuleDefinitionGroup allRuleDefinitions,
                     Map<String, RuleDefinitionGroup> ruleDefinitionsByGroup,
                     boolean stopOnDuplicates) {
    int duplicateCount = dumpRulesInMultipleGroups(allRuleDefinitions, ruleDefinitionsByGroup);

    if (stopOnDuplicates && duplicateCount > 0) {
      log.error("There are {} rule(s) in multiple groups, aborting!", duplicateCount);
      return false;
    }

    return true;
  }

  private static int dumpRulesInMultipleGroups(RuleDefinitionGroup allRules, Map<String,
    RuleDefinitionGroup> rulesByGroup) {
    Map<RuleDefinition, List<String>> ruleGroupMap = new HashMap<>();

    // Initialize map
    for (var rule : allRules.getRules()) {
      ruleGroupMap.put(rule, new LinkedList<>());
    }

    // Fill the list of groups for each rule
    for (var entry : rulesByGroup.entrySet()) {
      for (var rule : entry.getValue().getRules()) {
        ruleGroupMap.get(rule).add(entry.getKey());
      }
    }

    log.info("Checking for rules being in multiple groups...");

    var duplicateCount = 0;
    for (var entry : ruleGroupMap.entrySet()) {
      if (entry.getValue().size() > 1) {
        duplicateCount++;
        log.atWarn().log("* Rule {} '{}' is in multiple groups: {}",
          entry.getKey().getKey(),
          entry.getKey().getName(),
          String.join(",", entry.getValue()));
      }
    }

    log.info("Checking for rules being in multiple groups...done");

    return duplicateCount;
  }

}
