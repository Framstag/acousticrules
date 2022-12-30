package com.framstag.acousticrules.modifier;

import com.framstag.acousticrules.rules.Rule;
import com.framstag.acousticrules.rules.Severity;

public class ChangeSeverity extends Modifier {
  private Severity from;
  private Severity to;

  public Severity getFrom() {
    return from;
  }

  public void setFrom(Severity from) {
    this.from = from;
  }

  public Severity getTo() {
    return to;
  }

  public void setTo(Severity to) {
    this.to = to;
  }

  @Override
  public boolean modify(Rule rule) {
    if (rule.getSeverity()==from) {
      rule.setSeverity(to);
      return true;
    }

    return false;
  }

  @Override
  public String getDescription() {
    return "Setting severity from " + from + " to "+ to;
  }
}
