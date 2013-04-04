package de.consistec.doubleganger.impl.adapter;

/*
 * #%L
 * Project - doubleganger
 * File - JSONSerializationAdapter.java
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
import static de.consistec.doubleganger.common.util.CollectionsUtil.newHashMap;
import static de.consistec.doubleganger.common.util.CollectionsUtil.newHashSet;

import de.consistec.doubleganger.common.SyncData;
import de.consistec.doubleganger.common.SyncDirection;
import de.consistec.doubleganger.common.SyncSettings;
import de.consistec.doubleganger.common.TableSyncStrategies;
import de.consistec.doubleganger.common.TableSyncStrategy;
import de.consistec.doubleganger.common.conflict.ConflictStrategy;
import de.consistec.doubleganger.common.data.Change;
import de.consistec.doubleganger.common.data.MDEntry;
import de.consistec.doubleganger.common.data.schema.Schema;
import de.consistec.doubleganger.common.data.schema.SchemaXMLConverter;
import de.consistec.doubleganger.common.exception.SerializationException;
import de.consistec.doubleganger.impl.i18n.Errors;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * This class translates data transmitted in synchronization process to and from JSON String.
 *
 * @author Markus Backes
 * @company consistec Engineering and Consulting GmbH
 * @date 12.04.12 11:30
 * @since 0.0.1-SNAPSHOT
 */
public class JSONSerializationAdapter implements ISerializationAdapter<String> {

    private static final String FIELD_NAME_EXISTS = "exists";
    private static final String FIELD_NAME_PRIMARYKEY = "primarykey";
    private static final String FIELD_NAME_REVISION = "revision";
    private static final String FIELD_NAME_TABLE_NAME = "tableName";
    private static final String FIELD_NAME_ROWDATA = "rowdata";
    private static final transient SchemaXMLConverter XML_CONVERTER = new SchemaXMLConverter();

    @Override
    public SyncData deserializeChangeList(final String serializedObject) throws
        SerializationException {

        return deserializeChangeList(serializedObject, 0);
    }

    @Override
    public SyncData deserializeMaxRevisionAndChangeList(final String serializedObject) throws
        SerializationException {

        SyncData changeList = deserializeChangeList(serializedObject, 1);

        try {

            final JSONArray array = new JSONArray(serializedObject);
            Integer maxRevision = Integer.valueOf(array.getInt(0));
            changeList.setRevision(maxRevision);

            return changeList;
        } catch (JSONException e) {
            throw new SerializationException(read(Errors.CANT_CONVERT_FROM_JSON_TO_CHANGE_LIST), e);
        }
    }

    private SyncData deserializeChangeList(final String serializedObject, int startIndex) throws
        SerializationException {

        try {

            final SyncData changesList = new SyncData();
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
                    mdEntry.setDataRowExists(mdEntryObject.getBoolean(FIELD_NAME_EXISTS));
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
                changesList.addChange(change);
            }
            return changesList;
        } catch (JSONException e) {
            throw new SerializationException(read(Errors.CANT_CONVERT_FROM_JSON_TO_CHANGE_LIST), e);
        }
    }

    @Override
    public String serializeChangeList(final SyncData syncData) throws
        SerializationException {

        try {

            JSONObject object;
            JSONObject mdEntryObject;
            MDEntry entry;
            Map<String, Object> rowData;
            JSONObject jsonRowData;
            JSONArray array = new JSONArray();

            array.put(syncData.getRevision());

            for (Change c : syncData.getChanges()) {

                entry = c.getMdEntry();
                mdEntryObject = new JSONObject();

                mdEntryObject.put(FIELD_NAME_EXISTS, entry.dataRowExists());

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

                mdEntryObject.put(FIELD_NAME_EXISTS, entry.dataRowExists());

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

    @Override
    public String serializeSettings(final SyncSettings clientSettings) throws SerializationException {
        final JSONArray tableJSONArray = new JSONArray();
        final JSONArray strategyJSONArray = new JSONArray();

        try {
            for (String table : clientSettings.getSyncTables()) {
                tableJSONArray.put(table);
                JSONObject obj = new JSONObject();
                obj.put("direction", clientSettings.getStrategy(table).getDirection());
                obj.put("conflictStrategy", clientSettings.getStrategy(table).getConflictStrategy());

                strategyJSONArray.put(obj);
            }
        } catch (JSONException e) {
            throw new SerializationException(e);
        }

        final JSONArray jsonArray = new JSONArray();
        jsonArray.put(tableJSONArray);
        jsonArray.put(strategyJSONArray);
        return jsonArray.toString();
    }

    @Override
    public SyncSettings deserializeSettings(final String serializedObject) throws SerializationException {
        try {
            final JSONArray settingsJSONArray = new JSONArray(serializedObject);
            final JSONArray tableJSONArray = settingsJSONArray.getJSONArray(0);
            final JSONArray strategyJSONArray = settingsJSONArray.getJSONArray(1);
            Set<String> tableNames = newHashSet();
            TableSyncStrategies strategies = new TableSyncStrategies();
            for (int i = 0; i < tableJSONArray.length(); i++) {
                String tableName = tableJSONArray.getString(i);
                tableNames.add(tableName);
                JSONObject strategyObject = strategyJSONArray.getJSONObject(i);
                SyncDirection direction = SyncDirection.valueOf(strategyObject.getString("direction"));
                ConflictStrategy strategy = ConflictStrategy.valueOf(strategyObject.getString("conflictStrategy"));
                strategies.addSyncStrategyForTable(tableName, new TableSyncStrategy(direction, strategy));
            }
            return new SyncSettings(tableNames, strategies);

        } catch (JSONException e) {
            throw new SerializationException(e);
        }
    }
}
