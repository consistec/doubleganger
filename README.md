# doubleganger

doubleganger is a flexible framework for the synchronization of SQL databases.

Currently, only centralized scenarios are supported, where a central *server* synchronizes its database with several *clients*.

A typical use case will look as follows

1. A new client connects and executes an initial sync
   1. The client receives the database schema from server
   1. The client initializes the local database and creates the payload tables and some tables for storing information about the synchronization state.
   1. The client receives the current database content from server
1. Changes are applied on both sides
   * Other users change data on the server (either directly or by synchronizing other clients)
   * data are changed on the client
1. synchronization of changes
   * client obtains server changes and applies them locally
      * conflict are resolved on the client
   * client sends its changes to the server
   * if no new conflict occurs, client and server database are identical now

## Target Audience

doubleganger is a software *library*, not a complete application. This means that doubleganger is intended to be used by software developers to add synchronization features to their applications.

## Features

* Support for different databases
   * Standard SQL databases
   * special support for: SQLite, MySQL, PostgreSQL
   * adaptations easily possible through database adapters
* Transparent network layer
   * network transfer can be easily adapted with proxy modules
   * standard transport layer: JSON serialization over http(s)
* Implemented in Java
   * platform independant: Linux, Windows, Android, ...
   * flexible architecture facilitates implementations in other languages as well
* Little dependencies
   * works well in minimal environments (e.g., Android phones)
* Automatic conflict detection
   * per database row
* Configurable conflict resolution strategies
   * ask user, server wins, client wins
   * configurable per table
* Sync-direction
   * bi-directional, client to server, server to client
   * can be configured per database table
* Good scalability
   * Server-side database memory requirement is independent of number of clients
   * Supports usage of triggers for efficient detection of server side changes
      * for PostgreSQL and MySQL

## How does it work?

For each synchronized table, doubleganger adds a meta table. These meta tables exist on client *and* server, though the format is not exactly the same.

### Change Tracking

The server has two ways for detecting changes:

1. If trigger support is enabled, a database trigger is installed which automatically sets a flag in the meta data tables for each row which is being updated.
1. If trigger support is disabled, doubleganger scans all tables during a synchronization and computes a hash values for each row. That hash value is compared with the original hash value in the meta tables; if they differ, the row has been changed meanwhile.

Comparison of the two methods
   1. Hash based variant
      * Pro
         * No special database support like triggers is needed
      * Cons
         * performance: doubleganger needs to scan over the complete table for every sync. For large tables this means a good amount of work.
   1. Trigger based change detection
      * Pro
         * gives much better synchronizsation performance than the hash based variant
      * Cons
         * needs trigger support in the database
         * there's a slight overhead for each database accecss due to the triggers. If there are much more database updates than syncs, this might be relevant.

### Revision Numbers

During synchronization, the server needs to decide which data records from a table to send to the client.

One way to achieve this would be to store server-side for each client and each data record the hash values of the last sync. So, the server could detect for each record whether the client already has the current version or needs an update. However, the memory requirement of this method grows linearly with the number of clients.

Doubleganger chooses a different approach:
   * similar to Subversion, doubleganger uses consecutive revision numbers
      * the revision number is increment for each sync
      * the client remembers the revision number of the last sync
      * the server remembers for each record the revision number of the last change (in the meta table)
      * at the beginning of a sync, the client sends its revision number to the server
         * thus, the server can detect which records have changed since the last sync of that client
   * Compared to approaches which store the synchronization time instead of a revision, this approach doesn't suffer from time zone or synchronization issues.

## Documentation and Usage

* A good starting point to get information about basic ideas and some details about the synchronization algorithm is the [doubleganger wiki](TODO).
* An example for the integration of doubleganger into your own code is given in the [doubleganger tutorial](TODO:link to tutorial).
* There's is [JavaDoc](TODO:link) available.
* If you have questions or suggestions that are not covered by these documents, you're very welcome to contact us via the [mailing list](TODO:link)

## Download

Currently, we don't offer pre-build download. However, it's very easy to compile the code for yourself.

## Repository Structure

The project repository is split into several (maven) modules:

* common: contains sources and interfaces of the synchronization framework
* devtools:
  * checkstyle
  * logging: uses AspectJ for generating verbose logging when needed
* implementation
  * android-adapters: contains database adapters for Android 2.3 (for Android 4.0 you may use [SQLDroid](https://github.com/SQLDroid/SQLDroid) which we cannot provide due to license incompatibility with GPL)
  * db_adapters: contains implementations of the database adapter interface for several data bases, in particular for MySQL and PostgreSQL
  * server_sync_providers_proxies: example implementation of the proxy interface for http based data transmission using JSON for serialization
* apps: contains example applications which use the synchronization framework
  * jmeter: plugin and syncclient for JMeter based performance and load tests
  * server: example implementation of a Java sync server; can be deployed on Tomcat and configured for several database backends
  * client/AndroidSyncClient: example synchronization client for Android
  * client/ConsoleSyncClient: console based synchronization client (e.g., for automated tests)
* parent: empty maven project; most other maven projects inherit basic settings from here

## How to Build doubleganger

doubleganger's build process is based on [Apache Maven](http://maven.apache.org/).

The library can be built via the usual steps (mvn clean && mvn compile) and can be integrated easily into other Maven projects. Besides that, the generated JAR-File can also be used in non-Maven projects.

For details about the usage of doubleganger in your project see the [doubleganger tutorial](TODO:link to tutorial).

## License

doubleganger is licensed under [GPL Version 3](http://www.gnu.org/licenses/gpl.html). Commercial licenses are available upon [request](swd@consistec.de).

## Thanks

The initial development of doubleganger has been funded by Saarland's Ministry of Economic Affairs and Science within the program **Technologieprogramm Saar**.
