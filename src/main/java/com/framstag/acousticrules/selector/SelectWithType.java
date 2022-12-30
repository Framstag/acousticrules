package com.framstag.acousticrules.selector;

import com.framstag.acousticrules.rules.Rule;

import java.util.Set;
import java.util.stream.Collectors;

public class SelectWithType extends Selector {
  private Set<String> types;

  public Set<String> getTypes() {
    return types;
  }

  public void setTypes(Set<String> types) {
    this.types = types;
  }

  @Override
  public boolean select(Rule rule) {
    return types.contains(rule.getType());
  }

  @Override
  public String getDescription() {
    return "Select rules with types(s) " +
      types.stream().map(tag -> "'"+tag+"'").collect(Collectors.joining(", "));
  }

}
