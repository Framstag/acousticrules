/*
  Copyright 2022 Tim Teulings

  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at

      http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
 */
package com.framstag.acousticrules;

import com.framstag.acousticrules.filter.Filter;
import com.framstag.acousticrules.markdowndoc.MarkdownDocGenerator;
import com.framstag.acousticrules.modifier.Modifier;
import com.framstag.acousticrules.processing.ProcessingGroup;
import com.framstag.acousticrules.processing.ProcessingGroupLoader;
import com.framstag.acousticrules.qualityprofile.QualityGroup;
import com.framstag.acousticrules.qualityprofile.QualityProfile;
import com.framstag.acousticrules.qualityprofile.QualityProfileGenerator;
import com.framstag.acousticrules.qualityprofile.QualityProfileLoader;
import com.framstag.acousticrules.rules.Rule;
import com.framstag.acousticrules.rules.RuleSet;
import com.framstag.acousticrules.rules.RuleSetLoader;
import com.framstag.acousticrules.selector.Selector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.CommandLine;

import javax.xml.stream.XMLStreamException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

@CommandLine.Command(name = "AcousticRules",
  mixinStandardHelpOptions = true,
  version = "AcousticRules 0.9",
  description = "Generates a Sonar Quality Profile from a list of sonar rules")
public class AcousticRules implements Callable<Integer> {

  @CommandLine.Option(names = {"-p", "--processor"}, paramLabel = "filename", description = "File containing a processor group definition")
  private List<String> processorSetFiles;

  @CommandLine.Option(names = {"-q", "--quality_profile"}, paramLabel = "filename", required = true, description = "File containing a quality profile definition")
  private Path qualityProfileFile;

  @CommandLine.Option(names = {"-u", "--dumpUnused"}, description = "Dump the rules not used in any group")
  private boolean dumpUnusedRules = false;

  @CommandLine.Option(names = {"--stopOnDuplicates"}, description = "Do not generate a quality profile if the same rule is matched by multiple groups")
  private boolean stopOnDuplicates = false;

  @CommandLine.Parameters(description = "Rule files")
  private List<String> ruleFiles;


  private static final Logger logger = LoggerFactory.getLogger(AcousticRules.class);

  public static void main(String[] args) {
    int exitCode = new CommandLine(new AcousticRules()).execute(args);
    System.exit(exitCode);
  }

  @Override
  public Integer call() throws Exception {
    List<ProcessingGroup> processingGroups = new LinkedList<>();

    for (String filename : processorSetFiles) {
      ProcessingGroup processingGroup = loadProcessingGroup(filename);
      if (processingGroup == null) {
        return 1;
      }

      processingGroups.add(processingGroup);
    }

    List<Rule> rules = loadRules(ruleFiles);
    if (rules == null) {
      return 1;
    }

    Map<String, Set<Rule>> rulesByGroup = new HashMap<>();
    for (ProcessingGroup group : processingGroups) {
      logger.info("Processing group '{}'...", group.getName());
      Set<Rule> groupRuleSet = selectRules(rules, group);

      // TODO: Rewrite API to be more functional
      filterRules(group.getFilters(), groupRuleSet);
      logRules(groupRuleSet);

      rulesByGroup.put(group.getName(), groupRuleSet);
    }

    int duplicateCount = dumpRulesInMultipleGroups(rules, rulesByGroup);

    if (stopOnDuplicates && duplicateCount > 0) {
      logger.error("There are {} rule(s) in multiple groups, aborting!", duplicateCount);
      return 1;
    }

    Set<Rule> overallRules = rulesByGroup.values().stream().flatMap(Collection::stream).collect(Collectors.toSet());

    logger.info("Overall rules: {}", overallRules.size());

    if (dumpUnusedRules) {
      dumpUnusedRules(rules, overallRules);
    }

    if (qualityProfileFile != null) {
      QualityProfile qualityProfile = loadQualityProfile();

      filterRules(qualityProfile, rulesByGroup);
      modifyRules(qualityProfile, rulesByGroup);

      writeSonarQualityProfile(qualityProfile, rulesByGroup);

      if (qualityProfile.hasDocumentationFilename()) {
        generateDocumentationFile(qualityProfile, rulesByGroup);
      }
    }

    return 0;
  }

