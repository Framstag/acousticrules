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

package com.framstag.acousticrules.rulestogroup.usecase;

import com.framstag.acousticrules.rulestogroup.ProcessingGroup;
import com.framstag.acousticrules.rulestogroup.adapter.processinggroup.ProcessingGroupRepository;
import com.framstag.acousticrules.rules.definition.RuleDefinitionGroup;
import com.framstag.acousticrules.rules.definition.RulesRepository;
import com.framstag.acousticrules.rulestogroup.service.RuleToGroupService;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;

public class RulesToGroupsUseCase {
  private final RuleToGroupService ruleToGroupService = new RuleToGroupService();
  private final RulesRepository rulesRepository = new RulesRepository();
  private final ProcessingGroupRepository processingGroupRepository = new ProcessingGroupRepository();


  public RulesToGroupResult run(List<Path> ruleFiles, List<Path> processorSetFiles) {
    var allRuleDefinitionsList = rulesRepository.loadRulesFromRulesDownloadFiles(ruleFiles);

    var allRuleDefinitions = RuleDefinitionGroup.fromRuleDefinitionList(allRuleDefinitionsList);

    List<ProcessingGroup> processingGroups = processingGroupRepository.loadProcessingGroups(processorSetFiles);
    Map<String, RuleDefinitionGroup> ruleDefinitionsByGroup = ruleToGroupService.processRulesToGroups(
      allRuleDefinitions,
      processingGroups);

    return new RulesToGroupResult(allRuleDefinitions,ruleDefinitionsByGroup);
  }
}
