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

  private static void writeH1(Writer writer, String title) throws IOException {
    writer.write("# ");
    writer.write(title);
    writeLn(writer);
    writeLn(writer);
  }

  private static void writeH2(Writer writer, String title) throws IOException {
    writer.write("## ");
    writer.write(title);
    writeLn(writer);
    writeLn(writer);
  }

  private static void writeGroups(FileWriter writer,
                                  Map<String, RuleInstanceGroup> rulesByGroup,
                                  List<String> groupNames) throws IOException {
    for (var groupName : groupNames) {
      if (!rulesByGroup.containsKey(groupName)) {
        log.atError().log("Quality profile requests dump of group '{}', but this group does not exist", groupName);
        break;
      }

      log.atInfo().log("Writing group '{}'...", groupName);

      var ruleInstanceGroup = rulesByGroup.get(groupName);

      writeGroupHeader(writer, groupName);
      writeProcessing(writer, ruleInstanceGroup);

      if (!ruleInstanceGroup.getRuleInstances().isEmpty()) {
        writeGroupTable(writer, ruleInstanceGroup);
      }
    }
  }

  private static void writeUnusedRules(FileWriter writer,
                                       RuleDefinitionGroup ruleGroup) throws IOException {
    log.atInfo().log("Writing unused rules...");

    writer.write("|Rule|Type|Description|Severity|Status|Tags|");
    writeLn(writer);
    writer.write("|----|----|-----------|--------|------|----|");
    writeLn(writer);


    List<RuleDefinition> rulesList = ruleGroup
      .getRules()
      .stream()
      .sorted(Comparator.comparing(RuleDefinition::getKey)).toList();

    for (RuleDefinition rule : rulesList) {
      writeSeparator(writer);
      writer.write(rule.getKey());
      writeSeparator(writer);
      writer.write(rule.getType());
      writeSeparator(writer);
      writer.write(escaped(rule.getName()));
      writeSeparator(writer);
      writer.write(rule.getSeverity().name());
      writeSeparator(writer);
      writer.write(rule.getStatus().name());
      writeSeparator(writer);
      writer.write(String.join(",", rule.getSysTags()));
      writeSeparator(writer);
      writeLn(writer);
    }

    writeLn(writer);
  }

  private static void writeGroupHeader(FileWriter writer,
                                       String groupName) throws IOException {
    writeH3(writer, groupName);
  }

  private static void writeProcessing(FileWriter writer,
                                      RuleInstanceGroup ruleGroup) throws IOException {
    var definitionGroup = ruleGroup.getRuleDefinitionGroup();
    var processingGroup = definitionGroup.getProcessingGroup();

    writeH4(writer, "Rule Selectors");

    writer.write("|Selection|Reason|");
    writeLn(writer);
    writer.write("|---------|------|");
    writeLn(writer);

    for (var selector : processingGroup.getSelectors()) {
      writer.write("|");
      writer.write(selector.getDescription());
      writer.write("|");
      if (selector.hasReason()) {
        writer.write(selector.getReason());
      }
      writer.write("|");
      writeLn(writer);
    }

    writeLn(writer);

    writeH4(writer, "Rule Filters");

    writer.write("|Filter|Reason|");
    writeLn(writer);
    writer.write("|---------|------|");
    writeLn(writer);

    for (var filter : processingGroup.getFilters()) {
      writer.write("|");
      writer.write(filter.getDescription());
      writer.write("|");
      if (filter.hasReason()) {
        writer.write(filter.getReason());
      }
      writer.write("|");
      writeLn(writer);
    }

    writeLn(writer);
  }

  private static void writeH4(Writer writer, String title) throws IOException {
    writer.write("#### ");
    writer.write(title);
    writeLn(writer);
    writeLn(writer);
  }

  private static void writeLn(Writer writer) throws IOException {
    writer.write(System.lineSeparator());
  }

  private static void writeGroupTable(FileWriter writer,
                                      RuleInstanceGroup ruleGroup) throws IOException {
    writeH4(writer, "Resulting Rules");

    writer.write("|Rule|Description|Severity|Reason|Parameter|");
    writeLn(writer);
    writer.write("|----|-----------|--------|------|---------|");
    writeLn(writer);


    List<RuleDefinition> rulesList = ruleGroup
      .getRuleDefinitionGroup().getRules()
      .stream()
      .sorted(Comparator.comparing(RuleDefinition::getKey)).toList();

    for (RuleDefinition ruleDefinition : rulesList) {
      writeGroupTableEntry(writer, ruleGroup, ruleDefinition);

      writeLn(writer);
    }

    writeLn(writer);
  }

  private static void writeSeparator(Writer writer) throws IOException {
    writer.write("|");
  }

  private static String escaped(String text) {
    return text
      .replace("<", "&lt;")
      .replace(">", "&gt;");
  }

  private static void writeH3(Writer writer, String title) throws IOException {
    writer.write("### ");
    writer.write(title);
    writeLn(writer);
    writeLn(writer);
  }

  private static void writeGroupTableEntry(FileWriter writer,
                                           RuleInstanceGroup ruleGroup,
                                           RuleDefinition ruleDefinition) throws IOException {
    var ruleInstance = ruleGroup.getRuleInstance(ruleDefinition.getKey());
    var ruleIsDeleted = ruleInstance == null;
    var ruleIsDisabled = ruleInstance != null && ruleInstance.isDisabled();
    var ruleIsUsed = !ruleIsDeleted && !ruleIsDisabled;
    var severityHasChanged = ruleIsUsed &&
      ruleInstance.getSeverity() != ruleDefinition.getSeverity();
    var hasParameter = ruleIsUsed && !ruleInstance.getParameter().isEmpty();

    writeSeparator(writer);

    // Rule

    if (!ruleIsUsed) {
      writer.write(strikedThrough(ruleDefinition.getKey()));
    } else {
      writer.write(ruleDefinition.getKey());
    }

    writeSeparator(writer);

    // Description

    writer.write(escaped(ruleDefinition.getName()));

    writeSeparator(writer);

    // Severity

    if (!ruleIsUsed) {
      writer.write(strikedThrough(ruleDefinition.getSeverity().name()));
    } else if (severityHasChanged) {
      writer.write(strikedThrough(ruleDefinition.getSeverity().name()));
      writer.write(" -> ");
      writer.write(ruleInstance.getSeverity().name());
    } else {
      writer.write(ruleInstance.getSeverity().name());
    }

    writeSeparator(writer);

    // Severity Reason

    if (severityHasChanged && ruleInstance.hasSeverityReason()) {
      writer.write(ruleInstance.getSeverityReason());
      writeLinebreak(writer);
    }
    if (ruleIsDisabled && ruleInstance.hasStatusReason()) {
      writer.write(ruleInstance.getStatusReason());
      writeLinebreak(writer);
    }

    writeSeparator(writer);

    if (hasParameter) {
      for (var entry : ruleInstance.getParameter().entrySet()) {
        writer.write(entry.getKey());
        writer.write("=");
        writer.write(entry.getValue());
        writeLinebreak(writer);
      }
    }

    writeSeparator(writer);
  }

  private static String strikedThrough(String text) {
    return "~~~" + text + "~~~";
  }

  private static void writeLinebreak(Writer writer) throws IOException {
    writer.write("<br />");
  }

  public void writeMarkdown(QualityProfile qualityProfile,
                            Path filename,
                            Map<String, RuleInstanceGroup> rulesByGroup,
                            RuleDefinitionGroup unusedRules) throws IOException {
    try (var writer = new FileWriter(filename.toString(), StandardCharsets.UTF_8)) {
      writeH1(writer, "Documentation");
      writeH2(writer, "Used Rules by Group");

      writeGroups(writer,
        rulesByGroup,
        qualityProfile
          .groups()
          .stream()
          .map(QualityGroup::name)
          .toList());

      writeH2(writer, "Unused Rules");
      writeUnusedRules(writer, unusedRules);
    }
  }
}
