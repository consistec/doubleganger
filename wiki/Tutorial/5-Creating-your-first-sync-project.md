The following links references to separate steps we will process in this tutorial. Starting with database creation and initialization we then go to the creation of the console client and server application. At last we will start the synchronization and check the result.

If you don't want to reimplement the SyncClient and SyncServer you can either pull the source code from git ([[3. Getting the doubleganger framework]]) or download it directy: [[SyncClient.tar.gz]], [[SyncServer.tar.gz]]

Create and initialize databases
===============================

We need two PostgreSQL databases to store the server's and the client's data. You could also use two different instances, the server instance running on port 5432 and the client instance on port 5433.

Creating user, database and tables with content on the server
-------------------------------------------------------------

1. Connect to the DB server with the `postgres` user and create a new user `syncuser`:
```bash
postgres@host:/home/user/syncframework$ psql

postgres=# \c postgres postgres localhost 5432

postgres=# CREATE USER syncuser WITH PASSWORD 'syncuser' LOGIN SUPERUSER INHERIT NOCREATEDB NOCREATEROLE REPLICATION;
```

2. Create the database `server`:
```bash
postgres=# CREATE DATABASE server WITH OWNER = syncuser ENCODING = 'UTF8' TABLESPACE = pg_default LC_COLLATE = 'de_DE.UTF8' LC_CTYPE = 'de_DE.UTF8' CONNECTION LIMIT = -1;
```

3. Create the table `categories` from which the data will be synced:
```bash
postgres=# CREATE TABLE categories (id INTEGER NOT NULL PRIMARY KEY, name VARCHAR (300), description VARCHAR(300));
```

4. Insert some data:
```bash
postgres=# INSERT INTO categories (id, name, description) VALUES (1, 'Beverages', 'Soft drinks');
postgres=# INSERT INTO categories (id, name, description) VALUES (2, 'Condiments', 'Sweet and ');
```

Creating user and database on the client
----------------------------------------

For the client database we only need to create the syncuser and the database 'client'. The doubleganger framework will detect the missing table during the synchronization and will create it for us.

*If the server and the client run on two separate PostgreSQL instances, you have to create the syncuser on the client as well (cf. first step).*

```bash
postgres=# CREATE DATABASE client WITH OWNER = syncuser ENCODING = 'UTF8' TABLESPACE = pg_default LC_COLLATE = 'de_DE.UTF8' LC_CTYPE = 'de_DE.UTF8' CONNECTION LIMIT = -1;
```

Now we have initialized our databases we can start implementing the console client application.

Client implementations
======================

Lets start with the implementation of the client application.

1) Create a new project
-----------------------
At first we create a normal maven project called 'SyncClient':

[[new-project-client.png]]

2) Dependencies
-----------------------
Next we need to add following dependencies to our pom.xml file: common, db_adapters, sync_provider_proxies and for the client database we also need the db driver:

```xml
 <?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>de.consistec.example</groupId>
    <artifactId>SyncClient</artifactId>
    <version>1.0-SNAPSHOT</version>
    <packaging>jar</packaging>
    <name>SyncClient</name>

    <build>
        <finalName>SyncClient</finalName>
        <plugins>
            <plugin>
                <artifactId>maven-compiler-plugin</artifactId>
                <version>2.5.1</version>
                <configuration>
                    <encoding>UTF-8</encoding>
                    <source>1.6</source>
                    <target>1.6</target>
                    <showDeprecation>true</showDeprecation>
                </configuration>
            </plugin>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-shade-plugin</artifactId>
                <version>2.0</version>
                <executions>
                    <execution>
                        <phase>package</phase>
                        <goals>
                            <goal>shade</goal>
                        </goals>
                        <configuration>
                            <transformers>
                                <transformer implementation="org.apache.maven.plugins.shade.resource.ManifestResourceTransformer">
                                    <mainClass>SyncClient</mainClass>
                                </transformer>
                            </transformers>
                            <createDependencyReducedPom>false</createDependencyReducedPom>
                        </configuration>
                    </execution>
                </executions>
            </plugin>
        </plugins>
    </build>

    <dependencies>
        <dependency>
            <groupId>de.consistec.doubleganger</groupId>
            <artifactId>common</artifactId>
            <version>0.0.1-SNAPSHOT</version>
        </dependency>
        <dependency>
            <groupId>de.consistec.doubleganger</groupId>
            <artifactId>db_adapters</artifactId>
            <version>0.0.1-SNAPSHOT</version>
        </dependency>
        <dependency>
            <groupId>de.consistec.doubleganger</groupId>
            <artifactId>sync_providers_proxies</artifactId>
            <version>0.0.1-SNAPSHOT</version>
        </dependency>
        <dependency>
            <groupId>postgresql</groupId>
            <artifactId>postgresql</artifactId>
            <version>9.1-901-1.jdbc4</version>
        </dependency>
    </dependencies>

</project>
```

