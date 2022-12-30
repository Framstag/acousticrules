package com.framstag.acousticrules.filter;

import com.framstag.acousticrules.rules.Rule;

import java.util.Set;
import java.util.stream.Collectors;

public class DropWithTag extends Filter {
  private Set<String> tags;

  public Set<String> getTags() {
    return tags;
  }

  public void setTags(Set<String> tags) {
    this.tags = tags;
  }

  @Override
  public boolean filter(Rule rule) {
    for (String tag : rule.getSysTags()) {
      if (tags.contains(tag)) {
        return true;
      }
    }

    return false;
  }

  @Override
  public String getDescription() {
    return "Removing rules with tag(s) "+tags.stream().map(tag -> "'"+tag+"'").collect(Collectors.joining(", "));
  }
}
