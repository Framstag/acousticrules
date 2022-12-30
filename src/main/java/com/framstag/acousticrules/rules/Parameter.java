package com.framstag.acousticrules.rules;

public class Parameter {
  private String key;
  private String defaultValue;

  private String value;

  public String getKey() {
    return key;
  }

  public void setKey(String key) {
    this.key = key;
  }

  public String getDefaultValue() {
    return defaultValue;
  }

  public void setDefaultValue(String defaultValue) {
    this.defaultValue = defaultValue;
  }

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }

  public boolean hasOverwrittenDefaultValue() {
    if (defaultValue == null && value == null) {
      return false;
    }

    if (defaultValue == null || value == null) {
      return true;
    }

    return !defaultValue.equals((value));
  }
}
