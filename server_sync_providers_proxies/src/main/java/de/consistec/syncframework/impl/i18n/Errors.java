package de.consistec.syncframework.impl.i18n;

import ch.qos.cal10n.BaseName;
import ch.qos.cal10n.Locale;
import ch.qos.cal10n.LocaleData;

/**
 *
 * @company Consistec Engineering and Consulting GmbH
 * @date 04.12.2012 12:24:22
 * @author Piotr Wieczorek
 * @since 0.0.1-SNAPSHOT
 */
@BaseName("de/consistec/syncframework/impl/i18n/errors")
@LocaleData(value = {
    @Locale("en") })
public enum Errors {

    /**
     * When conversion from JSON to {@link java.util.List List&lt;Change&gt;} fails.
     */
    CANT_CONVERT_FROM_JSON_TO_CHANGE_LIST,
    /**
     * When conversion from {@link java.util.List List&lt;Change&gt;} to JSON fails.
     */
    CANT_CONVERT_CHANGELIST_TO_JSON,
    /**
     * When conversion from JSON string to {@link de.consistec.syncframework.common.data.schema.Schema Schema} fails.
     */
    CANT_CONVERT_JSON_TO_SCHEMA,
    /**
     * When conversion from {@link de.consistec.syncframework.common.data.schema.Schema Schema} to JSON string fails.
     */
    CANT_CONVERT_SCHEMA_TO_JSON,
    /**
     * When applying changes fails because of {@link de.consistec.syncframework.common.exception.SerializationException}.
     */
    CANT_APPLY_CHANGES_SERIALIZATION_FAILURE,
    /**
     * When attempt to changes fails because of {@link de.consistec.syncframework.common.exception.SerializationException}.
     */
    CANT_GET_CHANGES_SERIALIZATION_FAILURE,
    /**
     * When creating instance of {@link de.consistec.syncframework.common.data.schema.Schema Schema} fails because of
     * {@link de.consistec.syncframework.common.exception.SerializationException}.
     */
    CANT_GET_SCHEMA_SERIALIZATION_FAILURE,
    /**
     * When client had received server exception in response.
     * <p>
     * <b>Parameters</b>: http status, message.
     * </p>
     */
    SERVER_EXCEPTION_RECEIVED,
    /**
     * When server received unsupported action header.
     * <p>
     * <b>Parameters</b>: action.
     * </p>
     */
    SERVER_UNSUPPORTED_ACTION,
    /**
     * When server/client can't apply received changes.
     */
    CANT_APPLY_CHANGES,
    /**
     * When parsing the client revision fails.
     */
    CANT_PARSE_CLIENT_REVISION,
    /**
     * When client revision was not provided for getting the server change set.
     */
    CANT_GETCHANGES_NO_CLIENT_REVISION,
    /**
     * When attempt to get server change set fails.
     */
    CANT_GET_SERVER_CHANGES,
    /**
     * When attempt to create an instance of {@link de.consistec.syncframework.common.data.schema.Schema Schema} class
     * fails.
     */
    CANT_GET_CREATE_DB_SCHEMA;
}
