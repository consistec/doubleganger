package de.consistec.syncframework.common.conflict;

/*
 * #%L
 * Project - doppelganger
 * File - ConflictType.java
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

import de.consistec.syncframework.common.client.ConflictHandlingData;

/**
 * Enumeration which represents the possible, different conflicts types.
 * There are 6 following different conflict types in the syncframework:
 * <br/>
 * <p><ul>
 * <li>CLIENT_ADD_SERVER_ADD,</li>
 * <li>CLIENT_DEL_SERVER_DEL,</li>
 * <li>CLIENT_DEL_SERVER_MOD,</li>
 * <li>SERVER_DEL,</li>
 * <li>CLIENT_MOD_SERVER_MOD and</li>
 * <li>OUT_OF_DATE</li>
 * </ul></p>
 * <br/>
 * The conflict types are resolved with the method {@code isTheCase} of the {@code Resolver} interface.
 * The methods arguments are the localRevision, the localFlag, the localMdv and the remoteEntry. On the basis of
 * this arguments the resolver can decide which conflict type to return.
 * <br/>
 * The conflict types are resolved as followed:
 * <p>
 * CLIENT_ADD_SERVER_ADD -> if the localRevision is 0
 * CLIENT_DEL_SERVER_DEL -> if the localMdv is null or empty and it doesn't exist a remote entry
 * CLIENT_DEL_SERVER_MOD -> if the localMdv is null or empty and it exists a remote entry
 * SERVER_DEL            -> if the remote entry doesn't exist
 * CLIENT_MOD_SERVER_MOD -> if the localMdv and the remote entry exists
 * OUT_OF_DATE           -> if the localFlag is 1
 * </p>
 *
 * @author Piotr Wieczorek
 * @company consistec Engineering and Consulting GmbH
 * @date 21.11.2012 17:01:18
 * @since 0.0.1-SNAPSHOT
 */
public enum ConflictType {

    /**
     * Data was added on client and on server.
     */
    CLIENT_ADD_SERVER_ADD_OR_SERVER_MOD(new ConflictType.Resolver() {
    @Override
    public boolean isTheCase(ConflictHandlingData data) {
        return data.getLocalEntry().getRevision() == 0
                && data.getRemoteEntry().dataRowExists();
    }
}),
    /**
     * Data was added on client and on server.
     */
    CLIENT_ADD_SERVER_DEL(new ConflictType.Resolver() {
    @Override
    public boolean isTheCase(ConflictHandlingData data) {
        return data.getLocalEntry().getRevision() == 0
                && !data.getRemoteEntry().dataRowExists();
    }
}),
    /**
     * Data was modified on client and on server.
     */
    CLIENT_MOD_SERVER_ADD_OR_SERVER_MOD(new ConflictType.Resolver() {
    @Override
    public boolean isTheCase(ConflictHandlingData data) {
        return data.getLocalEntry().dataRowExists()
                && data.getRemoteEntry().dataRowExists();
    }
}),
    /**
     * Data was modified on client and on server.
     */
    CLIENT_MOD_SERVER_DEL(new ConflictType.Resolver() {
    @Override
    public boolean isTheCase(ConflictHandlingData data) {
        return data.getLocalEntry().dataRowExists()
                && !data.getRemoteEntry().dataRowExists();
    }
}),
    /**
     * Data was deleted on client and modified on server.
     */
    CLIENT_DEL_SERVER_ADD_OR_SERVER_MOD(new ConflictType.Resolver() {
    @Override
    public boolean isTheCase(ConflictHandlingData data) {
        return !data.getLocalEntry().dataRowExists()
                && data.getRemoteEntry().dataRowExists();
    }
}),
    /**
     * Data was deleted on client and on server.
     */
    CLIENT_DEL_SERVER_DEL(new ConflictType.Resolver() {
    @Override
    public boolean isTheCase(ConflictHandlingData data) {
        return !data.getLocalEntry().dataRowExists()
                && !data.getRemoteEntry().dataRowExists();
    }
});

    private ConflictType.Resolver resolver;

    private ConflictType(ConflictType.Resolver resolver) {
        this.resolver = resolver;
    }

    /**
     * Decides if the current conflict type should be used.
     * Delegated to the private resolver.
     *
     * @param data ConflictHandlingData which contains the data from client and server change.
     * @return true if resolver.isTheCase(localRev, localFlag, localMdv, remoteEntry) is true, otherwise false
     */
    public boolean isTheCase(ConflictHandlingData data) {
        return resolver.isTheCase(data);
    }

    /**
     * Contains method for resolving if the case is the current ConflictType.
     */
    public interface Resolver {

        /**
         * Decides if this conflict type is appropriate for the situation.
         *
         * @param data ConflictHandlingData which contains the data from client and server change.
         * @return true if this case is the current conflict type.
         */
        boolean isTheCase(ConflictHandlingData data);
    }
}