3) Configuration
---------------------------------------------
To configure the doubleganger framework, we need to create a properties file called 'sync.properties':

```bash
# ###################################################################################
# Configuration for doubleganger sync-framework
# Please follow the naming convention when adding options for new adapters,
# e.g. option xyz for new database adapter MyDbAdapter:
# doubleganger.server.db_adapter.xyz for server and framework.client.db_adapter.xyz
# ###################################################################################

# Which tables should be monitored and synchronized. Comma-sepparated list.
doubleganger.sync_tables=categories
# Suffix for tables with data cheksums
doubleganger.md_table_suffix=_md

# ##################################################################
# Configuration of server synchronization provider
# ##################################################################
# Proxy class to invoking providers method on the remote server.
# If not specified, local instance (non proxy) of server provider will be used.
doubleganger.server.proxy_provider.class=de.consistec.doubleganger.impl.proxy.http_servlet.HttpServerSyncProxy
# options for Http proxy provider from consistec GmbH
doubleganger.server.proxy_provider.url=http://localhost:8080/SyncServer/SyncService

# ###############################################################
# Client Configuration
# ###############################################################

# How many times framework should try to synchronize when transaction error occurs. Default 3.
doubleganger.client.number_of_sync_tries_on_transaction_error=3
# Should the sync framework use triggers on the client for this database?
doubleganger.client.use_sql_triggers=false

# ##################################################################
# Configuration of database adapter for client side operations
# ##################################################################

# Canonical name of database adapter class.
# Default db adapter class is de.consistec.syncframework.common.adapter.GenericDatabaseAdapter
framework.client.db_adapter.class=de.consistec.doubleganger.impl.adapter.PostgresDatabaseAdapter
# Options for generic adapter
doubleganger.client.db_adapter.url=
doubleganger.client.db_adapter.driver=
# Database user configuration
doubleganger.client.db_adapter.user=syncuser
doubleganger.client.db_adapter.password=syncuser
# Option specific for PostgreSQL adapter from consistec
doubleganger.client.db_adapter.host=localhost
# Optional. Defaults to 5432
doubleganger.client.db_adapter.port=
doubleganger.client.db_adapter.db_name=client
```

In the code above there is the minimum set of properties needed for client server synchronization. As we can see, the client connects the server through a HttpServerSyncProxy. This class is an implementation of the `IServerSyncProvider` interface and hides the transfer and connection details (in this case for http-protocol). The service the proxy should connect to is given with the property 'framework.server.proxy_provider.url'. In our example we want to connect to the SyncServer Service on port 8080. The next section contains configuration details for the client database which is prostgresql. With the property 'framework.client.db_adapter.class' we specify a custom implemented database-adapter which is necessary for some databases.

4) Main class
------------------------------------------------
Now we create a Main class called SyncClient:

```java
import de.consistec.doubleganger.common.Config;
import de.consistec.doubleganger.common.ISyncProgressListener;
import de.consistec.doubleganger.common.SyncContext;
import de.consistec.doubleganger.common.exception.ContextException;
import de.consistec.doubleganger.common.exception.SyncException;
import java.io.IOException;
import java.io.InputStream;
/**
 * @author thorsten
 * @company consistec Engineering and Consulting GmbH
 * @date 15.03.13 16:04
 */
public class SyncClient {
       public static void main (String[] args) throws IOException, ContextException, SyncException {
           Config conf = Config.getInstance();
           InputStream in = SyncClient.class.getResourceAsStream("sync.properties");
           conf.init(in);
           SyncContext.ClientContext syncClient = SyncContext.client();
           syncClient.addProgressListener(new ISyncProgressListener() {
               @Override
               public void progressUpdate(final String message) {
                   System.out.println(message);
               }
               @Override
               public void syncFinished() {
                   System.out.println("Synchronization finished!");
               }
           });
           syncClient.synchronize();
       }
}
```

