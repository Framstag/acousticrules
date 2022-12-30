package com.framstag.acousticrules.selector;

import com.framstag.acousticrules.rules.Rule;

import java.util.Set;
import java.util.stream.Collectors;

public class SelectWithKey extends Selector {
  private Set<String> keys;

  public Set<String> getKeys() {
    return keys;
  }

  public void setKeys(Set<String> keys) {
    this.keys = keys;
  }

  @Override
  public boolean select(Rule rule) {
    return keys.contains(rule.getKey());
  }

  @Override
  public String getDescription() {
    return "Select rules with keys(s) " +
      keys.stream().map(key -> "'"+key+"'").collect(Collectors.joining(", "));
  }

}
