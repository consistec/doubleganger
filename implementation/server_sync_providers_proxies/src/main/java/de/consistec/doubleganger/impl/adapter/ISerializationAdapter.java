package de.consistec.doubleganger.impl.adapter;

/*
 * #%L
 * Project - doubleganger
 * File - ISerializationAdapter.java
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

import de.consistec.doubleganger.common.SyncData;
import de.consistec.doubleganger.common.SyncSettings;
import de.consistec.doubleganger.common.data.Change;
import de.consistec.doubleganger.common.data.schema.Schema;
import de.consistec.doubleganger.common.exception.SerializationException;

import java.util.List;

/**
 * Example interface for serialization of changes and schema.
 * It is not necessary to implements this interface when one wants to write its own proxy.
 * How will the sync request be transmitted to server side is undefined.
 * <br/>One can see sample usage  in
 * {@link de.consistec.doubleganger.impl.proxy.http_servlet.HttpServerSyncProxy HttpServerSyncProxy}
 * implementation. ISerializationAdapter instance is in that case used to translate data into
 * JSON string, which is then transmitted through Http requests/responses.
 *
 * @param <T>
 * @author Markus Backes
 * @company consistec Engineering and Consulting GmbH
 * @date 12.04.12 12:57
 * @since 0.0.1-SNAPSHOT
 */
public interface ISerializationAdapter<T> {

    /**
     * Deserialize Object{@code <T>} to List{@code <Change>}.
     * <p/>
     *
     * @param serializedObject The serialized object
     * @return The change list
     * @throws SerializationException
     */
    SyncData deserializeChangeList(T serializedObject) throws SerializationException;

    /**
     * Deserialize Object{@code <T>} to List{@code <Change>}.
     * <p/>
     *
     * @param serializedObject The serialized object
     * @return The change list
     * @throws SerializationException
     */
    SyncData deserializeMaxRevisionAndChangeList(T serializedObject) throws SerializationException;

    /**
     * serialize List{@code <Change>} to Object{@code <T>}.
     * <p/>
     *
     * @param syncData the sync datas to serialize
     * @return The serialized object
     * @throws SerializationException
     */
    T serializeChangeList(SyncData syncData) throws SerializationException;

    /**
     * serialize List{@code <Change>} to Object{@code <T>}.
     * <p/>
     *
     * @param changeList The change list to serialize
     * @return The serialized object
     * @throws SerializationException
     */
    T serializeChangeList(List<Change> changeList) throws SerializationException;

    /**
     * Deserialize Object{@code <T>} to Schema.
     * <p/>
     *
     * @param serializedObject The serialized object
     * @return The Schema
     * @throws SerializationException
     */
    Schema deserializeSchema(T serializedObject) throws SerializationException;

    /**
     * serialize Schema to Object{@code <T>}.
     * <p/>
     *
     * @param schema object representing database schema.
     * @return The serialized object
     * @throws SerializationException
     */
    T serializeSchema(Schema schema) throws SerializationException;

    /**
     * serialize the passed SyncSettings to Object{@code <T>}.
     *
     * @param clientSettings object representing the settings to sync.
     * @return the serialized object
     * @throws SerializationException
     */
    T serializeSettings(SyncSettings clientSettings) throws SerializationException;

    /**
     * deserialize the passed Object{@code <T>} to SyncSettings.
     *
     * @param serializedObject the serialized settings object
     * @return SyncSettings
     */
    SyncSettings deserializeSettings(T serializedObject) throws SerializationException;
}