Before we can start the synchronization we need to create a `FileInputStream` for the 'sync.properties' configuration file and pass it for inizializing to the created Config instance. Then we create the client synchronization context through the method `client()` of the  static class `SyncContext`. We want also get progress information during synchronization so we just add a `ProgressListener` and write the messages to the system output stream. And finally we start the synchronization with the `synchronize()` method of the `ClientContext`.

To connect the synchronization server through http we need an client side implementation oft he `IServerSyncProvider` interface. In the sync.properties file we configured the `HttpServerSyncProxy` as such an implemantation.

The code block below shows the `getChanges` method as an example how we can do a http request to get the server changes. The `HttpServerSyncProxy` is already fully implemented and is included in the sync_provider_proxies project.

```java
/**
 * IServerSyncProvider implementation for HTTP transport layer.
 * <p/>
 * This proxy invokes methods on remote synchronization server provider through http protocol.
 * It uses the {@link JSONSerializationAdapter serializer} to transform data to and from String.<br/>
 * Requests and responses are sended/received with use of org.apache.httpcomponents library.
 * <p/>
 * Objects of this class should <b>not</b> be created directly with {@code new} keyword. Instead, a canonical name
 * has to be specified in framework configuration. See {@link de.
 */
public class HttpServerSyncProxy implements IServerSyncProvider {

	@Override
    public SyncData getChanges(int rev) throws SyncException {
        LOGGER.warn("--------------------------------------   Proxy called - get chages");

        try {
            List<NameValuePair> data = newArrayList();
            data.add(new BasicNameValuePair(ACTION.name(), SyncAction.GET_CHANGES.getStringName()));
            data.add(new BasicNameValuePair(REVISION.name(), String.valueOf(rev)));
            String serializedResponse = request(data);
            return serializationAdapter.deserializeMaxRevisionAndChangeList(serializedResponse);
        } catch (SerializationException e) {
            throw new SyncException(read(Errors.CANT_GET_CHANGES_SERIALIZATION_FAILURE), e);
        }
    }
}
```

Server implementations
======================

Now we want to create the SyncServer Project.

1) Create a new project
-----------------------

We create also a normal Maven project called SyncServer:

[new-project-server.png]

2) Dependencies
---------------
Add the required doubleganger dependencies and maven-compiler-plugin to the pom.xml:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>de.consistec.example</groupId>
    <artifactId>SyncServer</artifactId>
    <version>1.0-SNAPSHOT</version>
    <packaging>war</packaging>
    <name>SyncServer</name>

    <build>
        <finalName>SyncServer</finalName>
        <plugins>
            <plugin>
                <artifactId>maven-compiler-plugin</artifactId>
                <version>2.5.1</version>
                <configuration>
                    <encoding>UTF-8</encoding>
                    <source>1.6</source>
                    <target>1.6</target>
                    <showDeprecation>true</showDeprecation>
                </configuration>
            </plugin>
        </plugins>
    </build>

    <dependencies>
        <dependency>
            <groupId>javax.servlet</groupId>
            <artifactId>servlet-api</artifactId>
            <version>2.5</version>
            <scope>provided</scope>
        </dependency>
        <dependency>
            <groupId>postgresql</groupId>
            <artifactId>postgresql</artifactId>
            <version>9.1-901.jdbc4</version>
        </dependency>
        <dependency>
            <groupId>de.consistec.doubleganger</groupId>
            <artifactId>common</artifactId>
            <version>0.0.1-SNAPSHOT</version>
        </dependency>
        <dependency>
            <groupId>de.consistec.doubleganger</groupId>
            <artifactId>db_adapters</artifactId>
            <version>0.0.1-SNAPSHOT</version>
        </dependency>
        <dependency>
            <groupId>de.consistec.doubleganger</groupId>
            <artifactId>sync_providers_proxies</artifactId>
            <version>0.0.1-SNAPSHOT</version>
        </dependency>
    </dependencies>

