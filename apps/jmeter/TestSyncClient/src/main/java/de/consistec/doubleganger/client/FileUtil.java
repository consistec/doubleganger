package de.consistec.doubleganger.client;

/*
 * #%L
 * doubleganger
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

import java.io.InputStream;

/**
 * Date: 28.09.12 09:33
 * <p/>
 * @author Marcel
 * @since 0.0.1-SNAPSHOT
 */
public final class FileUtil {

    private FileUtil() {
    }

    public static InputStream getDataSetResource(String fileName) {
        return Thread.currentThread().getContextClassLoader().getResourceAsStream(fileName);
    }

    /**
     * Extracts file extension from its name.
     * If it's impossible to extract extension (eg. there's no "." in filename) this method will return null
     * <p/>
     * @param filename
     * @return file extension or null id it's impossible to extract it from filename.
     */
    public static String getExtension(String filename) {

        String extension = null;
        int lastPointIndx = filename.lastIndexOf(".");

        if (lastPointIndx != -1) {
            extension = filename.substring(lastPointIndx + 1);
        }

        return (extension == null || extension.isEmpty()) ? null : extension;
    }
}
