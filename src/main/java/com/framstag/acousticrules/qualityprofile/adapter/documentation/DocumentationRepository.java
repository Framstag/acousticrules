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

package com.framstag.acousticrules.qualityprofile.adapter.documentation;

import com.framstag.acousticrules.qualityprofile.QualityProfile;
import com.framstag.acousticrules.rules.definition.RuleDefinitionGroup;
import com.framstag.acousticrules.rules.instance.RuleInstanceGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Map;

public class DocumentationRepository {
  private static final Logger log = LoggerFactory.getLogger(DocumentationRepository.class);

  public void writeDocumentation(QualityProfile qualityProfile,
                                 Map<String, RuleInstanceGroup> rulesByGroup,
                                 RuleDefinitionGroup unusedRules)
    throws IOException {
    log.info("Writing quality profile documentation '{}'...", qualityProfile.documentationFilename());
    var docGenerator = new MarkdownDocGenerator();

    docGenerator.writeMarkdown(qualityProfile,
      qualityProfile.documentationFilename(),
      rulesByGroup,
      unusedRules);
    log.info("Writing quality profile documentation done.");
  }
}
