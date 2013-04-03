After cloning the framework with '[[git clone|3 Getting the doubleganger framework]]', you may want to run the server app and execute the client apps to see the synchronization framework in action.

We will describe here how to setup each application project, starting with the SyncServer, since the client apps will only work if the server is running.

This description assumes that you have already installed and configured the following for the SyncServer:

- Maven 2 or higher,
- PostgreSQL 9.1 server or higher,
- Tomcat 6 servlet container or higher.

[apps' structure](apps-structure.png "Structure of the apps' projects")

First we need to setup the server and client database as described in [[a.) Create and initialize databases]].

SyncServer
==========

The SyncServer is the server side of the synchronization implemented as web application. To run the web application you must ensure that Tomcat 6 or higher is installed and running on port 8080.

Now we need to configure the SyncServer. The configuration file `doubleganger.properties` can be found in the directory `apps/SyncServer/src/main/webapp/WEB-INF`.

```bash
# Conflicts resolution strategy. Available actions are:
# SERVER_WINS, CLIENT_WINS, FIRE_EVENT.
# If no strategy specified, default value will be SERVER_WINS.
#doubleganger.conflict_action=FIRE_EVENT
# Sync direction, Available sync directions are:
# SERVER_TO_CLIENT, CLIENT_TO_SERVER
# If no sync direction is specified, than the default value will be SERVER_TO_CLIENT
# doubleganger.sync_direction=
# Which tables should be monitored and synchronised. Comma-sepparated list.
doubleganger.sync_tables=${db.sync_tables}
# How many times framework should try to synchronize when transaction error occurs. Default - 3.
doubleganger.number_of_sync_tries_on_transaction_error=10
# Suffix for tables with data cheksums
#doubleganger.md_table_suffix=_md

# ###############################################################
# Configuration of database adapter for server side operations
# ###############################################################

# Canonical name of database adapter class.
# Default db adapter class is de.consistec.doubleganger.common.adapter.GenericDatabaseAdapter
doubleganger.server.db_adapter.class=${db.adapter_class}
# Options for generic adapter
doubleganger.server.db_adapter.url=${db.url}
doubleganger.server.db_adapter.driver=${db.driver}
doubleganger.server.db_adapter.user=${db.user}
doubleganger.server.db_adapter.password=${db.passwd}
# Option specific for PostgreSQL adapter from consistec GmbH
doubleganger.server.db_adapter.host=${db.host}
# Optional. Defaults to 5432
doubleganger.server.db_adapter.port=${db.port}
doubleganger.server.db_adapter.db_name=${db.name}
doubleganger.server.db_adapter.schema=${postgres_schema}
# Use SQL-Triggers for change-tracking
doubleganger.use_sql_triggers=${use_sql_triggers}
```

As we can see the property values in the properties file are references to properties in the maven pom.xml file. These values will be replaced during the Maven build through resource filtering.
```xml
<properties>
    <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    <!-- Which tables should be monitored and synchronised. Comma-separated list. -->
    <db.sync_tables>categories</db.sync_tables>
    <!--  Default db adapter class is de.consistec.doubleganger.common.adapter.GenericDatabaseAdapter -->
    <db.adapter_class>de.consistec.doubleganger.impl.adapter.PostgresDatabaseAdapter</db.adapter_class>
    <!-- Options for generic adapter -->
    <db.url></db.url>
    <db.port>5432</db.port>
    <db.driver></db.driver>
    <db.name>server</db.name>
    <db.user>syncuser</db.user>
    <db.passwd>syncuser</db.passwd>
    <postgres_schema>public</postgres_schema>
    <!-- Option specific for PostgreSQL adapter from consistec GmbH -->
    <db.host>localhost</db.host>
    <!--<db.pooling>false</db.pooling>-->
    <debug>false</debug>
    <java.version>1.6</java.version>
    <!-- sql trigger support for change tracking -->
    <use_sql_triggers>false</use_sql_triggers>
    <!-- deploy information -->
    <deploy.name>SyncServer</deploy.name>
    <display_build>${maven.build.timestamp}</display_build>
</properties>
```

The properties show that the PostgreSQL server runs on port 5432, the database name is 'server' and we want to connect with the 'syncuser'. For synchronization with postgres databases we need a special DatabaseAdapter called 'PostgresDatabaseAdapter' and we want to synchronize the table 'categories'.

Additionally you can uncomment the properties 'doubleganger.conflict_action' and 'doubleganger.sync_direction' and set the values as described in the comments.

That's all there is to configure. Now change to the syncframework path and build the syncframework:
```bash
cd syncframework
mvn install
```

The following commands are all related to the syncframework directory.

After the syncframework build completed you have to build the SyncServer:
```bash
cd apps/server/SyncServer
mvn install
```

To deploy the SyncServer copy the SyncServer.war file existing in apps/SyncServer/target to your webapps folder in your tomcat installation:
```bash
cp /.../syncframework/apps/server/SyncServer/target/SyncServer.war /var/lib/tomcat6/webapps
```

