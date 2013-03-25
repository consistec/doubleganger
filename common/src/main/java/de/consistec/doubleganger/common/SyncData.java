package de.consistec.doubleganger.common;

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
import static de.consistec.doubleganger.common.util.CollectionsUtil.newArrayList;

import de.consistec.doubleganger.common.data.Change;

import java.util.Collections;
import java.util.List;

/**
 * Container for revision and change set data which were synchronized.
 *
 * @author thorsten
 * @company consistec Engineering and Consulting GmbH
 * @date 22.01.13 16:46
 */
public class SyncData {

    private int revision;
    private List<Change> changes;

    /**
     * Creates a new SyncData (revision 0, no changes).
     */
    public SyncData() {
        this.revision = 0;
        this.changes = newArrayList();
    }

    /**
     * Creates a clone of an existing SyncData (copying its {@link Change}s).
     *
     * @param syncData revision and change set from client or server
     */
    public SyncData(SyncData syncData) {
        this.revision = syncData.getRevision();
        // we have to copy the lists to remove items from it.
        this.changes = newArrayList(syncData.getChanges());
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
     * sets the containers revision.
     *
     * @param revision client or server revision.
     */
    public void setRevision(final int revision) {
        this.revision = revision;
    }

    /**
     * returns the change set of the container.
     *
     * @return list of changes
     */
    public List<Change> getChanges() {
        return Collections.unmodifiableList(changes);
    }

    /**
     * Adds a change to synchronize.
     * <p/>
     * @param change the change to synchronize.
     */
    public void addChange(final Change change) {
        this.changes.add(change);
    }

    /**
     * Remove a change from the synchronization list.
     * <p/>
     * @param change the change to remove.
     */
    public void removeChange(final Change change) {
        this.changes.remove(change);
    }

    /**
     * Sorts the change list using the {@link Change.getPrimaryKeyComparator}.
     */
    public void sortChanges() {
        Collections.sort(this.changes, Change.getPrimaryKeyComparator());
    }

    /**
     * Searches the list of changes for this specific change and returns the conflicting change.
     * Please use {@link sortChanges()} beforehand to sort the list of changes.
     * @param remoteChange the change to search for
     * @return the conflicting change (null if not found)
     */
    public Change getConflictingChange(Change remoteChange) {
        int foundIndex = Collections.binarySearch(this.changes, remoteChange, Change.getPrimaryKeyComparator());
        return (foundIndex < 0) ? null : this.changes.get(foundIndex);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final SyncData other = (SyncData) obj;
        if (this.revision != other.revision) {
            return false;
        }
        if (this.changes != other.changes && (this.changes == null || !this.changes.equals(other.changes))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 79 * hash + this.revision;
        hash = 79 * hash + (this.changes != null ? this.changes.hashCode() : 0);
        return hash;
    }

}
