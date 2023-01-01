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
package com.framstag.acousticrules.qualityprofile;

import jakarta.json.bind.annotation.JsonbProperty;

import java.nio.file.Path;
import java.util.Collections;
import java.util.List;

public class QualityProfile {
  private String language;

  private String name;
  @JsonbProperty("output_filename")
  private Path outputFilename;

  @JsonbProperty("documentation_filename")
  private Path documentationFilename;
  private List<QualityGroup> groups;

  public String getLanguage() {
    return language;
  }

  public void setLanguage(String language) {
    this.language = language;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Path getDocumentationFilename() {
    return documentationFilename;
  }

  public void setDocumentationFilename(Path documentationFilename) {
    this.documentationFilename = documentationFilename;
  }

  public boolean hasDocumentationFilename() {
    return documentationFilename != null;
  }

  public Path getOutputFilename() {
    return outputFilename;
  }

  public void setOutputFilename(Path outputFilename) {
    this.outputFilename = outputFilename;
  }

  public List<QualityGroup> getGroups() {
    return Collections.unmodifiableList(groups);
  }

  public void setGroups(List<QualityGroup> groups) {
    this.groups = List.copyOf(groups);
  }
}
