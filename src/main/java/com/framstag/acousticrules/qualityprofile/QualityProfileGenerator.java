/*
 * AcousticRuler
 * Copyright 2023 Tim Teulings
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.framstag.acousticrules.qualityprofile;

import com.framstag.acousticrules.rules.instance.RuleInstance;
import com.framstag.acousticrules.rules.instance.RuleInstanceGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class QualityProfileGenerator {
  private static final Logger log = LoggerFactory.getLogger(QualityProfileGenerator.class);
  private static final int INDENT = 2;

  public void write(QualityProfile qualityProfile,
                    String language,
                    Map<String, RuleInstanceGroup> rulesByGroup) throws IOException {
    var outputFactory = XMLOutputFactory.newDefaultFactory();

    try(var fileOutputStream = new FileOutputStream(qualityProfile.outputFilename().toFile())) {

      var writer = outputFactory.createXMLStreamWriter(fileOutputStream, StandardCharsets.UTF_8.name());

      writer.writeStartDocument(StandardCharsets.UTF_8.name(), "1.0");
      writeLn(writer);

      writer.writeStartElement("profile");
      writeLn(writer);

      writeIndent(writer, INDENT);
      writer.writeStartElement("name");
      writer.writeCharacters(qualityProfile.name());
      writer.writeEndElement();
      writeLn(writer);

      writeIndent(writer, INDENT);
      writer.writeStartElement("language");
      writer.writeCharacters(language);
      writer.writeEndElement();
      writeLn(writer);
      writeLn(writer);

      writeIndent(writer, INDENT);
      writer.writeStartElement("rules");
      writeLn(writer);

      for (QualityGroup group : qualityProfile.groups()) {
        if (!rulesByGroup.containsKey(group.name())) {
          log.atError().log("Quality profile requests dump of group '{}', but this group does not exist", group.name());
          break;
        }

        log.atInfo().log("Writing group '{}'...", group.name());

        writeIndent(writer, INDENT + INDENT);
        writer.writeComment(" Group " + group.name() + " ");
        writeLn(writer);
        writeLn(writer);

        RuleInstanceGroup groupRules = rulesByGroup.get(group.name());

        for (RuleInstance rule : groupRules.getRuleInstances()) {
          if (!rule.isDisabled()) {
            writeRule(writer, rule, INDENT + INDENT);
            // TODO: Not on the last rule
            writeLn(writer);
          }
        }
      }

      writeIndent(writer, INDENT);
      writer.writeEndElement();
      writeLn(writer);

      writer.writeEndDocument();
    } catch (FileNotFoundException | XMLStreamException e) {
      throw new IOException("Error while writing quality profile", e);
    }
  }

  private static void writeLn(XMLStreamWriter writer) throws XMLStreamException {
    writer.writeCharacters("\n");
  }

  private static void writeIndent(XMLStreamWriter writer, int spaceCount) throws XMLStreamException {
    for (var count = 1; count <= spaceCount; count++) {
      writer.writeCharacters(" ");
    }
  }

  private static void writeRule(XMLStreamWriter writer, RuleInstance rule, int indent) throws XMLStreamException {
    writeIndent(writer, indent);
    writer.writeStartElement("rule");
    writeLn(writer);

    writeIndent(writer, indent + INDENT);
    writer.writeStartElement("repositoryKey");
    writer.writeCharacters(rule.getRepo());
    writer.writeEndElement();
    writeLn(writer);

    writeIndent(writer, indent + INDENT);
    writer.writeStartElement("key");
    // TODO: Improve the hack!
    writer.writeCharacters(rule.getKey().substring(rule.getKey().indexOf(":") + 1));
    writer.writeEndElement();
    writeLn(writer);

    writeIndent(writer, indent + INDENT);
    writer.writeStartElement("priority");
    writer.writeCharacters(rule.getSeverity().name());
    writer.writeEndElement();
    writeLn(writer);

    writeParameterList(writer, rule, indent + INDENT);

    writeIndent(writer, indent);
    writer.writeEndElement();
    writeLn(writer);
  }

  private static void writeParameterList(XMLStreamWriter writer,
                                         RuleInstance rule,
                                         int indent) throws XMLStreamException {
    writeIndent(writer, indent);

    if (rule.hasParameter()) {
      writer.writeStartElement("parameters");
      writeLn(writer);

      for (var parameter : rule.getParameter().entrySet()) {
        writeParameter(writer, indent + INDENT, parameter.getKey(), parameter.getValue());
      }

      writeIndent(writer, indent);
      writer.writeEndElement();
      writeLn(writer);
    } else {
      writer.writeEmptyElement("parameters");
      writeLn(writer);
    }
  }

  private static void writeParameter(XMLStreamWriter writer,
                                     int indent,
                                     String key,
                                     String value) throws XMLStreamException {
    writeIndent(writer, indent);
    writer.writeStartElement("parameter");
    writeLn(writer);

    writeIndent(writer, indent + INDENT);
    writer.writeStartElement("key");
    writer.writeCharacters(key);
    writer.writeEndElement();
    writeLn(writer);

    if (value != null) {
      writeIndent(writer, indent + INDENT);
      writer.writeStartElement("value");
      writer.writeCharacters(value);
      writer.writeEndElement();
      writeLn(writer);
    } else {
      writeIndent(writer, indent + INDENT);
      writer.writeEmptyElement("value");
      writeLn(writer);
    }

    writeIndent(writer, indent);
    writer.writeEndElement();
    writeLn(writer);
  }
}
