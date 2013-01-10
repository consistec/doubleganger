package de.consistec.syncframework.client;

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