  private static void filterRules(QualityProfile qualityProfile, Map<String, Set<Rule>> rulesByGroup) {
    logger.info("Filtering rules...");
    for (QualityGroup group : qualityProfile.getGroups()) {
      if (!rulesByGroup.containsKey(group.getName())) {
        logger.error("Quality profile requests filtering of group '{}', but this group does not exist", group.getName());
        break;
      }

      filterRules(group.getFilters(), rulesByGroup.get(group.getName()));
    }
    logger.info("Filtering rules done.");
  }

  private static int dumpRulesInMultipleGroups(List<Rule> rules, Map<String, Set<Rule>> rulesByGroup) {
    int duplicateCount = 0;
    Map<Rule, List<String>> ruleGroupMap = new HashMap<>();

    // Initialize map
    for (Rule rule : rules) {
      ruleGroupMap.put(rule, new LinkedList<>());
    }

    // Fill the list of groups for each rule
    for (Map.Entry<String, Set<Rule>> entry : rulesByGroup.entrySet()) {
      for (Rule rule : entry.getValue()) {
        ruleGroupMap.get(rule).add(entry.getKey());
      }
    }

    logger.info("Checking for rules being in multiple groups...");

    for (Map.Entry<Rule, List<String>> entry : ruleGroupMap.entrySet()) {
      if (entry.getValue().size() > 1) {
        duplicateCount++;
        logger.warn("* Rule {} '{}' is in multiple groups: {}",
          entry.getKey().getKey(),
          entry.getKey().getName(),
          String.join(",", entry.getValue()));
      }
    }

    logger.info("Checking for rules being in multiple groups...done");

    return duplicateCount;
  }

  private static void logRules(Set<Rule> groupRuleSet) {
    for (Rule rule : groupRuleSet) {
      logger.info(" * {} {} {} {} ({})",
        rule.getKey(),
        rule.getSeverity(),
        rule.getType(),
        rule.getName(),
        String.join(", ", rule.getSysTags()));
    }
  }

  private static void modifyRules(Collection<QualityGroup> groups, Map<String,Set<Rule>> groupRulesetMap) {
    for (QualityGroup group : groups) {
      if (!groupRulesetMap.containsKey(group.getName())) {
        logger.error("Quality profile requests modification group '{}', but this group does not exist", group.getName());
        break;
      }

      Set<Rule> rules = groupRulesetMap.get(group.getName());

      if (group.getModifier() != null) {
        logger.info("Modifying group '{}'...", group.getName());
        for (Modifier modifier : group.getModifier()) {
          int modifiedCount = 0;

          logger.info("Modifier: {} {}",
            modifier.getDescription(),
            modifier.getReason() != null ? "- " + modifier.getReason() : "");
          for (Rule rule : rules) {
            if (modifier.modify(rule)) {
              modifiedCount++;
            }
          }

          logger.info("{} of {} rules modified",
            modifiedCount,
            rules.size());

        }
      }
    }
  }

  private static void filterRules(List<Filter> filters, Set<Rule> groupRuleSet) {
    if (filters != null) {
      for (Filter filter : filters) {
        List<Rule> filteredRules = new LinkedList<>();
        logger.info("Filter: {} {}",
          filter.getDescription(),
          filter.getReason() != null ? "- " + filter.getReason() : "");
        for (Rule rule : new ArrayList<>(groupRuleSet)) {
          boolean filtered = filter.filter(rule);

          if (filtered) {
            filteredRules.add(rule);
          }
        }

        filteredRules.forEach(groupRuleSet::remove);

        logger.info("{} filtered => {} rules over all",
          filteredRules.size(),
          groupRuleSet.size());
      }
    }
  }

