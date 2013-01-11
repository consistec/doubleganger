package de.consistec.syncframework.common;

import static de.consistec.syncframework.common.i18n.MessageReader.read;
import static de.consistec.syncframework.common.util.Preconditions.checkNotNull;
import static de.consistec.syncframework.common.util.SyncStatePreconditions.checkSyncDirectionAndConflictStrategyState;

import de.consistec.syncframework.common.conflict.ConflictStrategy;
import de.consistec.syncframework.common.i18n.Errors;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;

/**
 * Synchronization strategy for table.
 * Sync strategy is a collection of rules which affects the synchronization algorithm.
 * <p/>
 * This class is (and will be) immutable.
 *
 * @author Marcel
 * @company Consistec Engineering and Consulting GmbH
 * @date 30.10.12 15:46
 * @serial
 * @since 0.0.1-SNAPSHOT
 */
public final class TableSyncStrategy implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * Direction of synchronization process.
     * Not null.
     *
     * @serial
     * @since 0.0.1-SNAPHSOT
     */
    private SyncDirection direction;
    /**
     * Action to take in case of merge conflict.
     * Not null.
     *
     * @serial
     * @since 0.0.1-SNAPHSOT
     */
    private ConflictStrategy strategy;

    /**
     * Initialize strategy object.
     * This constructor requires all fields to be passed as parameters.
     *
     * @param direction Synchronization direction. Not null!
     * @param strategy Conflict strategy. Not null!
     */
    public TableSyncStrategy(SyncDirection direction, ConflictStrategy strategy) {
        this.direction = direction;
        this.strategy = strategy;

//        checkConflictAndSyncDirectionState(syncStrategy.getConflictAction(), getSyncDirectionForTable(table));
        validateState();
    }

    /**
     * Synchronization direction.
     *
     * @return Direction.
     */
    public SyncDirection getDirection() {
        return direction;
    }

    /**
     * Returns conflict strategy.
     *
     * @return Conflict strategy.
     */
    public ConflictStrategy getConflictStrategy() {
        return strategy;
    }

    /**
     * Remember to add new checks when adding fields to the class.
     */
    private void validateState() {
        checkNotNull(direction, read(Errors.COMMON_SYNC_DIRECTION_CANT_BE_NULL));
        checkNotNull(strategy, read(Errors.COMMON_CONFLICT_ACTION_CANT_BE_NULL));

        checkSyncDirectionAndConflictStrategyState(direction, strategy);
    }

    /**
     * While deserializing perform the same validation as in constructor.
     * <p/>
     *
     * @param objInputStream
     * @throws ClassNotFoundException
     * @throws IOException
     */
    private void readObject(ObjectInputStream objInputStream) throws ClassNotFoundException, IOException {
        objInputStream.defaultReadObject();
        validateState();
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 11 * hash + (this.direction != null ? this.direction.hashCode() : 0);
        hash = 11 * hash + (this.strategy != null ? this.strategy.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final TableSyncStrategy other = (TableSyncStrategy) obj;
        if (this.direction != other.direction) {
            return false;
        }
        if (this.strategy != other.strategy) {
            return false;
        }
        return true;
    }

    /**
     * String representation of instance state.
     * It is a subject of change but it will be something like<br/>
     * <code>TableSyncStrategy{property1=value, propertie2=value ... }</code>
     * <p/>
     *
     * @return Short description of object state.
     */
    @Override
    public String toString() {
        return getClass().getSimpleName() + "{"
            + "direction=" + (direction == null ? "null" : direction)
            + ", strategy="
            + (strategy == null ? "null" : strategy) + '}';
    }
}
