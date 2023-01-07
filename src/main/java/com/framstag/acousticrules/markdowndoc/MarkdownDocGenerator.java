/*
 * AcousticRuler
 * Copyright 2022 Tim Teulings
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
package com.framstag.acousticrules.markdowndoc;

import com.framstag.acousticrules.qualityprofile.QualityGroup;
import com.framstag.acousticrules.qualityprofile.QualityProfile;
import com.framstag.acousticrules.rules.Rule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MarkdownDocGenerator {

  private static final Logger log = LoggerFactory.getLogger(MarkdownDocGenerator.class);

  public void writeMarkdown(QualityProfile qualityProfile,
                            Path filename,
                            Map<String, Set<Rule>> rulesByGroup) throws IOException {
    try (var writer = new FileWriter(filename.toString(), StandardCharsets.UTF_8)) {
      writer.write("# Documentation");
      writer.write(System.lineSeparator());
      writer.write(System.lineSeparator());

      List<QualityGroup> qualityGroupList = qualityProfile.groups().stream()
        .sorted(Comparator.comparing(QualityGroup::name)).toList();

      for (QualityGroup group : qualityGroupList) {
        if (!rulesByGroup.containsKey(group.name())) {
          log.atError().log("Quality profile requests dump of group '{}', but this group does not exist", group.name());
          break;
        }

        log.atInfo().log("Writing group '{}'...", group.name());

        writer.write("## ");
        writer.write(group.name());
        writer.write(System.lineSeparator());
        writer.write(System.lineSeparator());

        writer.write("|Rule|Description|Severity|");
        writer.write(System.lineSeparator());
        writer.write("|----|-----------|--------|");
        writer.write(System.lineSeparator());

        Set<Rule> rules = rulesByGroup.get(group.name());

        List<Rule> rulesList = rules.stream().sorted(Comparator.comparing(Rule::getKey)).toList();

        for (Rule rule : rulesList) {
          writer.write("|");
          writer.write(rule.getKey());
          writer.write("|");
          writer.write(rule.getName());
          writer.write("|");
          writer.write(rule.getSeverity().name());
          writer.write("|");
          writer.write(System.lineSeparator());
        }

        writer.write(System.lineSeparator());
      }
    }
  }
}
