package de.consistec.doubleganger.server;

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

import static de.consistec.doubleganger.common.util.CollectionsUtil.newHashMap;

import de.consistec.doubleganger.common.Config;
import de.consistec.doubleganger.common.TableSyncStrategies;
import de.consistec.doubleganger.common.exception.ConfigException;
import de.consistec.doubleganger.common.util.PropertiesUtil;
import de.consistec.doubleganger.impl.commands.RequestCommand;
import de.consistec.doubleganger.impl.proxy.http_servlet.SyncAction;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author marcel
 * @company consistec Engineering and Consulting GmbH
 * @date 25.01.13 09:56
 */
public class ContextListenerMock implements ServletContextListener {

    public static final String SERVER_OPTION_POOLING = "pooling";
    public static final String SERVER_OPTION_DEBUG = "debug";
    public static final String HTTP_PROCESSOR_CTX_ATTR = "HTTP_PROCESSOR";
    private static final Logger LOGGER = LoggerFactory.getLogger(ContextListener.class.getCanonicalName());
    private static final String PARAMA_SYNC_CONFIG_FILE_NAME = "sync_config_file_name";
    private static final String PARAMA_SERVER_CONFIG_FILE_NAME = "server_config_file_name";
    private static final String DEFAULT_FRAMEWORK_CONFIG_FILE = "doubleganger.properties";
    private static final String DEFAULT_SERVER_CONFIG_FILE = "server.properties";
    private ServletContext ctx;
    private boolean isPooling;
    private boolean isDebugEnabled;
    private final String configFileName;
    private Map<SyncAction, RequestCommand> mockedRequests = newHashMap();
    private HttpServletProcessorMock processor;

    public ContextListenerMock(String configFileName) {
        this.configFileName = configFileName;
    }

    public ContextListenerMock() {
        this.configFileName = "/WEB-INF/" + DEFAULT_FRAMEWORK_CONFIG_FILE;
    }

    @Override
    public void contextInitialized(ServletContextEvent sce) {

        LOGGER.info("ContextListener startet ...");

        ctx = sce.getServletContext();


        try {
            loadDoublegangerConfig();
            LOGGER.info("Sync framework configuration loaded");
        } catch (IOException ex) {
            throw new ConfigException("Error while loading configuration for synchronization framework", ex);
        }

        try {
            loadServerConfig();
        } catch (IOException ex) {
            throw new ConfigException("Error while loading server configuration", ex);
        }

        try {
            prepareHttpProcessor();
        } catch (Exception ex) {
            throw new RuntimeException("Couldn't construct Http processor", ex);
        }

    }

    public void addRequest(SyncAction action, RequestCommand request) {
        mockedRequests.put(action, request);
        processor.exchangeCommand(action, request);
    }

    private void prepareHttpProcessor() throws Exception {

        processor = new HttpServletProcessorMock(isDebugEnabled);
        ctx.setAttribute(HTTP_PROCESSOR_CTX_ATTR, processor);
    }

    private String readParamFromContext(String defValue, String paramName) {
        return PropertiesUtil.defaultIfNull(defValue, ctx.getInitParameter(paramName));
    }

    private File readFile(String fileName) {
        return new File(ctx.getRealPath(fileName));
    }

    private void loadDoublegangerConfig() throws IOException {

        FileInputStream fis = null;

        try {
            fis = new FileInputStream(readFile(readParamFromContext(configFileName,
                PARAMA_SYNC_CONFIG_FILE_NAME)));
            Config config = Config.getInstance();
            config.init(fis);
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException ex) {
                    LOGGER.warn("Can't close sync framework config file");
                }
            }
        }
    }

    private void loadServerConfig() throws IOException {

        FileInputStream fis = null;
        Properties props = new Properties();

        try {
            fis = new FileInputStream(readFile(readParamFromContext(DEFAULT_SERVER_CONFIG_FILE,
                PARAMA_SERVER_CONFIG_FILE_NAME)));
            props.load(fis);

        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException ex) {
                    LOGGER.warn("Can't close server config file");
                }
            }
        }

        isPooling = PropertiesUtil.defaultIfNull(Boolean.FALSE, PropertiesUtil.readBoolean(props, SERVER_OPTION_POOLING,
            false)).booleanValue();
        LOGGER.debug("db pooling is {}", isPooling);
        ctx.setAttribute(SERVER_OPTION_POOLING, isPooling);

        isDebugEnabled = PropertiesUtil.defaultIfNull(Boolean.FALSE,
            PropertiesUtil.readBoolean(props, SERVER_OPTION_DEBUG,
                false)).booleanValue();
        LOGGER.debug("debug mode {}", isDebugEnabled);
        ctx.setAttribute(SERVER_OPTION_DEBUG, isDebugEnabled);
    }

    @Override
    public void contextDestroyed(final ServletContextEvent servletContextEvent) {
        LOGGER.info("ContextListener finished ...");
    }

    public void setDebugEnabled(final boolean debugEnabled) {
        processor.setDebugEnabled(debugEnabled);
    }

    public void setTableSyncStrategies(final TableSyncStrategies tableSyncStrategies) {
        processor.setTableSyncStrategies(tableSyncStrategies);
    }
}
