package de.consistec.syncframework.server;

import de.consistec.syncframework.common.TableSyncStrategies;
import de.consistec.syncframework.common.exception.ContextException;
import de.consistec.syncframework.impl.commands.RequestCommand;
import de.consistec.syncframework.impl.proxy.http_servlet.HttpServletProcessor;
import de.consistec.syncframework.impl.proxy.http_servlet.SyncAction;

/**
 * @author marcel
 * @company Consistec Engineering and Consulting GmbH
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
