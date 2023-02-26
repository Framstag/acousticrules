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
package com.framstag.acousticrules;

import com.framstag.acousticrules.processing.ProcessingGroup;
import com.framstag.acousticrules.processing.ProcessingGroupRepository;
import com.framstag.acousticrules.properties.Propertizer;
import com.framstag.acousticrules.qualityprofile.QualityProfileRepository;
import com.framstag.acousticrules.rules.definition.RuleDefinition;
import com.framstag.acousticrules.rules.definition.RuleDefinitionGroup;
import com.framstag.acousticrules.rules.definition.RulesRepository;
import com.framstag.acousticrules.rules.instance.RuleInstanceGroup;
import com.framstag.acousticrules.service.DocumentationRepository;
import com.framstag.acousticrules.service.PropertyService;
import com.framstag.acousticrules.service.QualityProfilePropertizerService;
import com.framstag.acousticrules.service.RuleInstanceService;
import com.framstag.acousticrules.service.RuleToGroupService;
import com.framstag.acousticrules.service.RulesLanguageService;
import com.framstag.acousticrules.service.SonarQualityProfileRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.CommandLine;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

@CommandLine.Command(name = "AcousticRules",
  mixinStandardHelpOptions = true,
  version = "AcousticRules 0.9",
  description = "Generates a Sonar Quality Profile from a list of sonar rules")
public class AcousticRulesCommand implements Callable<Integer> {

  private static final Logger log = LoggerFactory.getLogger(AcousticRulesCommand.class);
  private final PropertyService propertyService = new PropertyService();
  private final RulesLanguageService rulesLanguageService = new RulesLanguageService();
  private final RuleToGroupService ruleToGroupService = new RuleToGroupService();
  private final QualityProfilePropertizerService qualityProfilePropertizerService = new QualityProfilePropertizerService();
  private final RuleInstanceService ruleInstanceService = new RuleInstanceService();
  private final RulesRepository rulesRepository = new RulesRepository();
  private final ProcessingGroupRepository processingGroupRepository = new ProcessingGroupRepository();
  private final QualityProfileRepository qualityProfileRepository = new QualityProfileRepository();
  private final SonarQualityProfileRepository sonarQualityProfileRepository = new SonarQualityProfileRepository();
  private final DocumentationRepository documentationRepository = new DocumentationRepository();
  @CommandLine.Option(
    names = {"-r", "--rule"},
    paramLabel = "filename",
    description = "File containing a processor group definition")
  private List<Path> processorSetFiles;
  @CommandLine.Option(
    names = {"-q", "--quality_profile"},
    paramLabel = "filename",
    required = true,
    description = "File containing a quality profile definition")
  private Path qualityProfileFile;
  @CommandLine.Option(
    names = {"--stopOnDuplicates"},
    description = "Do not generate a quality profile if the same rule is matched by multiple groups")
  private boolean stopOnDuplicates;
  @CommandLine.Option(
    names = {"-p", "--property"},
    paramLabel = "value",
    description = "Definition of a property")
  private Map<String, String> propertyMap;
  @CommandLine.Parameters(
    description = "Rule files"
  )
  private List<Path> ruleFiles;

  public AcousticRulesCommand() {
    // no code, yet
  }

  @Override
  public Integer call() throws Exception {
    propertyService.dump(propertyMap);

    var allRuleDefinitionsList = rulesRepository.loadRulesFromRulesDownloadFiles(ruleFiles);

    var language = rulesLanguageService.verifyAndReturnLanguage(allRuleDefinitionsList);

    var allRuleDefinitions = RuleDefinitionGroup.fromRuleDefinitionList(allRuleDefinitionsList);

    List<ProcessingGroup> processingGroups = processingGroupRepository.loadProcessingGroups(processorSetFiles);
    Map<String, RuleDefinitionGroup> ruleDefinitionsByGroup = ruleToGroupService.processRulesToGroups(allRuleDefinitions,
      processingGroups);

    int duplicateCount = dumpRulesInMultipleGroups(allRuleDefinitions, ruleDefinitionsByGroup);

    if (stopOnDuplicates && duplicateCount > 0) {
      log.error("There are {} rule(s) in multiple groups, aborting!", duplicateCount);
      return 1;
    }

    var usedRuleDefinitions = RuleDefinitionGroup.fromRuleDefinitionGroups(ruleDefinitionsByGroup.values());
    var unusedRuleDefinitions = allRuleDefinitions.filter(usedRuleDefinitions.getRules());

    log.info("Overall selected rule definitions: {}", usedRuleDefinitions.size());

    if (qualityProfileFile != null) {
      var propertizer = new Propertizer(propertyMap);

      var qualityProfile = qualityProfileRepository.load(qualityProfileFile);

      qualityProfile = qualityProfilePropertizerService.propertize(qualityProfile, propertizer);

      Map<String, RuleInstanceGroup> ruleInstanceGroupMap = ruleInstanceService.process(qualityProfile,
        ruleDefinitionsByGroup);

      sonarQualityProfileRepository.writeProfile(qualityProfile, language, ruleInstanceGroupMap);

      if (qualityProfile.hasDocumentationFilename()) {
        documentationRepository.writeDocumentation(qualityProfile, ruleInstanceGroupMap, unusedRuleDefinitions);
      }
    }

    return 0;
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
