package de.consistec.syncframework.common.adapter;

/*
 * #%L
 * Project - doppelganger
 * File - DatabaseAdapterCallback.java
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

import de.consistec.syncframework.common.exception.database_adapter.DatabaseAdapterException;

import java.sql.SQLException;

/**
 * Callback interface for ADatabaseAdapter class.
 * <p/>
 *
 * @author Markus Backes
 * @company consistec Engineering and Consulting GmbH
 * @date 03.07.12 11:36
 * @since 0.0.1-SNAPSHOT
 */
public interface DatabaseAdapterCallback<T> {

    /**
     * The callback method.
     * <p/>
     *
     * @param result The callback result
     * @throws DatabaseAdapterException
     */
    void onSuccess(T result) throws DatabaseAdapterException, SQLException;
}
