package com.framstag.acousticrules.qualityprofile;

import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.json.bind.JsonbConfig;
import org.eclipse.yasson.YassonConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Files;
import java.nio.file.Path;

public class QualityProfileLoader {
  private static final Logger logger = LoggerFactory.getLogger(QualityProfileLoader.class);

  public QualityProfile load(Path filename) {
    JsonbConfig jsonbConfig = new JsonbConfig();
    jsonbConfig.setProperty(YassonConfig.FAIL_ON_UNKNOWN_PROPERTIES,Boolean.TRUE);

    try (Jsonb jsonb = JsonbBuilder.create(jsonbConfig)) {

      String configFileContent = Files.readString(filename);
      return jsonb.fromJson(configFileContent, QualityProfile.class);
    } catch (Exception e) {
      logger.error("Cannot read quality profile definition",e);
      return null;
    }
  }
}
