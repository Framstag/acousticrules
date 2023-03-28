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
package com.framstag.acousticrules.rulestogroup.adapter.processinggroup;

import com.framstag.acousticrules.exception.ParameterException;
import com.framstag.acousticrules.rulestogroup.ProcessingGroup;
import jakarta.json.bind.JsonbBuilder;
import jakarta.json.bind.JsonbConfig;
import org.eclipse.yasson.YassonConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;

public class ProcessingGroupRepository {
  private static final Logger log = LoggerFactory.getLogger(ProcessingGroupRepository.class);

  public List<ProcessingGroup> loadProcessingGroups(Iterable<Path> processorSetFiles) throws ParameterException {
    List<ProcessingGroup> processingGroups = new LinkedList<>();

    for (var filename : processorSetFiles) {
      processingGroups.add(load(filename));
    }

    return processingGroups;
  }

  public ProcessingGroup load(Path filename) throws ParameterException {
    log.info("Loading processing group '{}'", filename);
    var jsonbConfig = new JsonbConfig();
    jsonbConfig.setProperty(YassonConfig.FAIL_ON_UNKNOWN_PROPERTIES, Boolean.TRUE);

    try (var jsonb = JsonbBuilder.create(jsonbConfig)) {
      var configFileContent = Files.readString(filename);
      return jsonb.fromJson(configFileContent, ProcessingGroup.class);
    } catch (Exception e) {
      throw new ParameterException("Cannot load processing group", e);
    }
  }

}
