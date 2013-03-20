package de.consistec.doubleganger.common.conflict;

/*
 * #%L
 * Project - doppelganger
 * File - IConflictStrategy.java
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

import de.consistec.doubleganger.common.IConflictListener;
import de.consistec.doubleganger.common.adapter.IDatabaseAdapter;
import de.consistec.doubleganger.common.client.ConflictHandlingData;
import de.consistec.doubleganger.common.data.ResolvedChange;
import de.consistec.doubleganger.common.exception.SyncException;
import de.consistec.doubleganger.common.exception.database_adapter.DatabaseAdapterException;

import java.security.NoSuchAlgorithmException;
import java.util.Map;

/**
 * @author marcel
 * @company consistec Engineering and Consulting GmbH
 * @date 12.12.12 11:56
 */
public interface IConflictStrategy {

    /**
     * @param adapter - database adapter to call db operations
     * @param data - data used for conflict handling
     * @throws DatabaseAdapterException
     * @todo write comment
     */
    void resolveByClientWinsStrategy(final IDatabaseAdapter adapter, final ConflictHandlingData data) throws
        DatabaseAdapterException;

    /**
     * @param adapter - database adapter to call db operations
     * @param data - data used for conflict handling
     * @throws DatabaseAdapterException
     * @throws NoSuchAlgorithmException
     * @todo write comment
     */
    void resolveByServerWinsStrategy(final IDatabaseAdapter adapter, final ConflictHandlingData data) throws
        DatabaseAdapterException, NoSuchAlgorithmException;

    /**
     * @param adapter - database adapter to call db operations
     * @param data - data used for conflict handling
     * @param clientData - data values of conflicted data row from client of type {@code Map<String, Object>}
     * where the keys the column names from the data row are and the values
     * the content of the data row are.
     * @param conflictListener - listener to call events if configured
     * @return ResolvedChange -  the modified change from user
     * @throws SyncException
     * @throws DatabaseAdapterException
     * @throws NoSuchAlgorithmException
     * @todo write comment
     */
    ResolvedChange resolveByFireEvent(final IDatabaseAdapter adapter, final ConflictHandlingData data,
                                      final Map<String, Object> clientData, final IConflictListener conflictListener
    ) throws SyncException, DatabaseAdapterException, NoSuchAlgorithmException;

}
