package com.framstag.acousticrules.modifier;

import com.framstag.acousticrules.rules.Rule;
import jakarta.json.bind.annotation.JsonbSubtype;
import jakarta.json.bind.annotation.JsonbTypeInfo;

@JsonbTypeInfo(key = "@processor", value = {
  @JsonbSubtype(alias = "ChangeSeverity", type = ChangeSeverity.class),
  @JsonbSubtype(alias = "SetSeverity", type = SetSeverity.class),
  @JsonbSubtype(alias = "SetParamForKey", type = SetParamForKey.class)
})
public abstract class Modifier {
  private String reason;

  public String getReason() {
    return reason;
  }

  public void setReason(String reason) {
    this.reason = reason;
  }

  abstract public String getDescription();

  abstract public boolean modify(Rule rule);
}
