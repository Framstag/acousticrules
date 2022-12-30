package com.framstag.acousticrules.qualityprofile;

import jakarta.json.bind.annotation.JsonbProperty;

import java.nio.file.Path;
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
    return groups;
  }

  public void setGroups(List<QualityGroup> groups) {
    this.groups = groups;
  }
}
