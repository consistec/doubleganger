package de.consistec.doubleganger.common.adapter.impl;

/**
 * Container class to hold database connection values.
 *
 * @author marcel
 * @company Consistec Engineering and Consulting GmbH
 * @date 27.03.13 14:18
 */
public class ConnectionDataHolder {


    private String username;
    private String password;
    private String connectionUrl;
    private String driverName;

    /**
     * Constructor of this container to pass all necessary values.
     *
     * @param username - the username for db connection
     * @param password - the password for db connection
     * @param connectionUrl - the database connection url
     * @param driverName - the driver name
     */
    public ConnectionDataHolder(final String username, final String password, final String connectionUrl,
                                final String driverName
    ) {
        this.username = username;
        this.password = password;
        this.connectionUrl = connectionUrl;
        this.driverName = driverName;
    }

    /**
     * Gets the value of username.
     *
     * @return username - String
     */
    public String getUsername() {
        return username;
    }

    /**
     * Gets the value of password.
     *
     * @return passwoord - String
     */
    public String getPassword() {
        return password;
    }

    /**
     * Gets the value of connection url.
     *
     * @return connectionUrl - String
     */
    public String getConnectionUrl() {
        return connectionUrl;
    }

    /**
     * Gets the value of driver name.
     *
     * @return driverName - String
     */
    public String getDriverName() {
        return driverName;
    }
}
