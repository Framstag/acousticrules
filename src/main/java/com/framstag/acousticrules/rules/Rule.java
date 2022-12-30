package com.framstag.acousticrules.rules;

import java.util.List;

public class Rule {

  private String key;
  private String name;
  private String repo;
  private Severity severity;
  private Status status;
  private String lang;
  private String type;

  private List<String> sysTags;

  private List<Parameter> params;

  public String getKey() {
    return key;
  }

  public void setKey(String key) {
    this.key = key;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getRepo() {
    return repo;
  }

  public void setRepo(String repo) {
    this.repo = repo;
  }

  public Severity getSeverity() {
    return severity;
  }

  public void setSeverity(Severity severity) {
    this.severity = severity;
  }

  public Status getStatus() {
    return status;
  }

  public void setStatus(Status status) {
    this.status = status;
  }

  public String getLang() {
    return lang;
  }

  public void setLang(String lang) {
    this.lang = lang;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public List<String> getSysTags() {
    return sysTags;
  }

  public void setSysTags(List<String> sysTags) {
    this.sysTags = sysTags;
  }

  public List<Parameter> getParams() {
    return params;
  }

  public void setParams(List<Parameter> params) {
    this.params = params;
  }

  public boolean hasParams() {
    return params != null && !params.isEmpty();
  }

  public void setParam(String key, String value) {
    // TODO: Errorhandling
    if (!hasParams()) {
      return;
    }

    for (Parameter param : params) {
      if (param.getKey().equals(key)) {
        param.setValue(value);

        return;
      }
    }
  }
}
