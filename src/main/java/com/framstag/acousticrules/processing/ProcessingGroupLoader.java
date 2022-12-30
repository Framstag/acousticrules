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
package com.framstag.acousticrules.processing;

import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.json.bind.JsonbConfig;
import org.eclipse.yasson.YassonConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Files;
import java.nio.file.Path;

public class ProcessingGroupLoader {
  private static final Logger logger = LoggerFactory.getLogger(ProcessingGroupLoader.class);

  public ProcessingGroup load(Path filename) {
    JsonbConfig jsonbConfig = new JsonbConfig();
    jsonbConfig.setProperty(YassonConfig.FAIL_ON_UNKNOWN_PROPERTIES,Boolean.TRUE);

    try (Jsonb jsonb = JsonbBuilder.create(jsonbConfig)) {

      String configFileContent = Files.readString(filename);
      return jsonb.fromJson(configFileContent, ProcessingGroup.class);
    } catch (Exception e) {
      logger.error("Cannot read processing instructions",e);
      return null;
    }
  }
}