</project>
```

3) Listener
------------

Now create a Class named ContextListener with implements the interface ServletContextListener.

We need to override the `contextInitialized()` method. Within this method we initialize the server configuration and create the HttpServletProcessor which will also be cached it in the ServletContext.

```java
package de.consistec.example.server;
import de.consistec.doubleganger.common.Config;
import de.consistec.doubleganger.common.exception.ContextException;
import de.consistec.doubleganger.impl.proxy.http_servlet.HttpServletProcessor;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import javax.naming.NamingException;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
/**
 * @author thorsten
 * @company consistec Engineering and Consulting GmbH
 * @date 18.03.13 11:04
 */
public class ContextListener implements ServletContextListener {
    public static final String HTTP_PROCESSOR_CTX_ATTR = "HTTP_PROCESSOR";
    private ServletContext ctx;
    @Override
    public void contextInitialized(final ServletContextEvent sce) {
        ctx = sce.getServletContext();
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(new File(ctx.getRealPath("/WEB-INF/sync.properties")));
            Config config = Config.getInstance();
            config.init(fis);
            HttpServletProcessor processor = new HttpServletProcessor(false);
            ctx.setAttribute(HTTP_PROCESSOR_CTX_ATTR, processor);
        } catch (FileNotFoundException e) {
            e.printStackTrace(System.err);
        } catch (IOException e) {
            e.printStackTrace(System.err);
        } catch (ContextException e) {
            e.printStackTrace(System.err);
        }
    }
    @Override
    public void contextDestroyed(final ServletContextEvent servletContextEvent) {
    }
}
```

4) Servlet
-----------

The Servlet just creates the HttpServletProcessor and delegates the client requests to it.

```java
package de.consistec.example.server;
import de.consistec.doubleganger.common.exception.SerializationException;
import de.consistec.doubleganger.common.exception.database_adapter.DatabaseAdapterException;
import de.consistec.doubleganger.impl.proxy.http_servlet.HttpServletProcessor;
import java.io.IOException;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
/**
 * @author thorsten
 * @company consistec Engineering and Consulting GmbH
 * @date 18.03.13 11:40
 */
