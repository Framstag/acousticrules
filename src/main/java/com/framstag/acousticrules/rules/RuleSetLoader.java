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
package com.framstag.acousticrules.rules;

import com.framstag.acousticrules.exceptions.ParameterException;
import jakarta.json.bind.JsonbBuilder;
import jakarta.json.bind.JsonbConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;

public class RuleSetLoader {
  private static final Logger log = LoggerFactory.getLogger(RuleSetLoader.class);

  public List<Rule> loadRules(Iterable<Path> arguments) throws ParameterException {
    var ruleSetLoader = new RuleSetLoader();
    List<Rule> rules = new LinkedList<>();

    for (var ruleSetFilename : arguments) {
      var ruleSet = ruleSetLoader.load(ruleSetFilename);

      if (ruleSet.hasRules()) {
        rules.addAll(ruleSet.getRules());
      }
    }

    log.info("{} rules over all files loaded.", rules.size());

    return rules;
  }

  public RuleSet load(Path filename) throws ParameterException {
    var jsonbConfig = new JsonbConfig();

    log.info("Loading rule set '{}'", filename);
    try (var jsonb = JsonbBuilder.create(jsonbConfig)) {
      var configFileContent = Files.readString(filename);
      var ruleSet = jsonb.fromJson(configFileContent, RuleSet.class);
      log.info("Rule set with {} rules loaded.", ruleSet.getRuleCount());
      return ruleSet;
    } catch (Exception e) {
      throw new ParameterException("Cannot load rules", e);
    }
  }
}
