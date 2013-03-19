package de.consistec.doubleganger.common.conflict;

/*
 * #%L
 * Project - doppelganger
 * File - ServerToClientConflictStrategy.java
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

import de.consistec.doubleganger.common.IConflictListener;
import de.consistec.doubleganger.common.SyncDirection;
import de.consistec.doubleganger.common.adapter.IDatabaseAdapter;
import de.consistec.doubleganger.common.client.ConflictHandlingData;
import de.consistec.doubleganger.common.exception.SyncException;
import de.consistec.doubleganger.common.exception.database_adapter.DatabaseAdapterException;
import de.consistec.doubleganger.common.i18n.Errors;
import de.consistec.doubleganger.common.util.LoggingUtil;

import java.security.NoSuchAlgorithmException;
import java.util.Map;
import org.slf4j.cal10n.LocLogger;

/**
 * @author marcel
 * @company consistec Engineering and Consulting GmbH
 * @date 12.12.12 15:22
 */
public class ServerToClientConflictStrategy extends DefaultConflictStrategy {

    private static final LocLogger LOGGER = LoggingUtil.createLogger(
        ServerToClientConflictStrategy.class.getCanonicalName());

    @Override
    public void resolveByClientWinsStrategy(final IDatabaseAdapter adapter, final ConflictHandlingData data
    ) throws DatabaseAdapterException {
        throw new IllegalStateException(
            read(Errors.NOT_SUPPORTED_CONFLICT_STRATEGY, ConflictStrategy.SERVER_WINS.name(),
                SyncDirection.CLIENT_TO_SERVER.name()));
    }

    @Override
    public void resolveByServerWinsStrategy(final IDatabaseAdapter adapter, final ConflictHandlingData data
    ) throws DatabaseAdapterException, NoSuchAlgorithmException {

        super.resolveByServerWinsStrategy(adapter, data);
    }

    @Override
    public boolean resolveByFireEvent(final IDatabaseAdapter adapter, final ConflictHandlingData data,
                                      final Map<String, Object> clientData, final IConflictListener conflictListener
    ) throws SyncException, DatabaseAdapterException, NoSuchAlgorithmException {
        throw new IllegalStateException(
            read(Errors.NOT_SUPPORTED_CONFLICT_STRATEGY, ConflictStrategy.FIRE_EVENT.name(),
                SyncDirection.SERVER_TO_CLIENT.name()));
    }
}
