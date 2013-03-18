/*
 * Copyright (C) 2007 The Guava Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.consistec.doubleganger.common.util;

/*
 * #%L
 * Project - doppelganger
 * File - Preconditions.java
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


import static de.consistec.doubleganger.common.SyncDirection.CLIENT_TO_SERVER;
import static de.consistec.doubleganger.common.SyncDirection.SERVER_TO_CLIENT;
import static de.consistec.doubleganger.common.i18n.MessageReader.read;

import de.consistec.doubleganger.common.Config;
import de.consistec.doubleganger.common.SyncDirection;
import de.consistec.doubleganger.common.TableSyncStrategies;
import de.consistec.doubleganger.common.conflict.ConflictStrategy;
import de.consistec.doubleganger.common.data.Change;
import de.consistec.doubleganger.common.exception.SyncException;
import de.consistec.doubleganger.common.i18n.Errors;
import de.consistec.doubleganger.common.i18n.MessageReader;

import java.util.List;

/**
 * This class was copied from <a href="http://code.google.com/p/guava-libraries/">Guava</a> library.
 * <p/>
 * Simple static methods to be called at the start of your own methods to verify
 * correct arguments and state. This allows constructs such as
 * <pre>
 *     if (count <= 0) {
 *       throw new IllegalArgumentException("must be positive: " + count);
 *     }</pre>
 *
 * to be replaced with the more compact
 * <pre>
 *     checkArgument(count > 0, "must be positive: %s", count);</pre>
 *
 * Note that the sense of the expression is inverted; with {@code Preconditions}
 * you declare what you expect to be <i>true</i>, just as you do with an
 * <a href="http://java.sun.com/j2se/1.5.0/docs/guide/language/assert.html">
 * {@code assert}</a> or a JUnit {@code assertTrue} call.
 *
 * <p><b>Warning:</b> only the {@code "%s"} specifier is recognized as a
 * placeholder in these messages, not the full range of {@link
 * String#format(String, Object[])} specifiers.
 *
 * <p>Take care not to confuse precondition checking with other similar types
 * of checks! Precondition exceptions -- including those provided here, but also
 * {@link IndexOutOfBoundsException}, {@link java.util.NoSuchElementException}, {@link
 * UnsupportedOperationException} and others -- are used to signal that the
 * <i>calling method</i> has made an error. This tells the caller that it should
 * not have invoked the method when it did, with the arguments it did, or
 * perhaps ever. Postcondition or other invariant failures should not throw
 * these types of exceptions.
 *
 * <p>See the Guava User Guide on <a href=
 * "http://code.google.com/p/guava-libraries/wiki/PreconditionsExplained">
 * using {@code Preconditions}</a>.
 *
 * @author Kevin Bourrillion
 * @company The <a href="http://code.google.com/p/guava-libraries/">Guava</a> project
 * @date 22.11.2012 12:06
 * @since 0.0.1-SNAPSHOT
 */
public final class Preconditions {

    private Preconditions() {
        throw new AssertionError("No instances allowed");
    }

    /**
     * Ensures the truth of an expression involving one or more parameters to the calling method.
     *
     * @param expression a boolean expression
     * @throws IllegalArgumentException if {@code expression} is false.
     */
    public static void checkArgument(boolean expression) {
        if (!expression) {
            throw new IllegalArgumentException();
        }
    }

    /**
     * Ensures the truth of an expression involving one or more parameters to the calling method.
     *
     * @param expression a boolean expression
     * @param errorMessage the exception message to use if the check fails; will
     * be converted to a string using {@link String#valueOf(Object)}
     * @throws IllegalArgumentException if {@code expression} is false
     */
    public static void checkArgument(boolean expression, Object errorMessage) {
        if (!expression) {
            throw new IllegalArgumentException(String.valueOf(errorMessage));
        }
    }

    /**
     * Ensures the truth of an expression involving one or more parameters to the calling method.
     *
     * @param expression a boolean expression
     * @param errorMessageTemplate a template for the exception message should the
     * check fail. The message is formed by replacing each {@code %s}
     * placeholder in the template with an argument. These are matched by
     * position - the first {@code %s} gets {@code errorMessageArgs[0]}, etc.
     * Unmatched arguments will be appended to the formatted message in square
     * braces. Unmatched placeholders will be left as-is.
     * @param errorMessageArgs the arguments to be substituted into the message template.
     * Arguments are converted to strings using
     * {@link String#valueOf(Object)}.
     * @throws IllegalArgumentException if {@code expression} is false
     * @throws NullPointerException if the check fails and either {@code errorMessageTemplate}
     * or {@code errorMessageArgs} is null (don't let this happen)
     */
    public static void checkArgument(boolean expression, String errorMessageTemplate, Object... errorMessageArgs) {
        if (!expression) {
            throw new IllegalArgumentException(
                format(errorMessageTemplate, errorMessageArgs));
        }
    }

