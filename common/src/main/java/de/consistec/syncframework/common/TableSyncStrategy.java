package de.consistec.syncframework.common;

/*
 * #%L
 * Project - doppelganger
 * File - TableSyncStrategy.java
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

import static de.consistec.syncframework.common.SyncDirection.CLIENT_TO_SERVER;
import static de.consistec.syncframework.common.SyncDirection.SERVER_TO_CLIENT;
import static de.consistec.syncframework.common.conflict.ConflictStrategy.CLIENT_WINS;
import static de.consistec.syncframework.common.conflict.ConflictStrategy.FIRE_EVENT;
import static de.consistec.syncframework.common.conflict.ConflictStrategy.SERVER_WINS;
import static de.consistec.syncframework.common.i18n.MessageReader.read;
import static de.consistec.syncframework.common.util.Preconditions.checkNotNull;
import static de.consistec.syncframework.common.util.Preconditions.checkState;

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
 * @company consistec Engineering and Consulting GmbH
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

        checkState(!(strategy == SERVER_WINS && direction == CLIENT_TO_SERVER), direction, strategy);
        checkState(!(strategy == FIRE_EVENT && direction == CLIENT_TO_SERVER), direction, strategy);
        checkState(!(strategy == CLIENT_WINS && direction == SERVER_TO_CLIENT), direction, strategy);
        checkState(!(strategy == FIRE_EVENT && direction == SERVER_TO_CLIENT), direction, strategy);
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
