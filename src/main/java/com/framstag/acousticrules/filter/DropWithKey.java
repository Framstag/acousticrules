package com.framstag.acousticrules.filter;

import com.framstag.acousticrules.rules.Rule;

import java.util.Set;
import java.util.stream.Collectors;

public class DropWithKey extends Filter {
  private Set<String> keys;

  public Set<String> getKeys() {
    return keys;
  }

  public void setKeys(Set<String> keys) {
    this.keys = keys;
  }

  @Override
  public boolean filter(Rule rule) {
    if (keys.contains(rule.getKey())) {
      return true;
    }

    return false;
  }

  @Override
  public String getDescription() {
    return "Removing rules with keys(s) " +
      keys.stream().map(tag -> "'"+tag+"'").collect(Collectors.joining(", "));
  }
}
