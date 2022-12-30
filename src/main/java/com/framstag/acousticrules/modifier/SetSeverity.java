package com.framstag.acousticrules.modifier;

import com.framstag.acousticrules.rules.Rule;
import com.framstag.acousticrules.rules.Severity;

import java.util.Set;
import java.util.stream.Collectors;

public class SetSeverity extends Modifier {
  private Set<String> keys;
  private Severity to;

  public Set<String> getKeys() {
    return keys;
  }

  public void setKeys(Set<String> keys) {
    this.keys = keys;
  }

  public Severity getTo() {
    return to;
  }

  public void setTo(Severity to) {
    this.to = to;
  }

  @Override
  public boolean modify(Rule rule) {
    if (keys.contains(rule.getKey())) {
      rule.setSeverity(to);
      return true;
    }

    return false;
  }

  @Override
  public String getDescription() {
    return "Setting severity for key(s) " +
      keys.stream().map(tag -> "'"+tag+"'").collect(Collectors.joining(", ")) +
      " to "+ to;
  }
}
