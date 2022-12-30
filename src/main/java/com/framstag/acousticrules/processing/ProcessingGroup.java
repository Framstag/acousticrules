package com.framstag.acousticrules.processing;

import com.framstag.acousticrules.filter.Filter;
import com.framstag.acousticrules.modifier.Modifier;
import com.framstag.acousticrules.selector.Selector;

import java.util.List;

public class ProcessingGroup {
  private String name;
  private List<Selector> selectors;
  private List<Filter> filters;

  private List<Modifier> modifier;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public List<Selector> getSelectors() {
    return selectors;
  }

  public void setSelectors(List<Selector> selectors) {
    this.selectors = selectors;
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
