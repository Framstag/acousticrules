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
package com.framstag.acousticrules.qualityprofile.adapter.qualityprofile;

import com.framstag.acousticrules.exception.ParameterException;
import com.framstag.acousticrules.qualityprofile.QualityProfile;
import jakarta.json.bind.JsonbBuilder;
import jakarta.json.bind.JsonbConfig;
import org.eclipse.yasson.YassonConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Files;
import java.nio.file.Path;

public class QualityProfileRepository {
  private static final Logger log = LoggerFactory.getLogger(QualityProfileRepository.class);

  public QualityProfile load(Path filename) {
    var jsonbConfig = new JsonbConfig();
    jsonbConfig.setProperty(YassonConfig.FAIL_ON_UNKNOWN_PROPERTIES,Boolean.TRUE);

    log.info("Loading quality profile '{}'", filename);
    try (var jsonb = JsonbBuilder.create(jsonbConfig)) {

      var configFileContent = Files.readString(filename);
      var qualityProfile = jsonb.fromJson(configFileContent, QualityProfile.class);
      log.info("Loading quality profile done.");
      return qualityProfile;
    } catch (Exception e) {
      throw new ParameterException("Cannot load quality profile",e);
    }
  }
}
