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
package com.framstag.acousticrules.qualityprofile;

import jakarta.json.bind.annotation.JsonbCreator;
import jakarta.json.bind.annotation.JsonbProperty;

import java.nio.file.Path;
import java.util.Collections;
import java.util.List;

public record QualityProfile(String name,
                             Path outputFilename,
                             Path documentationFilename,
                             List<QualityGroup> groups) {
  @JsonbCreator
  public QualityProfile(@JsonbProperty("name") String name,
                        @JsonbProperty("output_filename") Path outputFilename,
                        @JsonbProperty("documentation_filename") Path documentationFilename,
                        @JsonbProperty("groups") List<QualityGroup> groups) {
    this.name = name;
    this.outputFilename = outputFilename;
    this.documentationFilename = documentationFilename;
    this.groups = List.copyOf(groups);
  }

  public QualityProfile withOutputFilename(Path outputFilename) {
    return new QualityProfile(name,
      outputFilename,
      documentationFilename,
      groups);
  }

  public QualityProfile withDocumentationFilename(Path documentationFilename) {
    return new QualityProfile(name,
      outputFilename,
      documentationFilename,
      groups);
  }

  public boolean hasDocumentationFilename() {
    return documentationFilename != null;
  }

  @Override
  public List<QualityGroup> groups() {
    return Collections.unmodifiableList(groups);
  }
}
