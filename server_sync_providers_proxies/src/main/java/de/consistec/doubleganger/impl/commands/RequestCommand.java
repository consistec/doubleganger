package de.consistec.doubleganger.impl.commands;

/*
 * #%L
 * Project - doppelganger
 * File - RequestCommand.java
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

import de.consistec.doubleganger.common.exception.SerializationException;
import de.consistec.doubleganger.common.exception.SyncException;
import de.consistec.doubleganger.impl.proxy.http_servlet.HttpRequestParamValues;

/**
 * This interface represents any server method call through http requests.
 *
 * @author marcel
 * @company consistec Engineering and Consulting GmbH
 * @date 11.01.13 09:26
 */
public interface RequestCommand {

//<editor-fold defaultstate="expanded" desc=" Class methods " >

    /**
     * Parses the request, invokes
     * {@link de.consistec.doubleganger.common.SyncContext.ServerContext any method }
     * and returns the result.
     *
     * @param paramValues values transfered through the http request parameter
     * @return json serialized response from server
     * @throws SyncException
     * @throws SerializationException
     */
    String execute(HttpRequestParamValues paramValues) throws SyncException,
        SerializationException;

//</editor-fold>

}
