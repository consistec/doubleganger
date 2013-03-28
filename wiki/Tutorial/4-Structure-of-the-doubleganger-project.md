The following picture shows the project structure. The most important subprojects are:

 * common,
 * db_adapters,
 * server_sync_providers_proxies.

[[doubleganger-structure.png]]

The project repository is split into the following (maven) modules:

1. **common** contains sources and interfaces of the synchronization framework. This is the core project of the doubleganger framework. It contains the SyncAgent and all the interfaces and abstract classes for client and server side implementations. Its important to deploy the common library on client and server side if you want to implement a client-server architecture. The common project also contains an example configuration file with all configurable parameters. It also contains a generic database adapter (with the fancy name "GenericDatabaseAdapter") for a wide range of databases, including SQLite.
2. **apps** contains example applications which use the synchronization framework
     * jmeter/**JMeterDataSource** is a plugin JMeter datasource plugin to connect several databases in one thread group
     * jmeter/**TestSyncClient** is a synchronization client for JMeter based performance and load tests - this project contains also the JMeter tests
     * server/**SyncServer** is an example implementation of a Java sync server; it can be deployed on Tomcat and configured for several database backends. This is the reference HTTP server side implementation that contains only one Servlet and a ContextListener. The ContextListener creates the server context at deploy time and the Servlet just handles the HTTP requests and delegates the called actions.
     * client/**AndroidSyncClient** is an example of a synchronization client for Android
     * client/**ConsoleSyncClient** is a console based synchronization client (e.g., for automated tests)
3. **devtools**
     * **checkstyle** contains the checkstyle rules used accross theframework
     * **logging** uses AspectJ for generating verbose logging when needed
4. **implementation**
     * **android-adapters** contains database adapters for Android 2.3.3 (Gingerbread) and 4.0+ (Ice Cream Sandwich and Jelly Bean).
     * **db_adapters** contains two reference implementations of the database adapter interface for the PostgreSQL and MySQL databases.
     * **server_sync_providers_proxies** is an example implementation of the proxy interface for HTTP based data transmission, using JSON for serialization. It contains client and server side reference implementations for the HTTP protocol used in the [[Tutorial]].


