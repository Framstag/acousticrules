package com.framstag.acousticrules.selector;

import com.framstag.acousticrules.rules.Rule;

import java.util.Set;
import java.util.stream.Collectors;

public class SelectWithTag extends Selector {
  private Set<String> tags;

  public Set<String> getTags() {
    return tags;
  }

  public void setTags(Set<String> tags) {
    this.tags = tags;
  }

  @Override
  public boolean select(Rule rule) {
    for (String tag : rule.getSysTags()) {
      if (tags.contains(tag)) {
        return true;
      }
    }

    return false;
  }

  @Override
  public String getDescription() {
    return "Selecting rules with tag(s) " +
      tags.stream().map(tag -> "'"+tag+"'").collect(Collectors.joining(", "));
  }
}