public class SyncServiceServlet extends HttpServlet {
    protected void processRequest(HttpServletRequest req, HttpServletResponse resp)
        throws ServletException, IOException {
        try {
            HttpSession session = req.getSession();
            ServletContext ctx = session.getServletContext();
            HttpServletProcessor processor = (HttpServletProcessor) ctx.getAttribute(ContextListener.HTTP_PROCESSOR_CTX_ATTR);
            processor.execute(req, resp);
        } catch (SerializationException e) {
            throw new ServletException(e);
        } catch (DatabaseAdapterException e) {
            throw new ServletException(e);
        } finally {
            req.getSession().invalidate();
        }
    }
    protected void doPost (HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        processRequest(req, resp);
    }
    @Override
    protected void doGet (HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        processRequest(req, resp);
    }
}
```

The HttpServletProcessor is included in the project sync_providers_proxies. This class is responsible for creating the server context and the serialization adapter and wraps the client http-actions to the corresponding server methods. Like the client context the server context is created through calling `server()` of the static SyncContext class. For serialization the HttpServletProcessor creates the JSONSerializationAdapter.

5) Configuration
-----------------

For server side we also need a configuration file called 'sync.properties' and put it to the src/main/webapp/WEB-INF folder:

```bash
# ###################################################################################
# Configuration for doubleganger sync-framework
# Please follow the naming convention when adding options for new adapters,
# e.g. option xyz for new database adapter MyDbAdapter:
# doubleganger.server.db_adapter.xyz for server and framework.client.db_adapter.xyz
# ###################################################################################
# Which tables should be monitored and synchronized. Comma-sepparated list.
doubleganger.sync_tables=categories
# Suffix for tables with data cheksums
doubleganger.md_table_suffix=_md
# ##################################################################
# Configuration of database adapter for client side operations
# ##################################################################
# Canonical name of database adapter class.
# Default db adapter class is de.consistec.doubleganger.common.adapter.GenericDatabaseAdapter
doubleganger.server.db_adapter.class=de.consistec.doubleganger.impl.adapter.PostgresDatabaseAdapter
# Options for generic adapter
doubleganger.server.db_adapter.url=
doubleganger.server.db_adapter.driver=
# Database user configuration
doubleganger.server.db_adapter.user=syncuser
doubleganger.server.db_adapter.password=syncuser
# Option specific for PostgreSQL adapter from consistec
doubleganger.server.db_adapter.host=localhost
# Optional. Defaults to 5432
doubleganger.server.db_adapter.port=5432
doubleganger.server.db_adapter.db_name=server
# Use SQL-Triggers for change tracking
doubleganger.server.use_sql_triggers=true
```

6) Web.xml
-----------
And to complete the SyncServer application we need to create the web.xml file into the src/main/webapp/WEB-INF folder:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<web-app version="3.0" xmlns="http://java.sun.com/xml/ns/javaee" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://java.sun.com/xml/ns/javaee http://java.sun.com/xml/ns/javaee/web-app_3_0.xsd">

    <description>Sample server application based on syncframework</description>
    <display-name>consistec GmbH Syncserver</display-name>
    <context-param>
        <description>Name of the configuration file for synchronistion framework</description>
        <param-name>sync_config_file_name</param-name>
        <param-value>sync.properties</param-value>
    </context-param>

    <context-param>
        <description>Name of the configuration file for server app</description>
        <param-name>server_config_file_name</param-name>
        <param-value>server.properties</param-value>
    </context-param>

    <!-- Define servlets that are included in the example application -->

    <servlet>
        <servlet-name>SyncService</servlet-name>
        <servlet-class>de.consistec.example.server.SyncServiceServlet</servlet-class>
    </servlet>

    <servlet-mapping>
        <servlet-name>SyncService</servlet-name>
        <url-pattern>/SyncService</url-pattern>
    </servlet-mapping>

    <listener>
        <listener-class>de.consistec.example.server.ContextListener</listener-class>
    </listener>

    <resource-ref>
        <description>Connection Pool</description>
        <res-ref-name>jdbc/sync</res-ref-name>
        <res-type>org.postgresql.ds.PGSimpleDataSource</res-type>
        <res-auth>Container</res-auth>
    </resource-ref>
</web-app>
```

Running server and client
=========================

To start a synchronization we need to first build and deploy the SyncServer war file to tomcat.

 * to create the war file put 'mvn install' to the command line interface:
```bash
user@laptop:~/workspace/SyncServer$ mvn install
[INFO] Scanning for projects...
...
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESSFUL
[INFO] ------------------------------------------------------------------------
[INFO] Total time: 3 seconds
[INFO] Finished at: Thu Mar 28 12:25:05 CET 2013
[INFO] Final Memory: 8M/109M
[INFO] ------------------------------------------------------------------------
```

 * now change to the tomcat path and copy the SyncServer.war file included in target folder to the webapp folder:

```bash
root@laptop:~/var/lib/tomcat7/webapps# ls
ROOT
root@laptop:~/var/lib/tomcat7/webapps# cp /home/user/workspace/SyncServer/target/SyncServer.war .
root@laptop:~/var/lib/tomcat7/webapps# ls
ROOT  SyncServer   SyncServer.war
```

 * go to the SyncClient project and build also the jar file with 'mvn install':

```bash
user@laptop:~/workspace/SyncClient$ mvn install
[INFO] Scanning for projects...
...
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESSFUL
[INFO] ------------------------------------------------------------------------
[INFO] Total time: 3 seconds
[INFO] Finished at: Thu Mar 28 12:26:05 CET 2013
[INFO] Final Memory: 10M/115M
[INFO] ------------------------------------------------------------------------
```

 * with the command: 'java -jar target/SyncClient.jar' you can start the SyncClient and the synchronization begins.

```bash
user@laptop:~/workspace/SyncClient$ java -jar target/SyncClient.jar
Client: requesting changes from server...
Client: requesting changes from server finished ...
Client: applying changes from client on server ...
Client: applying changes from client on server finished ...
Synchronization finished!
```

If everything works smoothly, you should get the same progress output from the server as depicted above.

Lets look into our client database and we will see the new synchronized data from server:

[[after-sync-postgres.png]]
