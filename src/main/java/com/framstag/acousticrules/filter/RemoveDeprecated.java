package com.framstag.acousticrules.filter;

import com.framstag.acousticrules.rules.Rule;
import com.framstag.acousticrules.rules.Status;

public class RemoveDeprecated extends Filter {
  @Override
  public boolean filter(Rule rule) {
    if (rule.getStatus()== Status.DEPRECATED) {
      return true;
    }

    return false;
  }

  @Override
  public String getDescription() {
    return "Removing deprecated rules";
  }
}
