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

import de.consistec.doubleganger.common.TableSyncStrategies;
import de.consistec.doubleganger.common.exception.ContextException;
import de.consistec.doubleganger.impl.commands.RequestCommand;
import de.consistec.doubleganger.impl.proxy.http_servlet.HttpServletProcessor;
import de.consistec.doubleganger.impl.proxy.http_servlet.SyncAction;

/**
 * @author marcel
 * @company consistec Engineering and Consulting GmbH
 * @date 25.01.13 09:59
 */
public class HttpServletProcessorMock extends HttpServletProcessor {


    public HttpServletProcessorMock(final boolean isDebugEnabled) throws ContextException {
        super(isDebugEnabled);
    }

    public HttpServletProcessorMock(final TableSyncStrategies tableSyncStrategies,
                                    final boolean isDebugEnabled
    ) throws ContextException {
        super(tableSyncStrategies, isDebugEnabled);
    }

    public void exchangeCommand(SyncAction syncAction, RequestCommand command) {
        actionCommands.put(syncAction.getStringName(), command);
    }

    public void setDebugEnabled(boolean isDebugEnabled) {
        super.isDebugEnabled = isDebugEnabled;
    }
}
