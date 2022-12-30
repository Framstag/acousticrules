package com.framstag.acousticrules.markdowndoc;

import com.framstag.acousticrules.qualityprofile.QualityGroup;
import com.framstag.acousticrules.qualityprofile.QualityProfile;
import com.framstag.acousticrules.rules.Rule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class MarkdownDocGenerator {

  private static final Logger logger = LoggerFactory.getLogger(MarkdownDocGenerator.class);

  public void writeMarkdown(QualityProfile qualityProfile, Path filename, Map<String, Set<Rule>> rulesByGroup) throws IOException {
    try (FileWriter writer = new FileWriter(filename.toString())) {
      writer.write("# Documentation");
      writer.write(System.lineSeparator());
      writer.write(System.lineSeparator());

      List<QualityGroup> qualityGroupList = qualityProfile.getGroups().stream().sorted(Comparator.comparing(QualityGroup::getName)).collect(Collectors.toList());

      for (QualityGroup group : qualityGroupList) {
        if (!rulesByGroup.containsKey(group.getName())) {
          logger.error("Quality profile requests dump of group '{}', but this group does not exist", group.getName());
          break;
        }

        logger.info("Writing group '{}'...", group.getName());

        writer.write("## ");
        writer.write(group.getName());
        writer.write(System.lineSeparator());
        writer.write(System.lineSeparator());

        writer.write("|Rule|Description|Severity|");
        writer.write(System.lineSeparator());
        writer.write("|----|-----------|--------|");
        writer.write(System.lineSeparator());

        Set<Rule> rules = rulesByGroup.get(group.getName());

        List<Rule> rulesList = rules.stream().sorted(Comparator.comparing(Rule::getKey)).collect(Collectors.toList());

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