    /**
     * Ensures the truth of an expression involving the state of the calling
     * instance, but not involving any parameters to the calling method.
     *
     * @param expression a boolean expression
     * @throws IllegalStateException if {@code expression} is false
     */
    public static void checkState(boolean expression) {
        if (!expression) {
            throw new IllegalStateException();
        }
    }

    /**
     * Ensures the truth of an expression involving the state of the calling
     * instance, but not involving any parameters to the calling method.
     *
     * @param expression a boolean expression
     * @param errorMessage the exception message to use if the check fails; will
     * be converted to a string using {@link String#valueOf(Object)}
     * @throws IllegalStateException if {@code expression} is false
     */
    public static void checkState(boolean expression, Object errorMessage) {
        if (!expression) {
            throw new IllegalStateException(String.valueOf(errorMessage));
        }
    }

    /**
     * Ensures the truth of an expression involving the state of the calling
     * instance, but not involving any parameters to the calling method.
     *
     * @param expression a boolean expression
     * @param errorMessageTemplate a template for the exception message should the
     * check fail. The message is formed by replacing each {@code %s}
     * placeholder in the template with an argument. These are matched by
     * position - the first {@code %s} gets {@code errorMessageArgs[0]}, etc.
     * Unmatched arguments will be appended to the formatted message in square
     * braces. Unmatched placeholders will be left as-is.
     * @param errorMessageArgs the arguments to be substituted into the message
     * template. Arguments are converted to strings using
     * {@link String#valueOf(Object)}.
     * @throws IllegalStateException if {@code expression} is false
     * @throws NullPointerException if the check fails and either {@code errorMessageTemplate}
     * or {@code errorMessageArgs} is null (don't let this happen)
     */
    public static void checkState(boolean expression, String errorMessageTemplate, Object... errorMessageArgs) {
        if (!expression) {
            throw new IllegalStateException(
                format(errorMessageTemplate, errorMessageArgs));
        }
    }

    /**
     * Ensures that an object reference passed as a parameter to the calling method is not null.
     *
     * @param <T> Type of <i>reference</i>
     * @param reference an object reference
     * @return the non-null reference that was validated
     * @throws NullPointerException if {@code reference} is null
     */
    public static <T> T checkNotNull(T reference) {
        if (reference == null) {
            throw new NullPointerException();
        }
        return reference;
    }

    /**
     * Ensures that an object reference passed as a parameter to the calling method is not null.
     *
     * @param <T> Type of <i>reference</i>
     * @param reference an object reference
     * @param errorMessage the exception message to use if the check fails; will
     * be converted to a string using {@link String#valueOf(Object)}
     * @return the non-null reference that was validated
     * @throws NullPointerException if {@code reference} is null
     */
    public static <T> T checkNotNull(T reference, Object errorMessage) {
        if (reference == null) {
            throw new NullPointerException(String.valueOf(errorMessage));
        }
        return reference;
    }

    /**
     * Ensures that an object reference passed as a parameter to the calling method is not null.
     *
     * @param <T> Type of <i>reference</i>
     * @param reference an object reference
     * @param errorMessageTemplate a template for the exception message should the
     * check fail. The message is formed by replacing each {@code %s}
     * placeholder in the template with an argument. These are matched by
     * position - the first {@code %s} gets {@code errorMessageArgs[0]}, etc.
     * Unmatched arguments will be appended to the formatted message in square
     * braces. Unmatched placeholders will be left as-is.
     * @param errorMessageArgs the arguments to be substituted into the message
     * template. Arguments are converted to strings using
     * {@link String#valueOf(Object)}.
     * @return the non-null reference that was validated
     * @throws NullPointerException if {@code reference} is null
     */
    public static <T> T checkNotNull(T reference, String errorMessageTemplate, Object... errorMessageArgs) {
        if (reference == null) {
            // If either of these parameters is null, the right thing happens anyway
            throw new NullPointerException(
                format(errorMessageTemplate, errorMessageArgs));
        }
        return reference;
    }

