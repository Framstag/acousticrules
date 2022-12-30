package com.framstag.acousticrules.rules;

import com.framstag.acousticrules.rules.Rule;

import java.util.List;

public class RuleSet {

  private List<Rule> rules;

  public List<Rule> getRules() {
    return rules;
  }

  public boolean hasRules() {
    return rules != null && !rules.isEmpty();
  }

  public int getRuleCount() {
    if (rules == null) {
      return 0;
    }

    return rules.size();
  }

  public void setRules(List<Rule> rules) {
    this.rules = rules;
  }
}
