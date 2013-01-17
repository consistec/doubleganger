package de.consistec.syncframework.impl.adapter;

import de.consistec.syncframework.common.SyncSettings;
import de.consistec.syncframework.common.Tuple;
import de.consistec.syncframework.common.data.Change;
import de.consistec.syncframework.common.data.schema.Schema;
import de.consistec.syncframework.common.exception.SerializationException;

import java.util.List;

/**
 * Example interface for serialization of changes and schema.
 * It is not necessary to implements this interface when one wants to write its own proxy.
 * How will the sync request be transmitted to server side is undefined.
 * <br/>One can see sample usage  in
 * {@link de.consistec.syncframework.impl.proxy.http_servlet.HttpServerSyncProxy HttpServerSyncProxy}
 * implementation. ISerializationAdapter instance is in that case used to translate data into
 * JSON string, which is then transmitted through Http requests/responses.
 *
 * @param <T>
 * @author Markus Backes
 * @company Consistec Engineering and Consulting GmbH
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
    List<Change> deserializeChangeList(T serializedObject) throws SerializationException;

    /**
     * Deserialize Object{@code <T>} to List{@code <Change>}.
     * <p/>
     *
     * @param serializedObject The serialized object
     * @return The change list
     * @throws SerializationException
     */
    Tuple<Integer, List<Change>> deserializeMaxRevisionAndChangeList(T serializedObject) throws SerializationException;

    /**
     * serialize List{@code <Change>} to Object{@code <T>}.
     * <p/>
     *
     * @param changeList The change list to serialize
     * @return The serialized object
     * @throws SerializationException
     */
    T serializeChangeList(Tuple<Integer, List<Change>> changeList) throws SerializationException;

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
