package com.framstag.acousticrules.modifier;

import com.framstag.acousticrules.rules.Rule;

public class SetParamForKey extends Modifier {
  private String key;

  private String param;

  private String value;

  public String getKey() {
    return key;
  }

  public void setKey(String key) {
    this.key = key;
  }

  public String getParam() {
    return param;
  }

  public void setParam(String param) {
    this.param = param;
  }

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }

  @Override
  public boolean modify(Rule rule) {
    if (key.equals(rule.getKey())) {
      rule.setParam(param,value);
      return true;
    }

    return false;
  }

  @Override
  public String getDescription() {
    return "Setting param "+param + " for key " + key + " to value '" + value + "'";
  }
}
