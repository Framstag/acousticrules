package com.framstag.acousticrules.qualityprofile;

import com.framstag.acousticrules.filter.Filter;
import com.framstag.acousticrules.modifier.Modifier;

import java.util.List;

public class QualityGroup {
  private String name;

  private List<Filter> filters;

  private List<Modifier> modifier;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public List<Filter> getFilters() {
    return filters;
  }

  public void setFilters(List<Filter> filters) {
    this.filters = filters;
  }

  public List<Modifier> getModifier() {
    return modifier;
  }

  public void setModifier(List<Modifier> modifier) {
    this.modifier = modifier;
  }
}
