package de.consistec.syncframework.impl.commands;

import de.consistec.syncframework.common.SyncContext;
import de.consistec.syncframework.common.exception.SerializationException;
import de.consistec.syncframework.common.exception.SyncException;
import de.consistec.syncframework.impl.adapter.ISerializationAdapter;

/**
 * This interface represents any server method call through http requests.
 *
 * @author marcel
 * @company Consistec Engineering and Consulting GmbH
 * @date 11.01.13 09:26
 */
public interface RequestCommand {

//<editor-fold defaultstate="expanded" desc=" Class fields " >

//</editor-fold>

//<editor-fold defaultstate="expanded" desc=" Class constructors " >

//</editor-fold>

//<editor-fold defaultstate="collapsed" desc=" Class accessors and mutators " >

//</editor-fold>

//<editor-fold defaultstate="expanded" desc=" Class methods " >

    /**
     * Parses the request, invokes
     * {@link de.consistec.syncframework.common.SyncContext.ServerContext any method }
     * and returns the result.
     *
     * @param ctx serverContext
     * @param serializationAdapter adapter for serialize the request parameter
     * @param clientRevision the client revision
     * @param clientChanges the client changes
     * @return the result of the server of any operation.
     * @throws SyncException
     * @throws SerializationException
     */
    String execute(SyncContext.ServerContext ctx, ISerializationAdapter serializationAdapter,
                   String clientRevision, String clientChanges
    ) throws SyncException,
        SerializationException;
//</editor-fold>

}
