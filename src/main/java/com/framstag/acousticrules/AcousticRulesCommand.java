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

import com.framstag.acousticrules.rules.definition.RuleDefinitionGroup;
import com.framstag.acousticrules.service.PropertyService;
import com.framstag.acousticrules.service.RulesLanguageService;
import com.framstag.acousticrules.usecase.DuplicationUseCase;
import com.framstag.acousticrules.usecase.QualityProfileUseCase;
import com.framstag.acousticrules.usecase.RulesToGroupsUseCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.CommandLine;

import java.nio.file.Path;
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

    var rulesToGroupsResult = new RulesToGroupsUseCase().run(ruleFiles,processorSetFiles);

    if (!new DuplicationUseCase().run(rulesToGroupsResult.allRuleDefinitions(),
      rulesToGroupsResult.ruleDefinitionsByGroup(),stopOnDuplicates)) {
      return 1;
    }

    var usedRuleDefinitions = RuleDefinitionGroup.fromRuleDefinitionGroups(rulesToGroupsResult
      .ruleDefinitionsByGroup().values());

    log.info("Overall selected rule definitions: {}", usedRuleDefinitions.size());

    if (qualityProfileFile != null) {
      var language = rulesLanguageService.verifyAndReturnLanguage(rulesToGroupsResult.allRuleDefinitions());
      var unusedRuleDefinitions = rulesToGroupsResult.allRuleDefinitions().filter(usedRuleDefinitions.getRules());

      new QualityProfileUseCase().run(propertyMap,
        language,
        qualityProfileFile,
        rulesToGroupsResult.ruleDefinitionsByGroup(),
        unusedRuleDefinitions);
    }

    return 0;
  }
}
