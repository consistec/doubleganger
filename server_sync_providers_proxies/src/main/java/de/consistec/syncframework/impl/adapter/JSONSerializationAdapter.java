package de.consistec.syncframework.impl.adapter;

/*
 * #%L
 * Project - doppelganger
 * File - JSONSerializationAdapter.java
 * %%
 * Copyright (C) 2011 - 2012 Consistec GmbH
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

import static de.consistec.syncframework.common.i18n.MessageReader.read;
import static de.consistec.syncframework.common.util.CollectionsUtil.newArrayList;
import static de.consistec.syncframework.common.util.CollectionsUtil.newHashMap;

import de.consistec.syncframework.common.Tuple;
import de.consistec.syncframework.common.data.Change;
import de.consistec.syncframework.common.data.MDEntry;
import de.consistec.syncframework.common.data.schema.Schema;
import de.consistec.syncframework.common.data.schema.SchemaXMLConverter;
import de.consistec.syncframework.common.exception.SerializationException;
import de.consistec.syncframework.impl.i18n.Errors;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * This class translates data transmitted in synchronization process to and from JSON String.
 *
 * @author Markus Backes
 * @company Consistec Engineering and Consulting GmbH
 * @date 12.04.12 11:30
 * @since 0.0.1-SNAPSHOT
 */
public class JSONSerializationAdapter implements ISerializationAdapter<String> {

    //<editor-fold defaultstate="expanded" desc=" Class fields " >
    private static final String FIELD_NAME_EXISTS = "exists";
    private static final String FIELD_NAME_PRIMARYKEY = "primarykey";
    private static final String FIELD_NAME_REVISION = "revision";
    private static final String FIELD_NAME_TABLE_NAME = "tableName";
    private static final String FIELD_NAME_ROWDATA = "rowdata";
    private static final transient SchemaXMLConverter XML_CONVERTER = new SchemaXMLConverter();
    //</editor-fold>

    //<editor-fold defaultstate="expanded" desc=" Class methods " >
    @Override
    public List<Change> deserializeChangeList(final String serializedObject) throws
        SerializationException {

        return deserializeChangeList(serializedObject, 0);
    }

    @Override
    public Tuple<Integer, List<Change>> deserializeMaxRevisionAndChangeList(final String serializedObject) throws
        SerializationException {

        List<Change> changeList = deserializeChangeList(serializedObject, 1);

        try {

            final JSONArray array = new JSONArray(serializedObject);
            Integer maxRevision = Integer.valueOf(array.getInt(0));

            return new Tuple<Integer, List<Change>>(maxRevision, changeList);
        } catch (JSONException e) {
            throw new SerializationException(read(Errors.CANT_CONVERT_FROM_JSON_TO_CHANGE_LIST), e);
        }
    }

    private List<Change> deserializeChangeList(final String serializedObject, int startIndex) throws
        SerializationException {

        try {

            final List<Change> changesList = newArrayList();
            final JSONArray array = new JSONArray(serializedObject);
            JSONObject object;
            JSONObject mdEntryObject;
            Change change;
            MDEntry mdEntry;
            JSONObject jsonMap;
            HashMap<String, Object> map;

            for (int i = startIndex; i < array.length(); i++) {

                change = new Change();
                mdEntry = new MDEntry();
                object = array.getJSONObject(i);
                mdEntryObject = object.getJSONObject("mdentry");

                if (mdEntryObject.has(FIELD_NAME_EXISTS)) {
                    mdEntry.setExists(mdEntryObject.getBoolean(FIELD_NAME_EXISTS));
                }

                mdEntry.setPrimaryKey(mdEntryObject.get(FIELD_NAME_PRIMARYKEY));
                mdEntry.setRevision(mdEntryObject.getInt(FIELD_NAME_REVISION));
                mdEntry.setTableName(mdEntryObject.getString(FIELD_NAME_TABLE_NAME));
                change.setMdEntry(mdEntry);

                jsonMap = object.getJSONObject(FIELD_NAME_ROWDATA);
                map = newHashMap();

                final Iterator<String> iterator = jsonMap.keys();

                String name;
                while (iterator.hasNext()) {

                    name = iterator.next();
                    if (jsonMap.get(name) == JSONObject.NULL) {
                        map.put(name, null);
                    } else {
                        map.put(name, jsonMap.get(name));
                    }
                }

                change.setRowData(map);
                changesList.add(change);
            }
            return changesList;
        } catch (JSONException e) {
            throw new SerializationException(read(Errors.CANT_CONVERT_FROM_JSON_TO_CHANGE_LIST), e);
        }
    }

