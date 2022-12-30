package com.framstag.acousticrules.selector;

import com.framstag.acousticrules.rules.Rule;
import jakarta.json.bind.annotation.JsonbSubtype;
import jakarta.json.bind.annotation.JsonbTypeInfo;

@JsonbTypeInfo(key = "@processor", value = {
  @JsonbSubtype(alias = "SelectWithKey", type = SelectWithKey.class),
  @JsonbSubtype(alias = "SelectWithTag", type = SelectWithTag.class),
  @JsonbSubtype(alias = "SelectWithType", type = SelectWithType.class)
})
public abstract class Selector {
  private String reason;

  public String getReason() {
    return reason;
  }

  public void setReason(String reason) {
    this.reason = reason;
  }

  abstract public String getDescription();

  abstract public boolean select(Rule rule);
}
