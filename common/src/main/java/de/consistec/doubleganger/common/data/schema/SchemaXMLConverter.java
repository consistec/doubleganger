package de.consistec.doubleganger.common.data.schema;

/*
 * #%L
 * Project - doppelganger
 * File - SchemaXMLConverter.java
 * %%
 * Copyright (C) 2011 - 2013 consistec GmbH
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */
import static de.consistec.doubleganger.common.i18n.MessageReader.read;

import de.consistec.doubleganger.common.exception.SerializationException;
import de.consistec.doubleganger.common.i18n.Errors;
import de.consistec.doubleganger.common.i18n.Warnings;
import de.consistec.doubleganger.common.util.SQLTypesUtil;

import java.io.IOException;
import java.io.StringReader;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Sax {@link org.xml.sax.helpers.DefaultHandler handler} implementation for reading/writing
 * {@link de.consistec.doubleganger.common.data.schema.Schema} to/from its xml representation.
 *
 * @company consistec Engineering and Consulting GmbH
 * @date 26.07.12 10:02
 * @author Markus Backes
 * @since 0.0.1-SNAPSHOT
 */
public class SchemaXMLConverter extends DefaultHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(SchemaXMLConverter.class.getCanonicalName());
    private static final String TABLE_QNAME = "table";
    private static final String TABLES_QNAME = "tables";
    private static final String COLUMN_QNAME = "column";
    private static final String COLUMNS_QNAME = "columns";
    private static final String CONSTRAINT_QNAME = "constraint";
    private static final String CONSTRAINTS_QNAME = "constraints";
    private static final String NAME_QNAME = "name";
    private static final String TYPE_QNAME = "type";
    private static final String SIZE_QNAME = "size";
    private static final String NULLABLE_QNAME = "nullable";
    private static final String SCHEMA_QNAME = "schema";
    private static final String DECIMAL_DIGITS_QNAME = "decimalDigits";
    private static final String XSD_SCHEMA_FILE = "schema.xsd";
    private Column column;
    private Constraint constraint;
    private Table table;
    private Schema schema;

    /**
     * Create new instance of converter.
     */
    public SchemaXMLConverter() {
        super();
    }

    /**
     * Converts {@link de.consistec.doubleganger.common.data.schema.Schema Schema} to xml String.
     *
     * @param schema Schema to convert
     * @return XML representation of Schema
     * @throws SerializationException
     */
    public String toXML(Schema schema) throws SerializationException {

        StringBuilder builder = new StringBuilder();
        try {
            builder.append(String.format("<%s>\n", SCHEMA_QNAME));
            builder.append(String.format("\t<%s>\n", TABLES_QNAME));

            for (Table currentTable : schema.getTables()) {
                builder.append(String.format("\t\t<%s %s=\"%s\">\n", TABLE_QNAME, NAME_QNAME, currentTable.getName()));
                builder.append(String.format("\t\t\t<%s>\n", COLUMNS_QNAME));
                for (Column currentColumn : currentTable.getColumns()) {
                    builder.append(String.format("\t\t\t\t<%s %s=\"%s\" %s=\"%s\" %s=\"%s\" %s=\"%s\" %s=\"%s\"/>\n",
                        COLUMN_QNAME, NAME_QNAME, currentColumn.getName(), TYPE_QNAME,
                        SQLTypesUtil.nameOf(currentColumn.getType()), SIZE_QNAME, currentColumn.getSize(),
                        DECIMAL_DIGITS_QNAME,
                        currentColumn.getDecimalDigits(), NULLABLE_QNAME, currentColumn.isNullable()));
                }
                builder.append(String.format("\t\t\t</%s>\n", COLUMNS_QNAME));
                builder.append(String.format("\t\t\t<%s>\n", CONSTRAINTS_QNAME));
                for (Constraint currentConstraint : currentTable.getConstraints()) {
                    builder.append(String.format("\t\t\t\t<%s %s=\"%s\" %s=\"%s\" %s=\"%s\"/>\n", CONSTRAINT_QNAME,
                        NAME_QNAME,
                        currentConstraint.getName(), TYPE_QNAME, currentConstraint.getType().name(), COLUMN_QNAME,
                        currentConstraint.getColumn()));
                }
                builder.append(String.format("\t\t\t</%s>\n", CONSTRAINTS_QNAME));
                builder.append(String.format("\t\t</%s>\n", TABLE_QNAME));
            }

            builder.append(String.format("\t</%s>\n", TABLES_QNAME));
            builder.append(String.format("</%s>\n", SCHEMA_QNAME));

        } catch (IllegalAccessException e) {
            throw new SerializationException(read(Errors.COMMON_CANT_CONVERT_DB_SCHEMA_TO_XML), e);
        }
        String schemaXml = builder.toString();
        validateSchema(schemaXml);
        return schemaXml;

    }

    private void validateSchema(String xml) throws SerializationException {
        try {
            // parse an XML document into a DOM tree
            DocumentBuilderFactory parserFactory = DocumentBuilderFactory.newInstance();
            parserFactory.setNamespaceAware(true);
            DocumentBuilder parser = parserFactory.newDocumentBuilder();
            Document document = parser.parse(new InputSource(new StringReader(xml)));
            SchemaFactory factory;

            try {
                // create a SchemaFactory capable of understanding WXS schemas
                factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            } catch (IllegalArgumentException e) {
                LOGGER.warn(read(Warnings.COMMON_CANT_CREATE_XML_SCHEMA_FACTORY), e);
                return;
            }

            // load a WXS schema, represented by a Schema instance
            Source schemaFile = new StreamSource(
                Thread.currentThread().getContextClassLoader().getResourceAsStream(XSD_SCHEMA_FILE));
            javax.xml.validation.Schema validationSchema = factory.newSchema(schemaFile);

            // create a Validator instance, which can be used to validate an instance document
            Validator validator = validationSchema.newValidator();

            // validate the DOM tree
            validator.validate(new DOMSource(document));
        } catch (ParserConfigurationException e) {
            throw new SerializationException(read(Errors.COMMON_CANT_CREATE_XML_PARSER), e);
        } catch (SAXException e) {
            throw new SerializationException(read(Errors.COMMON_XML_VALIDATION_FAILED), e);
        } catch (IOException e) {
            throw new SerializationException(e);
        }
    }

    /**
     * Converts xml {@link de.consistec.doubleganger.common.data.schema.Schema Schema} representation into real
     * java object.
     *
     * @param xml XMl representation of Schema
     * @return Java Schema object
     * @throws SerializationException
     */
    public Schema fromXML(String xml) throws SerializationException {
        validateSchema(xml);
        schema = new Schema();
        try {
            SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
            parser.parse(new InputSource(new StringReader(xml)), this);
        } catch (ParserConfigurationException e) {
            throw new SerializationException(read(Errors.COMMON_CANT_CREATE_XML_PARSER), e);
        } catch (SAXException e) {
            throw new SerializationException(read(Errors.COMMON_CANT_PARSE_XML_DOCUMENT), e);
        } catch (IOException e) {
            throw new SerializationException(e);
        }
        return schema;
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {

        if (qName.equalsIgnoreCase(TABLE_QNAME)) {
            table = new Table(attributes.getValue(NAME_QNAME));
        } else if (qName.equalsIgnoreCase(CONSTRAINT_QNAME)) {

            constraint = new Constraint(ConstraintType.valueOf(attributes.getValue(TYPE_QNAME)), attributes.getValue(
                NAME_QNAME),
                attributes.getValue(COLUMN_QNAME));

        } else if (qName.equalsIgnoreCase(COLUMN_QNAME)) {

            try {
                column = new Column(attributes.getValue(NAME_QNAME),
                    SQLTypesUtil.typeOf(attributes.getValue(TYPE_QNAME)));
                String size = attributes.getValue(SIZE_QNAME);
                if (size != null && !size.isEmpty()) {
                    column.setSize(Integer.parseInt(size));
                }

                String decimalDigits = attributes.getValue(DECIMAL_DIGITS_QNAME);
                if (decimalDigits != null && !decimalDigits.isEmpty()) {
                    column.setDecimalDigits(Integer.parseInt(decimalDigits));
                }

                String nullable = attributes.getValue(NULLABLE_QNAME);
                if (nullable != null && !nullable.isEmpty()) {
                    column.setNullable(Boolean.parseBoolean(nullable));
                }
            } catch (IllegalAccessException e) {
                throw new SAXException(read(Errors.COMMON_CANT_CONVERT_TO_TYPE, attributes.getValue(TYPE_QNAME)), e);
            }
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        if (qName.equalsIgnoreCase(TABLE_QNAME)) {
            schema.addTables(table);
        } else if (qName.equalsIgnoreCase(COLUMN_QNAME)) {
            table.add(column);
        } else if (qName.equalsIgnoreCase(CONSTRAINT_QNAME)) {
            table.add(constraint);
        }
    }
}