    @Override
    public String serializeChangeList(final Tuple<Integer, List<Change>> changeListTuple) throws
        SerializationException {

        try {

            JSONObject object;
            JSONObject mdEntryObject;
            MDEntry entry;
            Map<String, Object> rowData;
            JSONObject jsonRowData;
            JSONArray array = new JSONArray();

            array.put(changeListTuple.getValue1().intValue());

            for (Change c : changeListTuple.getValue2()) {

                entry = c.getMdEntry();
                mdEntryObject = new JSONObject();

                if (entry.isExists()) {
                    mdEntryObject.put(FIELD_NAME_EXISTS, true);
                } else {
                    mdEntryObject.put(FIELD_NAME_EXISTS, false);
                }

                mdEntryObject.put(FIELD_NAME_PRIMARYKEY, entry.getPrimaryKey());
                mdEntryObject.put(FIELD_NAME_REVISION, entry.getRevision());
                mdEntryObject.put(FIELD_NAME_TABLE_NAME, entry.getTableName());
                object = new JSONObject();
                object.put("name", "change");
                object.put("mdentry", mdEntryObject);
                rowData = c.getRowData();
                jsonRowData = new JSONObject();

                for (Map.Entry<String, Object> column : rowData.entrySet()) {

                    if (null == column.getValue()) {
                        jsonRowData.put(column.getKey(), JSONObject.NULL);
                    } else {
                        jsonRowData.put(column.getKey(), column.getValue());
                    }

                }

                object.put(FIELD_NAME_ROWDATA, jsonRowData);
                array.put(object);
            }

            return array.toString();
        } catch (JSONException e) {
            throw new SerializationException(read(Errors.CANT_CONVERT_CHANGELIST_TO_JSON), e);
        }
    }

    @Override
    public String serializeChangeList(final List<Change> changeList) throws SerializationException {
        try {

            JSONObject object;
            JSONObject mdEntryObject;
            MDEntry entry;
            Map<String, Object> rowData;
            JSONObject jsonRowData;
            JSONArray array = new JSONArray();

            for (Change c : changeList) {

                entry = c.getMdEntry();
                mdEntryObject = new JSONObject();

                if (entry.isExists()) {
                    mdEntryObject.put(FIELD_NAME_EXISTS, true);
                } else {
                    mdEntryObject.put(FIELD_NAME_EXISTS, false);
                }

                mdEntryObject.put(FIELD_NAME_PRIMARYKEY, entry.getPrimaryKey());
                mdEntryObject.put(FIELD_NAME_REVISION, entry.getRevision());
                mdEntryObject.put(FIELD_NAME_TABLE_NAME, entry.getTableName());
                object = new JSONObject();
                object.put("name", "change");
                object.put("mdentry", mdEntryObject);
                rowData = c.getRowData();
                jsonRowData = new JSONObject();

                for (Map.Entry<String, Object> column : rowData.entrySet()) {

                    if (null == column.getValue()) {
                        jsonRowData.put(column.getKey(), JSONObject.NULL);
                    } else {
                        jsonRowData.put(column.getKey(), column.getValue());
                    }

                }

                object.put(FIELD_NAME_ROWDATA, jsonRowData);
                array.put(object);
            }
            return array.toString();
        } catch (JSONException e) {
            throw new SerializationException(read(Errors.CANT_CONVERT_CHANGELIST_TO_JSON), e);
        }
    }

    @Override
    public Schema deserializeSchema(final String serializedObject) throws SerializationException {
        try {
            final JSONObject jsObject = new JSONObject(serializedObject);
            return XML_CONVERTER.fromXML(jsObject.getString("schema"));
        } catch (JSONException e) {
            throw new SerializationException(read(Errors.CANT_CONVERT_JSON_TO_SCHEMA), e);
        }
    }

    @Override
    public String serializeSchema(final Schema schema) throws SerializationException {

        final JSONObject object = new JSONObject();
        try {
            object.put("schema", XML_CONVERTER.toXML(schema));
            return object.toString();
        } catch (JSONException e) {
            throw new SerializationException(read(Errors.CANT_CONVERT_SCHEMA_TO_JSON), e);
        }
    }
    //</editor-fold>
}
