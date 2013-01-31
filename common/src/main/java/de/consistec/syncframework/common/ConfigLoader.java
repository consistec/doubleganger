package de.consistec.syncframework.common;

import static de.consistec.syncframework.common.i18n.MessageReader.read;
import static de.consistec.syncframework.common.util.Preconditions.checkNotNull;

import de.consistec.syncframework.common.i18n.Errors;
import de.consistec.syncframework.common.i18n.Infos;
import de.consistec.syncframework.common.util.LoggingUtil;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import org.slf4j.cal10n.LocLogger;

/**
 * @author marcel
 * @company consistec Engineering and Consulting GmbH
 * @date 10.01.13 14:37
 */
public class ConfigLoader {

    /**
     * Default configuration file.<br/>
     * Value: {@value}
     */
    public static final String CONFIG_FILE = "/syncframework.properties";
    /**
     * Logger of this class.
     * Value: {@value}
     */
    private static final LocLogger LOGGER = LoggingUtil.createLogger(ConfigLoader.class.getCanonicalName());


    /**
     * Loads configuration from default configuration file.
     * Configuration file has to be java's <i>.properties</i> file.
     * <p/>
     *
     * @throws java.io.IOException When errors during accessing the stream occur.
     * @see #loadFromFile(java.io.InputStream)
     */
    public void loadFromFile() throws IOException {

        InputStream in = null;
        try {
            in = getClass().getResourceAsStream(CONFIG_FILE);
            loadFromFile(in);
        } finally {
            if (in != null) {
                in.close();
            }
        }
    }

    /**
     * Loads configuration from provided InputStream.
     * InputStream has to represents java's <i>.properties</i> file.
     * <p/>
     *
     * @param stream Stream to property file.
     * @return props the loaded properties from passed input stream.
     * @throws IOException well, shit happens.
     */
    public Properties loadFromFile(InputStream stream) throws IOException {

        checkNotNull(stream, read(Errors.COMMON_INPUT_STREAM_IS_NULL));

        LOGGER.info(Infos.CONFIG_LOADING_FROM_STREAM);
        Properties props = new Properties();
        props.load(stream);

        if (props.isEmpty()) {
            LOGGER.info(Infos.CONFIG_CONFIGURATION_FILE_IS_EMPTY);
        } else {
            LOGGER.info(Infos.CONFIG_CONFIG_LOADED);
        }
        return props;
    }
}
