package de.consistec.doubleganger.impl.commands;

/*
 * #%L
 * Project - doppelganger
 * File - ApplyChangesCommand.java
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

import de.consistec.doubleganger.common.SyncData;
import de.consistec.doubleganger.common.data.Change;
import de.consistec.doubleganger.common.exception.SerializationException;
import de.consistec.doubleganger.common.exception.SyncException;
import de.consistec.doubleganger.common.util.LoggingUtil;
import de.consistec.doubleganger.common.util.StringUtil;
import de.consistec.doubleganger.impl.i18n.Errors;
import de.consistec.doubleganger.impl.i18n.Infos;
import de.consistec.doubleganger.impl.proxy.http_servlet.HttpRequestParamValues;

import java.util.List;
import org.slf4j.cal10n.LocLogger;

/**
 * Concrete class of RequestCommand that represents the server method call to applyChanges.
 *
 * @author marcel
 * @company consistec Engineering and Consulting GmbH
 * @date 11.01.13 09:28
 */
public class ApplyChangesCommand implements RequestCommand {

    private static final LocLogger LOGGER = LoggingUtil.createLogger(ApplyChangesCommand.class.getCanonicalName());

    /**
     * Parses the request, invokes
     * {@link de.consistec.doubleganger.common.SyncContext.ServerContext applyChanges() }
     * and returns the result.
     *
     * @param paramValues values transfered through the http request parameter
     * @return the result of the server operation applyChanges().
     * @throws SyncException
     * @throws SerializationException
     */
    @Override
    public String execute(final HttpRequestParamValues paramValues) throws
        SyncException,
        SerializationException {

        if (!StringUtil.isNullOrEmpty(paramValues.getClientChanges()) && !StringUtil.isNullOrEmpty(
            paramValues.getClientRevision())) {
            try {
                final int clientRevision = Integer.valueOf(paramValues.getClientRevision());

                List<Change> deserializedChanges = paramValues.getSerializationAdapter().deserializeChangeList(
                    paramValues.getClientChanges());
                LOGGER.debug("deserialized Changes:");
                LOGGER.debug("<{}>", deserializedChanges);
                SyncData syncData = new SyncData(clientRevision, deserializedChanges);
                int nextServerRevisionSendToClient = paramValues.getCtx().applyChanges(syncData);
                LOGGER.info(Infos.NEW_SERVER_REVISION, nextServerRevisionSendToClient);

                return String.valueOf(nextServerRevisionSendToClient);
            } catch (SyncException e) {
                LOGGER.error(read(Errors.CANT_APPLY_CHANGES, e.getLocalizedMessage()));
                throw e;
            }
        }

        return null;
    }
}
