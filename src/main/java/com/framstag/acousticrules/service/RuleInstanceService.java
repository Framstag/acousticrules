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
import com.framstag.acousticrules.modifier.Modifier;
import com.framstag.acousticrules.qualityprofile.QualityGroup;
import com.framstag.acousticrules.qualityprofile.QualityProfile;
import com.framstag.acousticrules.rules.CustomizedRule;
import com.framstag.acousticrules.rules.definition.RuleDefinitionGroup;
import com.framstag.acousticrules.rules.instance.RuleInstanceGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class RuleInstanceService {
  private static final Logger log = LoggerFactory.getLogger(RuleInstanceService.class);

  public Map<String, RuleInstanceGroup> process(QualityProfile qualityProfile,
                                                Map<String, RuleDefinitionGroup> ruleDefinitionsByGroup) {
    Map<String, RuleInstanceGroup> ruleInstanceGroupMap = createInstanceGroups(qualityProfile,
      ruleDefinitionsByGroup);
    ruleInstanceGroupMap = filterRules(qualityProfile, ruleInstanceGroupMap);
    modifyRules(qualityProfile, ruleInstanceGroupMap);

    return ruleInstanceGroupMap;
  }
  private static Map<String, RuleInstanceGroup>
  createInstanceGroups(QualityProfile qualityProfile,
                       Map<String, RuleDefinitionGroup> ruleDefinitionsByGroup) {
    Map<String, RuleInstanceGroup> ruleInstanceGroupMap = new HashMap<>();
    for (var group : qualityProfile.groups()) {
      if (!ruleDefinitionsByGroup.containsKey(group.name())) {
        log.atError().log("Quality profile references definition group '{}', but this group does not exist",
          group.name());
        break;
      }

      ruleInstanceGroupMap.put(group.name(),
        RuleInstanceGroup.fromDefinitionGroup(ruleDefinitionsByGroup.get(group.name())));
    }

    return ruleInstanceGroupMap;
  }

  private static Map<String, RuleInstanceGroup> filterRules(QualityProfile qualityProfile,
                                                            Map<String, RuleInstanceGroup> rulesByGroup) {
    Map<String, RuleInstanceGroup> filteredGroups = new HashMap<>();
    log.info("Filtering rules...");

    for (var group : qualityProfile.groups()) {
      filteredGroups.put(group.name(),
        filterRules(rulesByGroup.get(group.name()),
          group.filters()));
    }
    log.info("Filtering rules done.");

    return filteredGroups;
  }

  private static void modifyRules(QualityProfile qualityProfile, Map<String, RuleInstanceGroup> rulesByGroup) {
    log.info("Modifying rules...");
    // TODO: Return new, changed map
    modifyRules(qualityProfile.groups(), rulesByGroup);
    log.info("Modifying rules done.");
  }

  private static RuleInstanceGroup filterRules(RuleInstanceGroup groupRuleSet, List<Filter> filters) {
    var modifiedRulesGroup = groupRuleSet;

    for (var filter : filters) {
      List<CustomizedRule> filteredRules = new LinkedList<>();

      log.info("Filter: {} {}",
        filter.getDescription(),
        filter.getReasonString("- "));
      for (var rule : groupRuleSet.getRuleInstances()) {
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

  private static void modifyRules(Collection<QualityGroup> groups, Map<String, RuleInstanceGroup> groupRulesetMap) {
    for (var group : groups) {
      log.atInfo().log("Modifying group '{}'...", group.name());

      groupRulesetMap.put(group.name(),
        modifyRules(groupRulesetMap.get(group.name()),
          group.modifier()));
    }
  }

  private static RuleInstanceGroup modifyRules(RuleInstanceGroup rules, Collection<Modifier> modifiers) {
    RuleInstanceGroup modifiedRules = rules;

    for (var modifier : modifiers) {
      List<CustomizedRule> updatedDefinitions = new LinkedList<>();
      log.info("Modifier: {} {}",
        modifier.getDescription(),
        modifier.getReasonString("- "));
      for (var rule : modifiedRules.getRuleInstances()) {
        var instance = modifier.modify(rule);
        if (instance != null) {
          updatedDefinitions.add(instance);
        }
      }

      log.info("{} of {} rules modified",
        updatedDefinitions.size(),
        modifiedRules.size());

      modifiedRules = modifiedRules.update(updatedDefinitions);
    }

    return modifiedRules;
  }
}
