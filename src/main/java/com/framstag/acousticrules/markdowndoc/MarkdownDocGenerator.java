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
import com.framstag.acousticrules.rules.instance.RuleInstanceGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
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
      writeSeparator(writer);
      writer.write(rule.getKey());
      writeSeparator(writer);
      writer.write(rule.getName());
      writeSeparator(writer);
      writer.write(rule.getSeverity().name());
      writeSeparator(writer);
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


    List<RuleDefinition> rulesList = ruleGroup
      .getRuleDefinitionGroup().getRules()
      .stream()
      .sorted(Comparator.comparing(RuleDefinition::getKey)).toList();

    for (RuleDefinition ruleDefinition : rulesList) {
      var ruleInstance = ruleGroup.getRuleInstance(ruleDefinition.getKey());
      var ruleDeleted = ruleInstance == null;
      var severityChanged = !ruleDeleted && ruleInstance.getSeverity()!=ruleDefinition.getSeverity();

      writeSeparator(writer);

      if (ruleDeleted) {
        writer.write("~~~");
        writer.write(ruleDefinition.getKey());
        writer.write("~~~");
      } else {
        writer.write(ruleDefinition.getKey());
      }

      writeSeparator(writer);

      writer.write(ruleDefinition.getName());

      writeSeparator(writer);

      if (ruleDeleted) {
        writer.write("~~~");
        writer.write(ruleDefinition.getSeverity().name());
        writer.write("~~~");
      } else if (severityChanged) {
        writer.write("~~~");
        writer.write(ruleDefinition.getSeverity().name());
        writer.write("~~~");
        writer.write(" -> ");
        writer.write(ruleInstance.getSeverity().name());
      } else {
        writer.write(ruleInstance.getSeverity().name());
      }

      writeSeparator(writer);

      writer.write(System.lineSeparator());
    }

    writer.write(System.lineSeparator());
  }

  private static void writeSeparator(Writer writer) throws IOException {
    writer.write("|");
  }
}