    /*
     * All recent hotspots (as of 2009) *really* like to have the natural code
     *
     * if (guardExpression) {
     *    throw new BadException(messageExpression);
     * }
     *
     * refactored so that messageExpression is moved to a separate
     * String-returning method.
     *
     * if (guardExpression) {
     *    throw new BadException(badMsg(...));
     * }
     *
     * The alternative natural refactorings into void or Exception-returning
     * methods are much slower.  This is a big deal - we're talking factors of
     * 2-8 in microbenchmarks, not just 10-20%.  (This is a hotspot optimizer
     * bug, which should be fixed, but that's a separate, big project).
     *
     * The coding pattern above is heavily used in java.util, e.g. in ArrayList.
     * There is a RangeCheckMicroBenchmark in the JDK that was used to test this.
     *
     * But the methods in this class want to throw different exceptions,
     * depending on the args, so it appears that this pattern is not directly
     * applicable.  But we can use the ridiculous, devious trick of throwing an
     * exception in the middle of the construction of another exception.
     * Hotspot is fine with that.
     */

    /**
     * Ensures that {@code index} specifies a valid <i>element</i> in an array, list or string of size {@code size}.
     * An element index may range from zero, inclusive, to {@code size}, exclusive.
     *
     * @param index a user-supplied index identifying an element of an array, list or string
     * @param size the size of that array, list or string
     * @return the value of {@code index}
     * @throws IndexOutOfBoundsException if {@code index} is negative or is not less than {@code size}
     * @throws IllegalArgumentException if {@code size} is negative
     */
    public static int checkElementIndex(int index, int size) {
        return checkElementIndex(index, size, "index");
    }

