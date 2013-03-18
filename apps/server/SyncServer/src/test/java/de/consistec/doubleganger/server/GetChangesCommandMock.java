package de.consistec.doubleganger.server;

/*
 * #%L
 * doppelganger
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
import de.consistec.doubleganger.common.exception.SerializationException;
import de.consistec.doubleganger.common.exception.SyncException;
import de.consistec.doubleganger.common.util.LoggingUtil;
import de.consistec.doubleganger.impl.commands.RequestCommand;
import de.consistec.doubleganger.impl.i18n.Errors;
import de.consistec.doubleganger.impl.proxy.http_servlet.HttpRequestParamValues;

import org.slf4j.cal10n.LocLogger;

/**
 * @author marcel
 * @company consistec Engineering and Consulting GmbH
 * @date 25.01.13 10:09
 */
public class GetChangesCommandMock implements RequestCommand {

    private static final LocLogger LOGGER = LoggingUtil.createLogger(
        GetChangesCommandMock.class.getCanonicalName());

    private final SyncData expectedSyncData;


    public GetChangesCommandMock(SyncData expectedSyncData) {
        this.expectedSyncData = expectedSyncData;
    }

    @Override
    public String execute(final HttpRequestParamValues paramValues) throws SyncException, SerializationException {
        try {
            return paramValues.getSerializationAdapter().serializeChangeList(expectedSyncData).toString();
        } catch (SerializationException e) {
            LOGGER.error(read(Errors.CANT_GET_SERVER_CHANGES), e);
            throw e;
        }
    }
}
