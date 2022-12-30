package com.framstag.acousticrules.filter;

import com.framstag.acousticrules.rules.Rule;
import jakarta.json.bind.annotation.JsonbSubtype;
import jakarta.json.bind.annotation.JsonbTypeInfo;

@JsonbTypeInfo(key = "@processor", value = {
  @JsonbSubtype(alias = "DropWithTag", type = DropWithTag.class),
  @JsonbSubtype(alias = "DropWithKey", type = DropWithKey.class),
  @JsonbSubtype(alias = "DropWithType", type = DropWithType.class),
  @JsonbSubtype(alias = "DropNotWithType", type = DropNotWithType.class),
  @JsonbSubtype(alias = "RemoveDeprecated", type = RemoveDeprecated.class)
})
public abstract class Filter {
  private String reason;

  public String getReason() {
    return reason;
  }

  public void setReason(String reason) {
    this.reason = reason;
  }

  abstract public String getDescription();

  abstract public boolean filter(Rule rule);
}
