package com.framstag.acousticrules.qualityprofile;

import com.framstag.acousticrules.rules.Parameter;
import com.framstag.acousticrules.rules.Rule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Set;

public class QualityProfileGenerator {

  private static final Logger logger = LoggerFactory.getLogger(QualityProfileGenerator.class);

  private void writeIndent(XMLStreamWriter writer, int spaceCount) throws XMLStreamException {
    for (int count=1; count<=spaceCount; count++) {
      writer.writeCharacters(" ");
    }
  }

  private void writeRule(XMLStreamWriter writer, Rule rule, int indent) throws XMLStreamException {
    writeIndent(writer,indent);
    writer.writeStartElement("rule");
    writeLn(writer);

    writeIndent(writer,indent+2);
    writer.writeStartElement("repositoryKey");
    writer.writeCharacters(rule.getRepo());
    writer.writeEndElement();
    writeLn(writer);

    writeIndent(writer,indent+2);
    writer.writeStartElement("key");
    // TODO: Improve the hack!
    writer.writeCharacters(rule.getKey().substring(rule.getKey().indexOf(":")+1));
    writer.writeEndElement();
    writeLn(writer);

    writeIndent(writer,indent+2);
    writer.writeStartElement("priority");
    writer.writeCharacters(rule.getSeverity().name());
    writer.writeEndElement();
    writeLn(writer);

    writeParameterList(writer, rule, indent+2);

    writeIndent(writer,indent);
    writer.writeEndElement();
    writeLn(writer);
  }

  private void writeParameterList(XMLStreamWriter writer, Rule rule, int indent) throws XMLStreamException {
    writeIndent(writer,indent);

    if (rule.hasParams()) {
      writer.writeStartElement("parameters");
      writeLn(writer);

      for (Parameter parameter : rule.getParams()) {
        writeParameter(writer, indent +2, parameter);
      }

      writeIndent(writer,indent);
      writer.writeEndElement();
      writeLn(writer);
    }
    else {
      writer.writeEmptyElement("parameters");
      writeLn(writer);
    }
  }

  private void writeParameter(XMLStreamWriter writer, int indent, Parameter parameter) throws XMLStreamException {
    if (!parameter.hasOverwrittenDefaultValue()) {
      return;
    }

    writeIndent(writer, indent);
    writer.writeStartElement("parameter");
    writeLn(writer);

    writeIndent(writer, indent+2);
    writer.writeStartElement("key");
    writer.writeCharacters(parameter.getKey());
    writer.writeEndElement();
    writeLn(writer);

    if (parameter.getValue()!=null) {
      writeIndent(writer, indent + 2);
      writer.writeStartElement("value");
      writer.writeCharacters(parameter.getValue());
      writer.writeEndElement();
      writeLn(writer);
    }
    else {
      writeIndent(writer, indent + 2);
      writer.writeEmptyElement("value");
      writeLn(writer);
    }

    writeIndent(writer, indent);
    writer.writeEndElement();
    writeLn(writer);
  }

  private void writeLn(XMLStreamWriter writer) throws XMLStreamException {
    writer.writeCharacters("\n");
  }

  public void write(QualityProfile qualityProfile, Map<String, Set<Rule>> rulesByGroup) throws FileNotFoundException, XMLStreamException {
    XMLOutputFactory outputFactory = XMLOutputFactory.newDefaultFactory();

    XMLStreamWriter writer = outputFactory.createXMLStreamWriter(new FileOutputStream(qualityProfile.getOutputFilename().toFile()),
      StandardCharsets.UTF_8.name());

    writer.writeStartDocument(StandardCharsets.UTF_8.name(),"1.0");
    writeLn(writer);

    writer.writeStartElement("profile");
    writeLn(writer);

    writeIndent(writer,2);
    writer.writeStartElement("name");
    writer.writeCharacters(qualityProfile.getName());
    writer.writeEndElement();
    writeLn(writer);

    writeIndent(writer,2);
    writer.writeStartElement("language");
    writer.writeCharacters(qualityProfile.getLanguage());
    writer.writeEndElement();
    writeLn(writer);
    writeLn(writer);

    writeIndent(writer,2);
    writer.writeStartElement("rules");
    writeLn(writer);

    for (QualityGroup group : qualityProfile.getGroups()) {
      if (!rulesByGroup.containsKey(group.getName())) {
        logger.error("Quality profile requests dump of group '{}', but this group does not exist", group.getName());
        break;
      }

      logger.info("Writing group '{}'...", group.getName());

      writeIndent(writer,4);
      writer.writeComment(" Group " + group.getName()+" ");
      writeLn(writer);
      writeLn(writer);

      Set<Rule> groupRules=rulesByGroup.get(group.getName());

      for (Rule rule : groupRules) {
        writeRule(writer, rule, 4);
        writeLn(writer); // TODO: Not on the last rule
      }
    }

    writeIndent(writer,2);
    writer.writeEndElement();
    writeLn(writer);

    writer.writeEndDocument();
  }
}
