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
import com.framstag.acousticrules.processing.ProcessingGroupRepository;
import com.framstag.acousticrules.qualityprofile.QualityGroup;
import com.framstag.acousticrules.qualityprofile.QualityProfile;
import com.framstag.acousticrules.qualityprofile.QualityProfileGenerator;
import com.framstag.acousticrules.qualityprofile.QualityProfileRepository;
import com.framstag.acousticrules.rules.definition.RuleDefinition;
import com.framstag.acousticrules.rules.definition.RuleDefinitionGroup;
import com.framstag.acousticrules.rules.definition.RulesRepository;
import com.framstag.acousticrules.rules.instance.RuleInstance;
import com.framstag.acousticrules.rules.instance.RuleInstanceGroup;
import com.framstag.acousticrules.selector.Selector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.CommandLine;

import javax.xml.stream.XMLStreamException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;

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
    var allRuleDefinitionsList = new RulesRepository().loadRulesFromRulesDownloadFiles(ruleFiles);
    var allRuleDefinitions = RuleDefinitionGroup.fromRuleDefinitionList(allRuleDefinitionsList);
    List<ProcessingGroup> processingGroups = new ProcessingGroupRepository().loadProcessingGroups(processorSetFiles);
    Map<String,RuleDefinitionGroup> ruleDefinitionsByGroup = processRulesToGroups(allRuleDefinitions,
      processingGroups);

    int duplicateCount = dumpRulesInMultipleGroups(allRuleDefinitions, ruleDefinitionsByGroup);

    if (stopOnDuplicates && duplicateCount > 0) {
      log.error("There are {} rule(s) in multiple groups, aborting!", duplicateCount);
      return 1;
    }

    var usedRuleDefinitions = RuleDefinitionGroup.fromRuleDefinitionGroups(ruleDefinitionsByGroup.values());
    var unusedRuleDefinitions = allRuleDefinitions.filter(usedRuleDefinitions.getRules());

    log.info("Overall selected rule definitions: {}", usedRuleDefinitions.size());

    if (qualityProfileFile != null) {
      var qualityProfile = new QualityProfileRepository().load(qualityProfileFile);

      Map<String,RuleInstanceGroup> ruleInstanceGroupMap = createInstanceGroups(qualityProfile, ruleDefinitionsByGroup);
      ruleInstanceGroupMap = filterRules(qualityProfile, ruleInstanceGroupMap);
      modifyRules(qualityProfile, ruleInstanceGroupMap);

      writeSonarQualityProfile(qualityProfile, ruleDefinitionsByGroup);

      if (qualityProfile.hasDocumentationFilename()) {
        generateDocumentationFile(qualityProfile, ruleInstanceGroupMap,unusedRuleDefinitions);
      }
    }

    return 0;
  }

  private static Map<String, RuleDefinitionGroup> processRulesToGroups(RuleDefinitionGroup allRules,
                                                                       List<ProcessingGroup> processingGroups) {
    Map<String, RuleDefinitionGroup> rulesByGroup = new HashMap<>();

    for (var group : processingGroups) {
      log.info("Processing group '{}'...", group.getName());
      var ruleDefinitionGroup = RuleDefinitionGroup.fromProcessingGroup(group);
      ruleDefinitionGroup = selectRules(ruleDefinitionGroup,allRules,group.getSelectors());
      ruleDefinitionGroup = filterRules(ruleDefinitionGroup,group.getFilters());
      logRules(ruleDefinitionGroup);

      rulesByGroup.put(group.getName(), ruleDefinitionGroup);
    }

    return rulesByGroup;
  }

  private static int dumpRulesInMultipleGroups(RuleDefinitionGroup allRules, Map<String,
    RuleDefinitionGroup> rulesByGroup) {
    Map<RuleDefinition, List<String>> ruleGroupMap = new HashMap<>();

    // Initialize map
    for (var rule : allRules.getRules()) {
      ruleGroupMap.put(rule, new LinkedList<>());
    }

    // Fill the list of groups for each rule
    for (var entry : rulesByGroup.entrySet()) {
      for (var rule : entry.getValue().getRules()) {
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

  private static Map<String,RuleInstanceGroup> createInstanceGroups(QualityProfile qualityProfile,
                                                             Map<String, RuleDefinitionGroup> ruleDefinitionsByGroup) {
    Map<String, RuleInstanceGroup> ruleInstanceGroupMap = new HashMap<>();
    for (var group : qualityProfile.groups()) {
      if (!ruleDefinitionsByGroup.containsKey(group.name())) {
        log.atError().log("Quality profile references definition group '{}', but this group does not exist",
          group.name());
        break;
      }

      ruleInstanceGroupMap.put(group.name(),
        RuleInstanceGroup.fromDefinitionGroup(ruleDefinitionsByGroup.get(group.name())));
    }

    return ruleInstanceGroupMap;
  }

  private static Map<String,RuleInstanceGroup> filterRules(QualityProfile qualityProfile,
                                                           Map<String, RuleInstanceGroup> rulesByGroup) {
    Map<String, RuleInstanceGroup> filteredGroups = new HashMap<>();
    log.info("Filtering rules...");

    for (var group : qualityProfile.groups()) {
      filteredGroups.put(group.name(),
        filterRules(rulesByGroup.get(group.name()),
          group.filters()));
    }
    log.info("Filtering rules done.");

    return filteredGroups;
  }

  private static void modifyRules(QualityProfile qualityProfile, Map<String, RuleInstanceGroup> rulesByGroup) {
    log.info("Modifying rules...");
    // TODO: Return new, changed map
    modifyRules(qualityProfile.groups(), rulesByGroup);
    log.info("Modifying rules done.");
  }

  private static void writeSonarQualityProfile(QualityProfile qualityProfile,
                                               Map<String, RuleDefinitionGroup> rulesByGroup)
    throws FileNotFoundException, XMLStreamException {
    log.info("Writing quality profile...");

    var writer = new QualityProfileGenerator();

    writer.write(qualityProfile, rulesByGroup);
    log.info("Writing quality profile...done");
  }

  private static void generateDocumentationFile(QualityProfile qualityProfile,
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

  private static RuleDefinitionGroup selectRules(RuleDefinitionGroup processingGroup,
                                                 RuleDefinitionGroup allRules,
                                                 List<Selector> selectors) {
    Set<RuleDefinition> allSelectedRules = new HashSet<>();

    for (var selector : selectors) {
      Set<RuleDefinition> selectedRules = new HashSet<>();
      log.info("Selector: {} {}",
        selector.getDescription(),
        selector.getReasonString("- "));
      for (var rule : allRules.getRules()) {
        boolean selected = selector.select(rule);

        if (selected) {
          selectedRules.add(rule);
        }
      }

      allSelectedRules.addAll(selectedRules);
      log.info("{} rules selected => {} rules over all", selectedRules.size(), allSelectedRules.size());
    }

    return processingGroup.addOrUpdate(allSelectedRules);
  }

  private static RuleDefinitionGroup filterRules(RuleDefinitionGroup groupRuleSet, List<Filter> filters) {
    var modifiedRulesGroup = groupRuleSet;

    for (var filter : filters) {
      List<RuleDefinition> filteredRules = new LinkedList<>();

      log.info("Filter: {} {}",
        filter.getDescription(),
        filter.getReasonString("- "));
      for (var rule : groupRuleSet.getRules()) {
        boolean filtered = filter.filter(rule);
        if (filtered) {
          filteredRules.add(rule);
        }
      }

      modifiedRulesGroup = modifiedRulesGroup.filter(filteredRules);

      log.info("{} filtered => {} rules over all",
        filteredRules.size(),
        modifiedRulesGroup.size());
    }

    return modifiedRulesGroup;
  }

  private static void logRules(RuleDefinitionGroup ruleDefinitionGroup) {
    for (var rule : ruleDefinitionGroup.getRules()) {
      log.atInfo().log(" * {} {} {} {} ({})",
        rule.getKey(),
        rule.getSeverity(),
        rule.getType(),
        rule.getName(),
        String.join(", ", rule.getSysTags()));
    }
  }

  private static RuleInstanceGroup filterRules(RuleInstanceGroup groupRuleSet, List<Filter> filters) {
    var modifiedRulesGroup = groupRuleSet;

    for (var filter : filters) {
      List<RuleInstance> filteredRules = new LinkedList<>();

      log.info("Filter: {} {}",
        filter.getDescription(),
        filter.getReasonString("- "));
      for (var rule : groupRuleSet.getRuleInstances()) {
        boolean filtered = filter.filter(rule);
        if (filtered) {
          filteredRules.add(rule);
        }
      }

      modifiedRulesGroup = modifiedRulesGroup.filter(filteredRules);

      log.info("{} filtered => {} rules over all",
        filteredRules.size(),
        modifiedRulesGroup.size());
    }

    return modifiedRulesGroup;
  }

  private static void modifyRules(Collection<QualityGroup> groups, Map<String, RuleInstanceGroup> groupRulesetMap) {
    for (var group : groups) {
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
  private static RuleInstanceGroup modifyRules(RuleInstanceGroup rules, Collection<Modifier> modifiers) {
    RuleInstanceGroup modifiedRules = rules;

    for (var modifier : modifiers) {
      List<RuleInstance> updatedDefinitions = new LinkedList<>();
      log.info("Modifier: {} {}",
        modifier.getDescription(),
        modifier.getReasonString("- "));
      for (var rule : modifiedRules.getRuleInstances()) {
        var instance = modifier.modify(rule);
        if (instance != null) {
          updatedDefinitions.add(instance);
        }
      }

      log.info("{} of {} rules modified",
        updatedDefinitions.size(),
        modifiedRules.size());

      modifiedRules = modifiedRules.update(updatedDefinitions);
    }

    return modifiedRules;
  }
}
