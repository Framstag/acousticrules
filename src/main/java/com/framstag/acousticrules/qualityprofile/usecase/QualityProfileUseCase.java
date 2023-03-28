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

package com.framstag.acousticrules.qualityprofile.usecase;

import com.framstag.acousticrules.properties.Propertizer;
import com.framstag.acousticrules.qualityprofile.adapter.qualityprofile.QualityProfileRepository;
import com.framstag.acousticrules.rules.definition.RuleDefinitionGroup;
import com.framstag.acousticrules.rules.instance.RuleInstanceGroup;
import com.framstag.acousticrules.qualityprofile.adapter.documentation.DocumentationRepository;
import com.framstag.acousticrules.qualityprofile.service.QualityProfilePropertizerService;
import com.framstag.acousticrules.qualityprofile.service.RuleInstanceService;
import com.framstag.acousticrules.qualityprofile.service.RulesLanguageService;
import com.framstag.acousticrules.qualityprofile.adapter.profilegenerator.SonarQualityProfileRepository;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;

public class QualityProfileUseCase {
  private final RulesLanguageService rulesLanguageService = new RulesLanguageService();
  private final QualityProfilePropertizerService qualityProfilePropertizerService =
    new QualityProfilePropertizerService();
  private final RuleInstanceService ruleInstanceService = new RuleInstanceService();
  private final QualityProfileRepository qualityProfileRepository = new QualityProfileRepository();
  private final SonarQualityProfileRepository sonarQualityProfileRepository = new SonarQualityProfileRepository();
  private final DocumentationRepository documentationRepository = new DocumentationRepository();

  public void run(Map<String, String> propertyMap,
                  Path qualityProfileFile,
                  RuleDefinitionGroup usedRuleDefinitions,
                  RuleDefinitionGroup allRuleDefinitions,
                  Map<String, RuleDefinitionGroup> ruleDefinitionsByGroup) throws IOException {
    var propertizer = new Propertizer(propertyMap);

    var qualityProfile = qualityProfileRepository.load(qualityProfileFile);

    qualityProfile = qualityProfilePropertizerService.propertize(qualityProfile, propertizer);

    var language = rulesLanguageService.verifyAndReturnLanguage(allRuleDefinitions);

    var unusedRuleDefinitions = allRuleDefinitions.filter(usedRuleDefinitions.getRules());

    Map<String, RuleInstanceGroup> ruleInstanceGroupMap = ruleInstanceService.process(qualityProfile,
      ruleDefinitionsByGroup);

    sonarQualityProfileRepository.writeProfile(qualityProfile, language, ruleInstanceGroupMap);

    if (qualityProfile.hasDocumentationFilename()) {
      documentationRepository.writeDocumentation(qualityProfile, ruleInstanceGroupMap, unusedRuleDefinitions);
    }
  }
}
