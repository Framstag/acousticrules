package com.framstag.acousticrules.rules;

import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.json.bind.JsonbConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Files;
import java.nio.file.Path;

public class RuleSetLoader {
  private static final Logger logger = LoggerFactory.getLogger(RuleSetLoader.class);

  public RuleSet load(Path filename) {
    JsonbConfig jsonbConfig = new JsonbConfig();

    try (Jsonb jsonb = JsonbBuilder.create(jsonbConfig)) {

      String configFileContent = Files.readString(filename);
      return jsonb.fromJson(configFileContent, RuleSet.class);
    } catch (Exception e) {
      logger.error("Cannot read rules",e);
      return null;
    }
  }
}
