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

  private static final Logger log = LoggerFactory.getLogger(AcousticRules.class);
  @CommandLine.Option(
    names = {"-p", "--processor"},
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
    names = {"-u", "--dumpUnused"},
    description = "Dump the rules not used in any group")
  private boolean dumpUnusedRules;
  @CommandLine.Option(
    names = {"--stopOnDuplicates"},
    description = "Do not generate a quality profile if the same rule is matched by multiple groups")
  private boolean stopOnDuplicates;
  @CommandLine.Parameters(
    description = "Rule files"
  )
  private List<Path> ruleFiles;

  public static void main(String[] args) {
    int exitCode = new CommandLine(new AcousticRules()).execute(args);
    System.exit(exitCode);
  }

  @Override
  public Integer call() throws Exception {
    List<Rule> rules = new RuleSetLoader().loadRules(ruleFiles);
    List<ProcessingGroup> processingGroups = new ProcessingGroupLoader().loadProcessingGroups(processorSetFiles);
    Map<String, Set<Rule>> rulesByGroup = processRulesToGroups(rules, processingGroups);

    int duplicateCount = dumpRulesInMultipleGroups(rules, rulesByGroup);

    if (stopOnDuplicates && duplicateCount > 0) {
      log.error("There are {} rule(s) in multiple groups, aborting!", duplicateCount);
      return 1;
    }

    Set<Rule> overallRules = rulesByGroup.values().stream().flatMap(Collection::stream).collect(Collectors.toSet());

    log.info("Overall selected rules: {}", overallRules.size());

    if (dumpUnusedRules) {
      dumpUnusedRules(rules, overallRules);
    }

    if (qualityProfileFile != null) {
      var qualityProfile = new QualityProfileLoader().load(qualityProfileFile);

      filterRules(qualityProfile, rulesByGroup);
      modifyRules(qualityProfile, rulesByGroup);

      writeSonarQualityProfile(qualityProfile, rulesByGroup);

      if (qualityProfile.hasDocumentationFilename()) {
        generateDocumentationFile(qualityProfile, rulesByGroup);
      }
    }

    return 0;
  }

  private static Map<String, Set<Rule>> processRulesToGroups(List<Rule> rules, List<ProcessingGroup> processingGroups) {
    Map<String, Set<Rule>> rulesByGroup = new HashMap<>();

    for (var group : processingGroups) {
      log.info("Processing group '{}'...", group.getName());
      Set<Rule> groupRuleSet = selectRules(rules, group.getSelectors());
      filterRules(groupRuleSet, group.getFilters());
      logRules(groupRuleSet);

      rulesByGroup.put(group.getName(), groupRuleSet);
    }

    return rulesByGroup;
  }

  private static int dumpRulesInMultipleGroups(List<Rule> rules, Map<String, Set<Rule>> rulesByGroup) {
    Map<Rule, List<String>> ruleGroupMap = new HashMap<>();

    // Initialize map
    for (var rule : rules) {
      ruleGroupMap.put(rule, new LinkedList<>());
    }

    // Fill the list of groups for each rule
    for (var entry : rulesByGroup.entrySet()) {
      for (var rule : entry.getValue()) {
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

  private static void dumpUnusedRules(List<Rule> rules, Set<Rule> overallRules) {
    log.info("Dump rules not selected in any group...");

    for (var rule : rules) {
      if (!overallRules.contains(rule)) {
        log.atInfo().log(" * {} {} {} {} {} ({})",
          rule.getKey(),
          rule.getSeverity(),
          rule.getType(),
          rule.getStatus(),
          rule.getName(),
          String.join(", ", rule.getSysTags()));
      }
    }
    log.info("Dump rules not selected in any group...done");
  }

  private static void filterRules(QualityProfile qualityProfile, Map<String, Set<Rule>> rulesByGroup) {
    log.info("Filtering rules...");
    for (var group : qualityProfile.groups()) {
      if (!rulesByGroup.containsKey(group.name())) {
        log.atError().log("Quality profile requests filtering of group '{}', but this group does not exist",
            group.name());
        break;
      }

      filterRules(rulesByGroup.get(group.name()),group.filters());
    }
    log.info("Filtering rules done.");
  }

  private static void modifyRules(QualityProfile qualityProfile, Map<String, Set<Rule>> rulesByGroup) {
    log.info("Modifying rules...");
    modifyRules(qualityProfile.groups(), rulesByGroup);
    log.info("Modifying rules done.");
  }

  private static void writeSonarQualityProfile(QualityProfile qualityProfile,
                                               Map<String, Set<Rule>> rulesByGroup)
    throws FileNotFoundException, XMLStreamException {
    log.info("Writing quality profile...");

    var writer = new QualityProfileGenerator();

    writer.write(qualityProfile, rulesByGroup);
    log.info("Writing quality profile...done");
  }

  private static void generateDocumentationFile(QualityProfile qualityProfile,
                                                Map<String, Set<Rule>> rulesByGroup)
    throws IOException {
    log.info("Writing quality profile documentation '{}'...", qualityProfile.documentationFilename());
    var docGenerator = new MarkdownDocGenerator();

    docGenerator.writeMarkdown(qualityProfile, qualityProfile.documentationFilename(), rulesByGroup);
    log.info("Writing quality profile documentation done.");
  }

  private static Set<Rule> selectRules(List<Rule> rules, List<Selector> selectors) {
    Set<Rule> allSelectedRules = new HashSet<>();

    for (var selector : selectors) {
      Set<Rule> selectedRules = new HashSet<>();
      log.info("Selector: {} {}",
        selector.getDescription(),
        selector.getReasonString("- "));
      for (var rule : rules) {
        boolean selected = selector.select(rule);

        if (selected) {
          selectedRules.add(rule);
        }
      }

      allSelectedRules.addAll(selectedRules);
      log.info("{} rules selected => {} rules over all", selectedRules.size(), allSelectedRules.size());
    }

    return allSelectedRules;
  }

  private static void filterRules(Set<Rule> groupRuleSet, List<Filter> filters) {
    for (var filter : filters) {
      List<Rule> filteredRules = new LinkedList<>();
      log.info("Filter: {} {}",
        filter.getDescription(),
        filter.getReasonString("- "));
      for (var rule : new ArrayList<>(groupRuleSet)) {
        boolean filtered = filter.filter(rule);

        if (filtered) {
          filteredRules.add(rule);
        }
      }

      filteredRules.forEach(groupRuleSet::remove);

      log.info("{} filtered => {} rules over all",
        filteredRules.size(),
        groupRuleSet.size());
    }
  }

  private static void logRules(Set<Rule> groupRuleSet) {
    for (var rule : groupRuleSet) {
      log.atInfo().log(" * {} {} {} {} ({})",
        rule.getKey(),
        rule.getSeverity(),
        rule.getType(),
        rule.getName(),
        String.join(", ", rule.getSysTags()));
    }
  }

  private static void modifyRules(Collection<QualityGroup> groups, Map<String, Set<Rule>> groupRulesetMap) {
    for (var group : groups) {
      if (!groupRulesetMap.containsKey(group.name())) {
        log.atError().log("Quality profile requests modification group '{}', but this group does not exist",
            group.name());
        break;
      }

      log.atInfo().log("Modifying group '{}'...", group.name());

      groupRulesetMap.put(group.name(),
        modifyRules(groupRulesetMap.get(group.name()),
          group.modifier()));
    }
  }

  /**
   * Calls the modifier over the given Collection of rules and return a new collection with the new,
   * modified rules.
   * @param rules Set of rules
   * @param modifiers collection of modifiers to be applied
   * @return A new set of modified rules.
   */
  private static Set<Rule> modifyRules(Set<Rule> rules, Collection<Modifier> modifiers) {
    Set<Rule> modifiedRules = new HashSet<>(rules);

    for (var modifier : modifiers) {
      var modifiedCount = 0;

      log.info("Modifier: {} {}",
        modifier.getDescription(),
        modifier.getReasonString("- "));
      for (var rule : modifiedRules) {
        if (modifier.modify(rule)) {
          modifiedCount++;
        }
        modifiedRules.add(rule);
      }

      log.info("{} of {} rules modified",
        modifiedCount,
        rules.size());
    }

    return modifiedRules;
  }
}
