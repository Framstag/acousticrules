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
import com.framstag.acousticrules.rules.definition.RuleDefinition;
import com.framstag.acousticrules.rules.definition.RuleDefinitionGroup;
import com.framstag.acousticrules.rules.instance.RuleInstance;
import com.framstag.acousticrules.rules.instance.RuleInstanceGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class MarkdownDocGenerator {

  private static final Logger log = LoggerFactory.getLogger(MarkdownDocGenerator.class);

  public void writeMarkdown(QualityProfile qualityProfile,
                            Path filename,
                            Map<String, RuleInstanceGroup> rulesByGroup,
                            RuleDefinitionGroup unusedRules) throws IOException {
    try (var writer = new FileWriter(filename.toString(), StandardCharsets.UTF_8)) {
      writer.write("# Documentation");
      writer.write(System.lineSeparator());
      writer.write(System.lineSeparator());

      writer.write("## Used Rules by Group");
      writer.write(System.lineSeparator());
      writer.write(System.lineSeparator());
      writeGroups(writer, rulesByGroup, qualityProfile.groups().stream().map(QualityGroup::name).toList());

      writer.write("## Unused Rules");
      writer.write(System.lineSeparator());
      writer.write(System.lineSeparator());
      writeUnusedRules(writer, unusedRules);
    }
  }

  private static void writeGroups(FileWriter writer,
                                  Map<String, RuleInstanceGroup> rulesByGroup,
                                  List<String> groupNames) throws IOException {
    for (var groupName : groupNames) {
      if (!rulesByGroup.containsKey(groupName)) {
        log.atError().log("Quality profile requests dump of group '{}', but this group does not exist", groupName);
        break;
      }

      writeGroup(writer, rulesByGroup.get(groupName), groupName);
    }
  }

  private static void writeUnusedRules(FileWriter writer,
                                       RuleDefinitionGroup ruleGroup) throws IOException {
    log.atInfo().log("Writing unused rules...");

    writer.write("|Rule|Description|Severity|");
    writer.write(System.lineSeparator());
    writer.write("|----|-----------|--------|");
    writer.write(System.lineSeparator());


    List<RuleDefinition> rulesList = ruleGroup
      .getRules()
      .stream()
      .sorted(Comparator.comparing(RuleDefinition::getKey)).toList();

    for (RuleDefinition rule : rulesList) {
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

  private static void writeGroup(FileWriter writer,
                                 RuleInstanceGroup ruleGroup,
                                 String groupName) throws IOException {
    log.atInfo().log("Writing group '{}'...", groupName);

    writer.write("### ");
    writer.write(groupName);
    writer.write(System.lineSeparator());
    writer.write(System.lineSeparator());

    writer.write("|Rule|Description|Severity|");
    writer.write(System.lineSeparator());
    writer.write("|----|-----------|--------|");
    writer.write(System.lineSeparator());


    List<RuleInstance> rulesList = ruleGroup
      .getRuleInstances()
      .stream()
      .sorted(Comparator.comparing(RuleInstance::getKey)).toList();

    for (RuleInstance rule : rulesList) {
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