Take a look into the syncserver.log file in Tomcat's logs directory to verify that the SyncServers deployment was correct. If everything ran smoothly, you can setup the first client application.

ConsoleSyncClient
=================

With the ConsoleSyncClient you can start the synchronization via terminal. We also need to configure the client side synchronization part to tell the framework which database the client uses, how the client wants to connect to the server (with which protocol), the url of the SyncServer as well as optional properties like sync direction, conflict action, ...

```bash
# ###################################################################################
# Configuration file for doubleganger.
# Please fallow the naming convention when adding options for new adapters,
# e.g. option xyz for new database adapter MyDbAdapter:
# doubleganger.server.db_adapter.xyz for server and doubleganger.client.db_adapter.xyz
# ###################################################################################

# Conflicts resolution strategy. Available actions are:
# SERVER_WINS, CLIENT_WINS, FIRE_EVENT.
# If no strategy specified, default value will be SERVER_WINS.
doubleganger.conflict_action=CLIENT_WINS
# Sync direction, Available sync directions are:
# SERVER_TO_CLIENT, CLIENT_TO_SERVER
# If no sync direction is specified, than the default value will be SERVER_TO_CLIENT
doubleganger.sync_direction=SERVER_TO_CLIENT
# Which tables should be monitored and synchronised. Comma-sepparated list.
doubleganger.sync_tables=categories
# How many times framework should try to synchronize when transaction error occurs. Default - 3.
doubleganger.number_of_sync_tries_on_transaction_error=3
# Suffix for tables with data cheksums
doubleganger.md_table_suffix=_md

# ##################################################################
# Configuration of database adapter for client side operations
# ##################################################################

# Canonical name of database adapter class.
# Default db adapter class is de.consistec.doubleganger.common.adapter.GenericDatabaseAdapter
doubleganger.client.db_adapter.class=de.consistec.doubleganger.impl.adapter.PostgresDatabaseAdapter
# Options for generic adapter
doubleganger.client.db_adapter.url=
doubleganger.client.db_adapter.driver=
doubleganger.client.db_adapter.user=syncuser
doubleganger.client.db_adapter.password=syncuser
# Option specific for PostgreSQL adapter from consistec
doubleganger.client.db_adapter.host=localhost
# Optional. Defaults to 5432
doubleganger.client.db_adapter.port=5432
doubleganger.client.db_adapter.db_name=client


# ##################################################################
# Configuration of server synchronization provider
# ##################################################################
# Proxy class to invoking remote server. If not specified, local instance (non proxy) of IServerSyncProvider will be used.
doubleganger.server.proxy_provider.class=de.consistec.doubleganger.impl.proxy.http_servlet.HttpServerSyncProxy
# options for Http proxy provider from consistec GmbH
doubleganger.server.proxy_provider.url=http://localhost:8080/SyncServer/SyncService
doubleganger.server.proxy_provider.username=
doubleganger.server.proxy_provider.password=
```

In the client configuration file there is an additional section to configure the synchronization provider. Like in the code block above, the property `doubleganger.server.proxy_provider.class` defines an HTTP proxy (packaged in the implementation folder) to connect to the server via HTTP. Don't forget to specify the SyncServer URL in the property `doubleganger.server.proxy_provider.url`.

After configuration you need to change to the client path and build the ConsoleSyncClient:
```bash
cd apps/client/ConsoleSyncClient
mvn install
```

To start the synchronization you need only execute the jar file in the target folder and pass with the `-s` option the configuration file as argument:
```bash
java -jar target/ConsoleSyncClient-0.0.1-SNAPSHOT.jar -s console_sync_client.properties
```

JMeter - TestSyncClient
=======================

The TestSyncClient is just like the ConsoleSyncClient the client side part of synchronization. But unlike the ConsoleSyncClient, the TestSyncClient is just an interface for the JMeter tests and should not be executed directly.

The JMeter tests can be found in the src/test/resources directory. The shell script `testsync.sh` will be called from the JMeter Test and start the TestSyncClient. There are two categories of JMeter tests:

 * load tests
 * performance tests

Each test creates a specific number of sync clients (threads) and each client has its own database. The difference between the load and performance tests is the use of server context. While the load tests have direct (local) connection to the SyncServer and each sync client creates its own SyncServer instance (one to one relation), the clients in the performance tests all connect to the same SyncServer instance through the Tomcat web server.

[JMeter tests schema](jmeter-tests-schema.jpg "Schema for the JMeter tests")

For each kind of test exists a configuration file:

- 'performance_config_postgre.properties' for the performance tests and
- 'config_postgre.properties' for the load tests.

