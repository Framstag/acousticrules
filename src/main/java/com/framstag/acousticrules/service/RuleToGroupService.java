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

package com.framstag.acousticrules.service;

import com.framstag.acousticrules.filter.Filter;
import com.framstag.acousticrules.processing.ProcessingGroup;
import com.framstag.acousticrules.rules.definition.RuleDefinition;
import com.framstag.acousticrules.rules.definition.RuleDefinitionGroup;
import com.framstag.acousticrules.selector.Selector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class RuleToGroupService {
  private static final Logger log = LoggerFactory.getLogger(RuleToGroupService.class);

  public Map<String, RuleDefinitionGroup> processRulesToGroups(RuleDefinitionGroup allRules,
                                                               List<ProcessingGroup> processingGroups) {
    Map<String, RuleDefinitionGroup> rulesByGroup = new HashMap<>();

    for (var group : processingGroups) {
      log.info("Processing group '{}'...", group.getName());
      var ruleDefinitionGroup = RuleDefinitionGroup.fromProcessingGroup(group);
      ruleDefinitionGroup = selectRules(ruleDefinitionGroup, allRules, group.getSelectors());
      ruleDefinitionGroup = filterRules(ruleDefinitionGroup, group.getFilters());
      logRules(ruleDefinitionGroup);

      rulesByGroup.put(group.getName(), ruleDefinitionGroup);
    }

    return rulesByGroup;
  }

  private static RuleDefinitionGroup selectRules(RuleDefinitionGroup processingGroup,
                                          RuleDefinitionGroup allRules,
                                          List<Selector> selectors) {
    Set<RuleDefinition> allSelectedRules = new HashSet<>();

    for (var selector : selectors) {
      Set<RuleDefinition> selectedRules = new HashSet<>();
      log.info("Selector: {} {}",
        selector.getDescription(),
        selector.getReasonString("- "));
      for (var rule : allRules.getRules()) {
        boolean selected = selector.select(rule);

        if (selected) {
          selectedRules.add(rule);
        }
      }

      allSelectedRules.addAll(selectedRules);
      log.info("{} rules selected => {} rules over all", selectedRules.size(), allSelectedRules.size());
    }

    return processingGroup.addOrUpdate(allSelectedRules);
  }

  private static RuleDefinitionGroup filterRules(RuleDefinitionGroup groupRuleSet, List<Filter> filters) {
    var modifiedRulesGroup = groupRuleSet;

    for (var filter : filters) {
      List<RuleDefinition> filteredRules = new LinkedList<>();

      log.info("Filter: {} {}",
        filter.getDescription(),
        filter.getReasonString("- "));
      for (var rule : groupRuleSet.getRules()) {
        boolean filtered = filter.filter(rule);
        if (filtered) {
          filteredRules.add(rule);
        }
      }

      modifiedRulesGroup = modifiedRulesGroup.filter(filteredRules);

      log.info("{} filtered => {} rules over all",
        filteredRules.size(),
        modifiedRulesGroup.size());
    }

    return modifiedRulesGroup;
  }

  private static void logRules(RuleDefinitionGroup ruleDefinitionGroup) {
    for (var rule : ruleDefinitionGroup.getRules()) {
      log.atInfo().log(" * {} {} {} {} ({})",
        rule.getKey(),
        rule.getSeverity(),
        rule.getType(),
        rule.getName(),
        String.join(", ", rule.getSysTags()));
    }
  }

}
