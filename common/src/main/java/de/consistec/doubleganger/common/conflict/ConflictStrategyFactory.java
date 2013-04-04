package de.consistec.doubleganger.common.conflict;

/*
 * #%L
 * Project - doubleganger
 * File - ConflictStrategyFactory.java
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
import static de.consistec.doubleganger.common.util.Preconditions.checkNotNull;

import de.consistec.doubleganger.common.SyncDirection;
import de.consistec.doubleganger.common.exception.database_adapter.DatabaseAdapterInstantiationException;
import de.consistec.doubleganger.common.i18n.Errors;

/**
 * @author marcel
 * @company consistec Engineering and Consulting GmbH
 * @date 12.12.12 15:02
 */
public final class ConflictStrategyFactory {

    private ConflictStrategyFactory() {
        throw new AssertionError("Instances not allowed");
    }

    /**
     * Creates a class of type IConflictStrategy depends on the configured sync direction.
     * <p/>
     *
     * @param syncDirection {@link SyncDirection}
     *
     * @return IConflictStrategy New instance of {@link de.consistec.doubleganger.common.conflict.IConflictStrategy}
     * implementation.
     */
    public static IConflictStrategy newInstance(SyncDirection syncDirection) throws
        DatabaseAdapterInstantiationException {

        checkNotNull(read(Errors.COMMON_SYNC_DIRECTION_CANT_BE_NULL));

        IConflictStrategy conflictHandlingStrategy = null;

        switch (syncDirection) {
            case CLIENT_TO_SERVER:
                conflictHandlingStrategy = new ClientToServerConflictStrategy();
                break;
            case SERVER_TO_CLIENT:
                conflictHandlingStrategy = new ServerToClientConflictStrategy();
                break;
            case BIDIRECTIONAL:
            default:
                conflictHandlingStrategy = new DefaultConflictStrategy();
        }

        return conflictHandlingStrategy;
    }
}
