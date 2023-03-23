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

package com.framstag.acousticrules.service;

import com.framstag.acousticrules.exception.ParameterException;
import com.framstag.acousticrules.properties.Propertizer;
import com.framstag.acousticrules.qualityprofile.QualityProfile;

import java.nio.file.Path;

public class QualityProfilePropertizerService {
  public QualityProfile propertize(QualityProfile qualityProfile, Propertizer propertizer) {
    if (!propertizer.validate(qualityProfile.name())) {
      throw new ParameterException("Attribute 'name' of QualityProfile contains unknown Property");
    }

    if (!propertizer.validate(qualityProfile.outputFilename().toString())) {
      throw new ParameterException("Attribute 'outputFilename' of QualityProfile contains unknown Property");
    }

    if (!propertizer.validate(qualityProfile.documentationFilename().toString())) {
      throw new ParameterException("Attribute 'documentationFilename' of QualityProfile contains unknown Property");
    }

    qualityProfile = qualityProfile.withName(
      propertizer.resolve(qualityProfile.name()));
    qualityProfile = qualityProfile.withDocumentationFilename(
      Path.of(propertizer.resolve(qualityProfile.documentationFilename().toString())));
    qualityProfile = qualityProfile.withOutputFilename(
      Path.of(propertizer.resolve(qualityProfile.outputFilename().toString())));

    return qualityProfile;
  }
}
