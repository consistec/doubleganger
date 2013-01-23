package de.consistec.syncframework.common;

import de.consistec.syncframework.common.data.Change;

import java.util.List;

/**
 * Container for revision and change set data which where synchronized.
 *
 * @author thorsten
 * @company Consistec Engineering and Consulting GmbH
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
