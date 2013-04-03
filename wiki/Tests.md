JUnit
=====

JMeter
======
To run JMeter tests located under common/src/test/resources a SyncServer has to be started at localhost:8080.

 1. Open JMeter
 2. File -> Open -> ... choose jmx file (e.g. JMeter-Test-ADD.jmx)
 3. Hit start from toolbar

JMeter plugin
-------------

The project 'ConsistecDataSource' implements a JMeter plugin to get db-connections per thread.

By default JMeter uses connection pooling in tests. This means the db configuration is added globally to the test plan and within the test groups (threads/clients) the sampler referencing to this db-connection via a pool variable. Due to the connection pooling it is not possible to create a db connection for each thread/client. The new JMeter plugin creates a connection pool for each thread/client. The plugin uses a variable named 'users' that must be defined in the test plan. This variable holds the number of threads/client the test will create.

To get the plugin work, copy the ConsistecJDBCDataSource.jar file in the lib/ext folder within the jmeter directory. JMeter has to be new started and then the new plugin is visible under configuration elements and is named 'JDBC Connection Per Thread Configuration'
