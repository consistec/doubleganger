package de.consistec.doubleganger.common;

/*
 * #%L
 * Project - doppelganger
 * File - ConfigLoader.java
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

import static de.consistec.doubleganger.common.i18n.MessageReader.read;
import static de.consistec.doubleganger.common.util.Preconditions.checkNotNull;

import de.consistec.doubleganger.common.i18n.Errors;
import de.consistec.doubleganger.common.i18n.Infos;
import de.consistec.doubleganger.common.util.LoggingUtil;

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
    public static final String CONFIG_FILE = "/doubleganger.properties";
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