The configuration values can be set again in the pom.xml file:
```xml
<properties>
    <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    <jmeter_test_path>${pom.basedir}/target/test-classes/jmeter-tests/</jmeter_test_path>
    <jmeter_sync_retries>15</jmeter_sync_retries>
    <jmeter_apply_changes_retries>10</jmeter_apply_changes_retries>
    <jmeter_parallel_syncs>5</jmeter_parallel_syncs>
    <jmeter_parallel_syncs2>10</jmeter_parallel_syncs2>
    <!-- postgresql-settings for tests -->
    <postgres_server>localhost</postgres_server>
    <postgres_port>5432</postgres_port>
    <postgres_port_for_performance_test>5432</postgres_port_for_performance_test>
    <postgres_connect_db>postgres</postgres_connect_db>
    <postgres_admin>postgres</postgres_admin>
    <postgres_admin_pwd>root</postgres_admin_pwd>
    <postgres_server_dbname>server</postgres_server_dbname>
    <postgres_client_dbname>client</postgres_client_dbname>
    <postgres_sync_user>syncuser</postgres_sync_user>
    <postgres_sync_pwd>syncuser</postgres_sync_pwd>
    <postgres_synctables>categories</postgres_synctables>
    <postgres_schema>public</postgres_schema>
    <postgres_database_adapter>
        de.consistec.doubleganger.impl.adapter.PostgresDatabaseAdapter
    </postgres_database_adapter>

    <!--proxy provider settings-->
    <proxy_provider>de.consistec.doubleganger.impl.proxy.http_servlet.HttpServerSyncProxy</proxy_provider>

    <maven.compiler.source>1.6</maven.compiler.source>
    <maven.compiler.target>1.6</maven.compiler.target>
    <java.version>1.6</java.version>
    <deploy.name>SyncServer</deploy.name>
</properties>
```

Change to TestSyncClient path and build it:
```bash
cd app/jmeter/TestSyncClient
mvn install
```

The designed JMeter tests are included in the src/test/resources directory. Within the tests we use variables which will also be resolved during the Maven package phase with resource filtering. During the Maven installation of the TestSyncClient the tests were copied to the directory `target/test-classes/jmeter-tests` and can be executed with JMeter.

The easiest way to run the JMeter Tests is to install [JMeter version 2.8 RC 1](http://archive.apache.org/dist/jmeter/binaries/). Before you open the tests, you need to build the JMeter Datasource plugin and copy it in the `lib/ext` directory of your JMeter installation:
```bash
cd apps/jmeter/JMeterDataSource
mvn install
cp target/ConsistecJDBCDataSource.jar /.../jmeter_home/lib/ext
```

Now start JMeter and check the correct installation of the JMeterDataSource plugin:
```bash
cd /.../jmeter_home/bin
./jmeter.sh &
```

If you right click on the Testplan -> Add -> Configuration Element, there should be a new menu option named 'ConsistecDataSourceElement'. If you can see this option, the plugin is installed correctly.

To open a Testplan go to File -> Open, select the test plan directory 'app/jmeter/TestSyncClient/target/test-classes/jmeter-tests' and select any test. Now you are ready to run the JMeter load and performance tests.

[jmeter-testplan.png]

AndroidSyncClient
=================

With the last client application exists a real smartphone (Android) synchronization app to synchronize data from your smartphone (SQLite database) to any server database.

To deploy the client app on your android device or an emulator you additionally need the Android SDK.  We  recommend you to download the latest version of the Android SDK Manager. After downloading install the Tools and the API 10 (Android 2.3.3 Gingerbread).

Read [Android developer documentation](http://developer.android.com/tools/index.html) for more information on how to continue.

Set up the Android SDK
------------------

```bash
root@debian-vm:/# cd /opt/
root@debian-vm:/# wget http://dl.google.com/android/android-sdk_r20.0.1-linux.tgz
root@debian-vm:/# tar -xf android-sdk_r20.0.1-linux.tgz
root@debian-vm:/# export ANDROID_HOME=/opt/android-sdk-linux/
```

Set up an Android Virtual Device
--------------------------------
For deploying the application on an emulator we have to create a virtual android device:
```bash
consistec@debian-vm:~$ /opt/android-sdk-linux/tools/android create avd -n TestDevice -t android-10 -c 128M
```

To use the sqlite database on android we needed to implement a concrete database adapter (GingerbreadSQLiteDatabaseAdapter) to our syncframework. The project ' ' includes the GingerbreadSQLiteDatabaseAdapter implementation and we firstly need to build it before the AndroidSyncClient project:
```bash
cd implementation/android-adapters
mvn install
```

Now build the client application:
```bash
cd apps/client/AndroidSyncClient
mvn install
```

OPTIONAL: with Android tests
```bash
mvn install -PandroidTest
```

Install the application on the Virtual Device
---------------------------------------------

If the build of the android application is finished you can are ready to deploy it on your created device 'TestDevice'.

But before you need to start your created virtual Device. To do this open the Virtual Device Manager:
```bash
$ANDROID_HOME/tools/android
```

Go to Tools -> Manage AVDs... Select your virtual device and start it.

Now you are ready to deploy your application:
```bash
$ANDROID_HOME/platform-tools/adb install AndroidSyncClient.apk
```

If you want to redeploy the application:
```bash
$ANDROID_HOME/platform-tools/adb install -r AndroidSyncClient.apk
```

If the deployment was successful you should see a new AndroidSyncClient icon on your smartphone / virtual Device.


