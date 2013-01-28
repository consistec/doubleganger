package de.consistec.syncframework.impl.adapter;

import static de.consistec.syncframework.common.i18n.MessageReader.read;

import de.consistec.syncframework.common.data.schema.ISQLConverter;
import de.consistec.syncframework.common.exception.SchemaConverterException;
import de.consistec.syncframework.common.exception.SerializationException;
import de.consistec.syncframework.common.i18n.Errors;

import java.io.IOException;
import java.io.StringReader;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Implementation of {@link de.consistec.syncframework.common.data.schema.ISQLConverter ISQLConverter} interface.
 * <p/>
 *
 * @author marcel
 * @company Consistec Engineering and Consulting GmbH
 * @date 04.12.12 11:04
 */
public class ChangeLogToSQLConverter implements ISQLConverter {

    private static final char QUOTE = ' ';
    private static final String LITERALS_COMMA = ",";
    private SQLChangelogConverterHandler handler;

    /**
     * @param schema Schema to convert.
     * @return empty String because it is not necessary to implement
     * @throws SchemaConverterException if conversion fails.
     * @see de.consistec.syncframework.common.data.schema.ISQLConverter.toSQL(final de.consistec.syncframework.common.data.schema.Schema schema)
     */
    @Override
    public String toSQL(Object objectToConvert) throws SchemaConverterException {
        return "";
    }

    /**
     * @param xml Change log.
     * @return SQL query for parsed xml document
     * @throws SerializationException if parsing of xml document fails.
     * @see de.consistec.syncframework.common.data.schema.ISQLConverter.fromChangelog(final String xml)}
     */
    @Override
    public String fromChangelog(String xml) throws SerializationException {
        final StringBuilder result = new StringBuilder();
        try {
            SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
            handler = new SQLChangelogConverterHandler(result);
            parser.parse(new InputSource(new StringReader(xml)), handler);
            return result.toString();
        } catch (ParserConfigurationException e) {
            throw new SerializationException(read(Errors.COMMON_CANT_CREATE_XML_PARSER), e);
        } catch (SAXException e) {
            throw new SerializationException(read(Errors.COMMON_CANT_PARSE_XML_DOCUMENT), e);
        } catch (IOException e) {
            throw new SerializationException(e);
        }
    }

    public String getTableName() {
        return handler.getTableName();
    }

    private static class SQLChangelogConverterHandler extends DefaultHandler {

        private static final String LITERALS_DELETE = "delete";
        private static final String LITERALS_PARENTHESES = "()";
        private static final String LITERALS_INSERT = "insert";
        private static final String LITERALS_UPDATE = "update";
        private static final String LITERALS_COLUMN = "column";
        private static final String LITERALS_RIGHT_PARENTHESIS = ")";
        private static final String LITERALS_SINGLE_QUOTATION_MARK = "'";
        private static final String TABLE_NAME_XML_ATTRIBUTE = "tableName";
        private boolean appendWhere;
        private boolean isInsert;
        private boolean isUpdate;
        private StringBuilder names;
        private StringBuilder values;
        private final StringBuilder result;
        private String tableName;

        public SQLChangelogConverterHandler(StringBuilder result) {
            this.result = result;
            appendWhere = false;
            isInsert = false;
            isUpdate = false;
            names = new StringBuilder(LITERALS_PARENTHESES);
            values = new StringBuilder(LITERALS_PARENTHESES);
        }

        @Override
        public void characters(char[] ch, int start, int length) throws SAXException {
            if (appendWhere) {
                if (result.lastIndexOf(LITERALS_COMMA) == result.length() - 1) {
                    result.deleteCharAt(result.length() - 1);
                }
                result.append(" where ");
                result.append(ch, start, length);
                appendWhere = false;
            }
        }

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) throws
            SAXException {

            if (LITERALS_INSERT.equalsIgnoreCase(qName)) {
                tableName = attributes.getValue(TABLE_NAME_XML_ATTRIBUTE);
                result.append(String.format("insert into %s ", tableName));
                isInsert = true;
            }

            if (LITERALS_DELETE.equalsIgnoreCase(qName)) {
                tableName = attributes.getValue(TABLE_NAME_XML_ATTRIBUTE);
                result.append(String.format("delete from %s", tableName));
            }

            if (LITERALS_UPDATE.equalsIgnoreCase(qName)) {
                tableName = attributes.getValue(TABLE_NAME_XML_ATTRIBUTE);
                result.append(String.format("update %s SET ", tableName));
                isUpdate = true;
            }

            if (LITERALS_COLUMN.equalsIgnoreCase(qName)) {

                if (isUpdate) {

                    String name = attributes.getValue("name");
                    String valueNumeric = attributes.getValue("valueNumeric");
                    String value = attributes.getValue("value");

                    if (valueNumeric != null && !valueNumeric.isEmpty()) {
                        result.append(String.format("%s=%s,", name, valueNumeric));
                    } else if (value != null && !value.isEmpty()) {
                        result.append(String.format("%s='%s',", name, value));
                    }
                }
                if (isInsert) {

                    String name = attributes.getValue("name");
                    String valueNumeric = attributes.getValue("valueNumeric");
                    String value = attributes.getValue("value");

                    if (LITERALS_PARENTHESES.equalsIgnoreCase(names.toString())) {
                        int index = names.lastIndexOf(LITERALS_RIGHT_PARENTHESIS);
                        names.insert(index, name);
                    } else {
                        int index = names.lastIndexOf(LITERALS_RIGHT_PARENTHESIS);
                        names.insert(index, LITERALS_COMMA + name);
                    }

                    if (valueNumeric != null && !valueNumeric.isEmpty()) {

                        if (LITERALS_PARENTHESES.equalsIgnoreCase(values.toString())) {
                            int index = values.lastIndexOf(LITERALS_RIGHT_PARENTHESIS);
                            values.insert(index, valueNumeric);
                        } else {
                            int index = values.lastIndexOf(LITERALS_RIGHT_PARENTHESIS);
                            values.insert(index, LITERALS_COMMA + valueNumeric);
                        }

                    } else if (value != null && !value.isEmpty()) {

                        if (LITERALS_PARENTHESES.equalsIgnoreCase(values.toString())) {
                            int index = values.lastIndexOf(LITERALS_RIGHT_PARENTHESIS);
                            values.insert(index,
                                LITERALS_SINGLE_QUOTATION_MARK + value + LITERALS_SINGLE_QUOTATION_MARK);
                        } else {
                            int index = values.lastIndexOf(LITERALS_RIGHT_PARENTHESIS);
                            values.insert(index,
                                LITERALS_COMMA + LITERALS_SINGLE_QUOTATION_MARK + value + LITERALS_SINGLE_QUOTATION_MARK);
                        }
                    }
                }
            }

            if ("where".equalsIgnoreCase(qName)) {
                appendWhere = true;
            }
        }

        @Override
        public void endElement(String uri, String localName, String qName) throws SAXException {
            if (LITERALS_INSERT.equalsIgnoreCase(qName)) {
                result.append(String.format("%s VALUES %s", names.toString(), values.toString()));
                names = new StringBuilder(LITERALS_PARENTHESES);
                values = new StringBuilder(LITERALS_PARENTHESES);
            }
            if (LITERALS_DELETE.equalsIgnoreCase(qName)
                || LITERALS_UPDATE.equalsIgnoreCase(qName)
                || LITERALS_INSERT.equalsIgnoreCase(qName)) {

                result.append(";\n");
                isInsert = false;
                isUpdate = false;
            }
        }

        public String getTableName() {
            return tableName;
        }
    }
}
