package de.consistec.doubleganger.impl.commands;

/*
 * #%L
 * Project - doppelganger
 * File - GetSchemaCommand.java
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
import de.consistec.doubleganger.common.exception.SyncException;
import de.consistec.doubleganger.common.util.LoggingUtil;
import de.consistec.doubleganger.impl.i18n.Errors;
import de.consistec.doubleganger.impl.proxy.http_servlet.HttpRequestParamValues;

import org.slf4j.cal10n.LocLogger;

/**
 * Concrete class of RequestCommand that represents the server method call to getSchema.
 *
 * @author marcel
 * @company consistec Engineering and Consulting GmbH
 * @date 11.01.13 09:27
 */
public class GetSchemaCommand implements RequestCommand {

    private static final LocLogger LOGGER = LoggingUtil.createLogger(GetSchemaCommand.class.getCanonicalName());

    /**
     * Parses the request, invokes
     * {@link de.consistec.doubleganger.common.SyncContext.ServerContext getSchema() }
     * and returns the result.
     *
     * @param paramValues values transfered through the http request parameter
     * @return the result of the server operation getSchema().
     * @throws SyncException
     * @throws SerializationException
     */
    @Override
    public String execute(final HttpRequestParamValues paramValues) throws
        SyncException, SerializationException {
        try {
            return paramValues.getSerializationAdapter().serializeSchema(paramValues.getCtx().getSchema()).toString();
        } catch (SyncException e) {
            LOGGER.error(read(Errors.CANT_GET_CREATE_DB_SCHEMA), e);
            throw e;
        } catch (SerializationException e) {
            LOGGER.error(read(Errors.CANT_GET_CREATE_DB_SCHEMA), e);
            throw e;
        }
    }
}