    /**
     * Ensures that {@code index} specifies a valid <i>element</i> in an array,
     * list or string of size {@code size}. An element index may range from zero,
     * inclusive, to {@code size}, exclusive.
     *
     * @param index a user-supplied index identifying an element of an array, list or string
     * @param size the size of that array, list or string
     * @param desc the text to use to describe this index in an error message
     * @return the value of {@code index}
     * @throws IndexOutOfBoundsException if {@code index} is negative or is not less than {@code size}
     * @throws IllegalArgumentException if {@code size} is negative
     */
    public static int checkElementIndex(int index, int size, String desc) {
        // Carefully optimized for execution by hotspot (explanatory comment above)
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException(badElementIndex(index, size, desc));
        }
        return index;
    }

    private static String badElementIndex(int index, int size, String desc) {
        if (index < 0) {
            return format("%s (%s) must not be negative", desc, index);
        } else if (size < 0) {
            throw new IllegalArgumentException("negative size: " + size);
        } else {
            // index >= size
            return format("%s (%s) must be less than size (%s)", desc, index, size);
        }
    }

    /**
     * Ensures that {@code index} specifies a valid <i>position</i> in an array, list or string of size {@code size}.
     * A position index may range from zero to {@code size}, inclusive.
     *
     * @param index a user-supplied index identifying a position in an array, list or string
     * @param size the size of that array, list or string
     * @return the value of {@code index}
     * @throws IndexOutOfBoundsException if {@code index} is negative or is
     * greater than {@code size}
     * @throws IllegalArgumentException if {@code size} is negative
     */
    public static int checkPositionIndex(int index, int size) {
        return checkPositionIndex(index, size, "index");
    }

    /**
     * Ensures that {@code index} specifies a valid <i>position</i> in an array,
     * list or string of size {@code size}. A position index may range from zero to {@code size}, inclusive.
     *
     * @param index a user-supplied index identifying a position in an array, list or string
     * @param size the size of that array, list or string
     * @param desc the text to use to describe this index in an error message
     * @return the value of {@code index}
     * @throws IndexOutOfBoundsException if {@code index} is negative or is
     * greater than {@code size}
     * @throws IllegalArgumentException if {@code size} is negative
     */
    public static int checkPositionIndex(int index, int size, String desc) {
        // Carefully optimized for execution by hotspot (explanatory comment above)
        if (index < 0 || index > size) {
            throw new IndexOutOfBoundsException(badPositionIndex(index, size, desc));
        }
        return index;
    }

    private static String badPositionIndex(int index, int size, String desc) {
        if (index < 0) {
            return format("%s (%s) must not be negative", desc, index);
        } else if (size < 0) {
            throw new IllegalArgumentException("negative size: " + size);
        } else {
            // index > size
            return format("%s (%s) must not be greater than size (%s)",
                desc, index, size);
        }
    }

    /**
     * Ensures that {@code start} and {@code end} specify a valid <i>positions</i>
     * in an array, list or string of size {@code size}, and are in order. A
     * position index may range from zero to {@code size}, inclusive.
     *
     * @param start a user-supplied index identifying a starting position in an array, list or string
     * @param end a user-supplied index identifying a ending position in an array, list or string
     * @param size the size of that array, list or string
     * @throws IndexOutOfBoundsException if either index is negative or is
     * greater than {@code size}, or if {@code end} is less than {@code start}
     * @throws IllegalArgumentException if {@code size} is negative
     */
    public static void checkPositionIndexes(int start, int end, int size) {
        // Carefully optimized for execution by hotspot (explanatory comment above)
        if (start < 0 || end < start || end > size) {
            throw new IndexOutOfBoundsException(badPositionIndexes(start, end, size));
        }
    }

    private static String badPositionIndexes(int start, int end, int size) {
        if (start < 0 || start > size) {
            return badPositionIndex(start, size, "start index");
        }
        if (end < 0 || end > size) {
            return badPositionIndex(end, size, "end index");
        }
        // end < start
        return format("end index (%s) must not be less than start index (%s)", end, start);
    }

    /**
     * Substitutes each {@code %s} in {@code template} with an argument. These
     * are matched by position - the first {@code %s} gets {@code args[0]}, etc.
     * If there are more arguments than placeholders, the unmatched arguments will
     * be appended to the end of the formatted message in square braces.
     *
     * @param template a non-null string containing 0 or more {@code %s} placeholders.
     * @param args the arguments to be substituted into the message template. Arguments are converted to strings using
     * {@link String#valueOf(Object)}. Arguments can be null.
     */
    private static String format(final String template, Object... args) {
        // null -> "null"
        String templateLocal = String.valueOf(template);

        // start substituting the arguments into the '%s' placeholders
        StringBuilder builder = new StringBuilder(
            templateLocal.length() + 16 * args.length);
        int templateStart = 0;
        int i = 0;
        while (i < args.length) {
            int placeholderStart = templateLocal.indexOf("%s", templateStart);
            if (placeholderStart == -1) {
                break;
            }
            builder.append(templateLocal.substring(templateStart, placeholderStart));
            builder.append(args[i++]);
            templateStart = placeholderStart + 2;
        }
        builder.append(templateLocal.substring(templateStart));

        // if we run out of placeholders, append the extra args in square braces
        if (i < args.length) {
            builder.append(" [");
            builder.append(args[i++]);
            while (i < args.length) {
                builder.append(", ");
                builder.append(args[i++]);
            }
            builder.append(']');
        }

        return builder.toString();
    }

    /**
     * Validates the state of the global configured sync direction and conflict strategy.
     * <p/>
     * Invalid states are the following:
     * <ul>
     * <li>
     * the conflict strategies SERVER_WINS and FIRE_EVENT in combination with the CLIENT_TO_SERVER direction and
     * </li>
     * <li>
     * the conflict strategies CLIENT_WINS and FIRE_EVENT in combination with the SERVER_TO_CLIENT direction
     * </li>
     * </ul>
     */
    public static void checkGlobalSyncDirectionAndConflictStrategyState() {
        SyncDirection globalSyncDirection = Config.getInstance().getGlobalSyncDirection();
        ConflictStrategy globalConflictStrategy = Config.getInstance().getGlobalConflictStrategy();

        checkSyncDirectionAndConflictStrategyState(globalSyncDirection, globalConflictStrategy);
    }

    /**
     * Validates the state of sync direction and conflict strategy.
     * <p/>
     * Invalid states are the following:
     * <ul>
     * <li>
     * the conflict strategies SERVER_WINS and FIRE_EVENT in combination with the CLIENT_TO_SERVER direction and
     * </li>
     * <li>
     * the conflict strategies CLIENT_WINS and FIRE_EVENT in combination with the SERVER_TO_CLIENT direction
     * </li>
     * </ul>
     *
     * @param direction - sync direction to validate
     * @param strategy - conflict strategy to validate
     */
    public static void checkSyncDirectionAndConflictStrategyState(SyncDirection direction, ConflictStrategy strategy
    ) {
        if ((strategy == ConflictStrategy.SERVER_WINS || strategy == ConflictStrategy.FIRE_EVENT)
            && direction == SyncDirection.CLIENT_TO_SERVER
            || (strategy == ConflictStrategy.CLIENT_WINS || strategy == ConflictStrategy.FIRE_EVENT)
            && direction == SyncDirection.SERVER_TO_CLIENT) {
            throw new IllegalStateException(
                read(Errors.NOT_SUPPORTED_CONFLICT_STRATEGY, strategy.name(), direction.name()));
        }
    }

    /**
     * Validates the state of sync direction and conflict strategy.
     * <p/>
     * Invalid states are the following:
     * <ul>
     * <li>
     * the conflict strategies SERVER_WINS and FIRE_EVENT in combination with the CLIENT_TO_SERVER direction and
     * </li>
     * <li>
     * the conflict strategies CLIENT_WINS and FIRE_EVENT in combination with the SERVER_TO_CLIENT direction
     * </li>
     * </ul>
     *
     * @param expression expression to check
     * @param direction - sync direction to validate
     * @param strategy - conflict strategy to validate
     */
    public static void checkState(boolean expression, SyncDirection direction, ConflictStrategy strategy
    ) {
        if (!expression) {
            throw new IllegalStateException(
                read(Errors.NOT_SUPPORTED_CONFLICT_STRATEGY, strategy.name(), direction.name()));
        }
    }

    /**
     * Checks the expression. If the expression is false then a SyncException will be thrown.
     *
     * @param expression expression to check
     * @param errorMsg the error message passed through the SyncException
     * @throws SyncException thrown if expression is false
     */
    public static void checkSyncState(boolean expression, Errors errorMsg) throws SyncException {
        if (!expression) {
            throw new SyncException(MessageReader.read(errorMsg));
        }
    }

    /**
     * Checks if the passed server changes are valid synced. This means that the client has a valid
     * table sync strategy for the change.
     * For example:
     * The client has for table "categories" the table sync strategy: direction -> client_to_server
     * then the server should not sync changes for this table. In this case the method will throw a
     * SyncException.
     *
     * @param changes synced server changes
     * @param clientStrategies the table sync strategies on client
     * @throws SyncException if changes are invalid synced
     */
    public static void checkSyncDirectionOfServerChanges(List<Change> changes, TableSyncStrategies clientStrategies
    ) throws SyncException {
        checkSyncDirectionOfChanges(changes, clientStrategies, CLIENT_TO_SERVER,
            Errors.COMMON_NO_SERVERCHANGES_ALLOWED_TO_SYNC_FOR_TABLE);
    }

    /**
     * Checks if the passed client changes are valid synced. This means that the server has a valid
     * table sync strategy for the change.
     * For example:
     * The server has for table "categories" the table sync strategy: direction -> server_to_client
     * then the client should not sync changes for this table. In this case the method will throw a
     * SyncException.
     *
     * @param changes synced client changes
     * @param serverStrategies the table sync strategies on server
     * @throws SyncException if changes are invalid synced
     */
    public static void checkSyncDirectionOfClientChanges(List<Change> changes, TableSyncStrategies serverStrategies
    ) throws SyncException {
        checkSyncDirectionOfChanges(changes, serverStrategies, SERVER_TO_CLIENT,
            Errors.COMMON_NO_CLIENTCHANGES_ALLOWED_TO_SYNC_FOR_TABLE);
    }

    private static void checkSyncDirectionOfChanges(List<Change> changes, TableSyncStrategies serverStrategies,
                                                    SyncDirection direction, Errors errorMsg
    ) throws SyncException {
        StringBuilder invalidTableNames = new StringBuilder();
        for (Change change : changes) {
            String tableName = change.getMdEntry().getTableName();

            if ((serverStrategies.getSyncStrategyForTable(tableName).getDirection() == direction)) {
                invalidTableNames.append(tableName).append(", ");
            }
        }

        if (!invalidTableNames.toString().isEmpty()) {
            invalidTableNames.delete(invalidTableNames.length() - 2, invalidTableNames.length());
            throw new SyncException(
                MessageReader.read(errorMsg, invalidTableNames));
        }
    }
}
