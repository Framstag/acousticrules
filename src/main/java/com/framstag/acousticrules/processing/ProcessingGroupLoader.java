package com.framstag.acousticrules.processing;

import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.json.bind.JsonbConfig;
import org.eclipse.yasson.YassonConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Files;
import java.nio.file.Path;

public class ProcessingGroupLoader {
  private static final Logger logger = LoggerFactory.getLogger(ProcessingGroupLoader.class);

  public ProcessingGroup load(Path filename) {
    JsonbConfig jsonbConfig = new JsonbConfig();
    jsonbConfig.setProperty(YassonConfig.FAIL_ON_UNKNOWN_PROPERTIES,Boolean.TRUE);

    try (Jsonb jsonb = JsonbBuilder.create(jsonbConfig)) {

      String configFileContent = Files.readString(filename);
      return jsonb.fromJson(configFileContent, ProcessingGroup.class);
    } catch (Exception e) {
      logger.error("Cannot read processing instructions",e);
      return null;
    }
  }
}
