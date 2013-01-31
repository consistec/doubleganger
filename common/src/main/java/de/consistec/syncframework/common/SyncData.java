package de.consistec.syncframework.common;

/*
 * #%L
 * Project - doppelganger
 * File - SyncData.java
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

import de.consistec.syncframework.common.data.Change;

import java.util.List;

/**
 * Container for revision and change set data which where synchronized.
 *
 * @author thorsten
 * @company consistec Engineering and Consulting GmbH
 * @date 22.01.13 16:46
 */
public class SyncData {

    private int revision;
    private List<Change> changes;

    /**
     * Constructor of the container.
     *
     * @param revision client or server revision
     * @param changes client or server change set
     */
    public SyncData(int revision, List<Change> changes) {
        this.revision = revision;
        this.changes = changes;
    }

    /**
     * Constructor of the container.
     *
     * @param syncData revision and change set from client or server
     */
    public SyncData(SyncData syncData) {
        this.revision = syncData.getRevision();
        this.changes = syncData.getChanges();
    }

    /**
     * returns the revision of the container.
     *
     * @return revision
     */
    public int getRevision() {
        return revision;
    }

    /**
     * returns the change set of the container.
     *
     * @return list of changes
     */
    public List<Change> getChanges() {
        return changes;
    }

    /**
     * sets the containers revision.
     *
     * @param revision client or server revision.
     */
    public void setRevision(final int revision) {
        this.revision = revision;
    }

    /**
     * sets the containers change set.
     *
     * @param changes from client or server
     */
    public void setChanges(final List<Change> changes) {
        this.changes = changes;
    }
}