  private static Set<Rule> selectRules(List<Rule> rules, ProcessingGroup group) {
    Set<Rule> allSelectedRules = new HashSet<>();
    if (group.getSelectors() != null) {
      for (Selector selector : group.getSelectors()) {
        Set<Rule> selectedRules = new HashSet<>();
        logger.info("Selector: {} {}",
          selector.getDescription(),
          selector.getReason() != null ? "- " + selector.getReason() : "");
        for (Rule rule : rules) {
          boolean selected = selector.select(rule);

          if (selected) {
            selectedRules.add(rule);
          }
        }

        allSelectedRules.addAll(selectedRules);
        logger.info("{} rules selected => {} rules over all", selectedRules.size(), allSelectedRules.size());
      }
    }

    return allSelectedRules;
  }

  private static ProcessingGroup loadProcessingGroup(String processorSetFile) {
    ProcessingGroupLoader processingGroupLoader = new ProcessingGroupLoader();

    logger.info("Loading processing group '{}'", processorSetFile);

    ProcessingGroup processingGroup = processingGroupLoader.load(Path.of(processorSetFile));

    if (processingGroup == null) {
      logger.error("Cannot load processor set");
      return null;
    }

    logger.info("Processing group loaded.");
    return processingGroup;
  }

  private static List<Rule> loadRules(List<String> arguments) {
    RuleSetLoader ruleSetLoader = new RuleSetLoader();
    List<Rule> rules = new LinkedList<>();

    for (String ruleSetFilename : arguments) {
      logger.info("Loading rule set '{}'", ruleSetFilename);
      RuleSet ruleSet = ruleSetLoader.load(Path.of(ruleSetFilename));

      if (ruleSet == null) {
        logger.error("Cannot load rules");
        return null;
      }

      logger.info("Rule set with {} rules loaded.", ruleSet.getRuleCount());

      if (ruleSet.hasRules()) {
        rules.addAll(ruleSet.getRules());
      }
    }

    logger.info("{} rules over all files loaded.", rules.size());

    return rules;
  }

  private static void writeSonarQualityProfile(QualityProfile qualityProfile, Map<String, Set<Rule>> rulesByGroup) throws FileNotFoundException, XMLStreamException {
    logger.info("Writing quality profile...");

    QualityProfileGenerator writer = new QualityProfileGenerator();

    writer.write(qualityProfile, rulesByGroup);
    logger.info("Writing quality profile...done");
  }

  private static void modifyRules(QualityProfile qualityProfile, Map<String, Set<Rule>> rulesByGroup) {
    logger.info("Modifying rules...");
    modifyRules(qualityProfile.getGroups(), rulesByGroup);
    logger.info("Modifying rules done.");
  }

  private QualityProfile loadQualityProfile() {
    logger.info("Loading quality profile '{}'", qualityProfileFile);
    QualityProfileLoader qualityProfileLoader = new QualityProfileLoader();

    QualityProfile qualityProfile = qualityProfileLoader.load(qualityProfileFile);
    logger.info("Loading quality profile done.");
    return qualityProfile;
  }

  private static void generateDocumentationFile(QualityProfile qualityProfile, Map<String, Set<Rule>> rulesByGroup) throws IOException {
    logger.info("Writing quality profile documentation '{}'...", qualityProfile.getDocumentationFilename());
    MarkdownDocGenerator docGenerator = new MarkdownDocGenerator();

    docGenerator.writeMarkdown(qualityProfile, qualityProfile.getDocumentationFilename(), rulesByGroup);
    logger.info("Writing quality profile documentation done.");
  }

  private static void dumpUnusedRules(List<Rule> rules, Set<Rule> overallRules) {
    logger.info("Dump rules not selected in any group...");

    for (Rule rule : rules) {
      if (!overallRules.contains(rule)) {
        logger.info(" * {} {} {} {} {} ({})",
          rule.getKey(),
          rule.getSeverity(),
          rule.getType(),
          rule.getStatus(),
          rule.getName(),
          String.join(", ", rule.getSysTags()));
      }
    }
    logger.info("Dump rules not selected in any group...done");
  }
}
