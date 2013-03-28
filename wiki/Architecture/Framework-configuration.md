Framework configuration
=======================

You can define the doubleganger framework's configuration with a property file, using the file `doubleganger_template.properties` as a template.

To load these properties into the framework, you should pass your property file to the Config class, as illustrated below:
```java
package de.consistec.doubleganger.impl;

public abstract class TestDatabase {
    ...
    public void init() throws SQLException, IOException {
        Config.getInstance().init(getClass().getResourceAsStream("/config_mysql.properties"));
        ...
    }
}
```

Working examples of loading configuration files can be found in the test package from the module Database adapters. Have a close look at `TestDatabase.java` and the test resource files.

Global configuration
====================

Conflict resolution
-------------------

If a conflict arises, the framework gives by default the highest priority to the server's changes (`SERVER_WINS`). You could however set the conflict_action property to `CLIENT_WINS` to solve any conflict with the client's changes.

If you prefer to be prompted to resolve every conflict, set its value to `FIRE_EVENT`.
```bash
# Conflicts resolution strategy. Available actions are:
# SERVER_WINS, CLIENT_WINS, FIRE_EVENT.
# If no strategy is specified, the default value will be SERVER_WINS.
doubleganger.conflict_action=
```

Sync direction
--------------

```bash
# Sync direction, Available sync directions are:
# SERVER_TO_CLIENT, CLIENT_TO_SERVER, BIDIRECTIONAL
# If no sync direction is specified, then the default value will be BIDIRECTIONAL
doubleganger.sync_direction=
```

If you defined the sync direction as `SERVER_TO_CLIENT` or `CLIENT_TO_SERVER`, you must reset the framework's databases prior to any direction change.

**Please note that the following combinations of sync direction and conflict resolution are invalid:**

* `SERVER_TO_CLIENT` and `CLIENT_WINS`
* `CLIENT_TO_SERVER` and `SERVER_WINS`

Miscellaneous
-------------

The property sync_tables defines which tables the framework should monitor and synchronize. For its internal purposes, the framework creates for each monitored table a "metadata table". To differentiate these from your own tables, it appends a suffix that you can define with the property `md_table_suffix`.

In the following code snippet, the framework monitors the table "mytable", creating the table "mytable_md" for its internal activities.
```bash
# Which tables should be monitored and synchronized. Comma separated list.
doubleganger.sync_tables=mytable
# Suffix for tables with data cheksums
doubleganger.md_table_suffix=_md
```


Server configuration
====================

```bash
# How many times the server should try to get its changes when transaction error occurs. Default 3.
doubleganger.server.number_of_get_changes_tries_on_transaction_error=3
# How many times the server should try to apply the client's changes when transaction error occurs. Default 3.
doubleganger.server.number_of_apply_changes_tries_on_transaction_error=3
# Should the sync framework use triggers on the server for this database?
doubleganger.server.use_sql_triggers=false
```

Configuration of database adapter for server side operations
------------------------------------------------------------

```bash
# Canonical name of database adapter class.
# Default db adapter class is de.consistec.doubleganger.common.adapter.impl.GenericDatabaseAdapter
doubleganger.server.db_adapter.class=
# Options for generic adapter
doubleganger.server.db_adapter.url=
doubleganger.server.db_adapter.driver=
doubleganger.server.db_adapter.user=
doubleganger.server.db_adapter.password=
# Option specific for PostgreSQL adapter from consistec GmbH
doubleganger.server.db_adapter.host=
# Optional. Defaults to 5432
doubleganger.server.db_adapter.port=
doubleganger.server.db_adapter.db_name=
# connection schema - defaults to PUBLIC
doubleganger.server.db_adapter.schema=
```

Configuration of server synchronization provider
------------------------------------------------

```bash
# Proxy class to invoking providers method on the remote server.
# If not specified, local instance (non proxy) of server provider will be used.
doubleganger.server.proxy_provider.class=
# options for Http proxy provider from consistec GmbH
doubleganger.server.proxy_provider.url=
doubleganger.server.proxy_provider.username=
doubleganger.server.proxy_provider.password=
```

Client Configuration
====================

```bash
# How many times framework should try to synchronize when transaction error occurs. Default 3.
doubleganger.client.number_of_sync_tries_on_transaction_error=3
# Should the sync framework use triggers on the client for this database?
doubleganger.client.use_sql_triggers=false
```

Configuration of database adapter for client side operations
------------------------------------------------------------
```bash
# Canonical name of database adapter class.
# Default db adapter class is de.consistec.doubleganger.common.adapter.GenericDatabaseAdapter
doubleganger.client.db_adapter.class=
# Options for generic adapter
doubleganger.client.db_adapter.url=
doubleganger.client.db_adapter.driver=
doubleganger.client.db_adapter.user=
doubleganger.client.db_adapter.password=
# Option specific for PostgreSQL adapter from consistec
doubleganger.client.db_adapter.host=
# Optional. Defaults to 5432
doubleganger.client.db_adapter.port=
doubleganger.client.db_adapter.db_name=
# Option specific for GingerbreadSQLiteDatabaseAdapter and ICSSQLiteDatabaseAdapter adapter from consistec
doubleganger.client.db_adapter.database_path=
# connection schema - defaults to PUBLIC
doubleganger.client.db_adapter.schema=
```

Maven properties
================

You can actually set most of these properties through Maven. They are to be found in `db_adapters/pom.xml`, as illustrated below:

```xml
<properties>
    <synctables>categories,items</synctables>
 
    <!-- postgresql-settings for tests -->
    <postgres_server>localhost</postgres_server>
    <postgres_port>5432</postgres_port>
    <postgres_connect_db>postgres</postgres_connect_db>
    <postgres_admin>postgres</postgres_admin>
    <postgres_admin_pwd>root</postgres_admin_pwd>
    <postgres_server_dbname>server</postgres_server_dbname>
    <postgres_client_dbname>client</postgres_client_dbname>
    <postgres_sync_user>syncuser</postgres_sync_user>
    <postgres_sync_pwd>syncuser</postgres_sync_pwd>
    <postgres_extern_user>postgres</postgres_extern_user>
    <postgres_extern_pwd>root</postgres_extern_pwd>
    <postgres_driver>org.postgresql.Driver</postgres_driver>
    <postgres_database_adapter>de.consistec.doubleganger.impl.adapter.PostgresDatabaseAdapter
    </postgres_database_adapter>
    <postgres_triggers_server>false</postgres_triggers_server>
    <postgres_triggers_client>false</postgres_triggers_client>
 
    <!-- mySql-settings for tests -->
    ...
</properties>
```
