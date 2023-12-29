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
package com.framstag.acousticrules.rules.definition;

import com.framstag.acousticrules.exception.ParameterException;
import jakarta.json.bind.JsonbBuilder;
import jakarta.json.bind.JsonbConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;

/**
 * A Repository for loading RuleDefinitions from files.
 */
public class RulesRepository {
  private static final Logger log = LoggerFactory.getLogger(RulesRepository.class);

  private static RulesDownloadFile loadRulesDownloadFile(Path filename) throws ParameterException {
    var jsonbConfig = new JsonbConfig();

    log.info("Loading rules download file '{}'", filename);
    try (var jsonb = JsonbBuilder.create(jsonbConfig)) {
      var configFileContent = Files.readString(filename);
      var ruleSet = jsonb.fromJson(configFileContent, RulesDownloadFile.class);
      log.info("{} rule(s) loaded.", ruleSet.getRuleCount());
      return ruleSet;
    } catch (Exception e) {
      throw new ParameterException("Cannot load rules '%1$s'".formatted(filename), e);
    }
  }

  public RuleDefinitionList loadRulesFromRulesDownloadFiles(Iterable<Path> arguments) throws ParameterException {
    List<RulesDownloadFile> files = new LinkedList<>();

    for (var ruleSetFilename : arguments) {
      files.add(loadRulesDownloadFile(ruleSetFilename));
    }

    var definitionList =  RuleDefinitionList.fromRuleDownloadFiles(files);

    log.info("{} rules over all files loaded.", definitionList.size());

    return definitionList;
  }
}
