package com.framstag.acousticrules.filter;

import com.framstag.acousticrules.rules.Rule;

import java.util.Set;
import java.util.stream.Collectors;

public class DropNotWithType extends Filter {
  private Set<String> types;

  public Set<String> getTypes() {
    return types;
  }

  public void setTypes(Set<String> types) {
    this.types = types;
  }

  @Override
  public boolean filter(Rule rule) {
    return !types.contains(rule.getType());
  }

  @Override
  public String getDescription() {
    return "Removing rules with do not have types(s) "+
      types.stream().map(type -> "'"+type+"'").collect(Collectors.joining(", "));
  }
}
