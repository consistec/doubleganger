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

TODO: very brief description with link to tutorial and other stuff

## Usage

TODO: cf. tutorial

## Download

Currently, we don't offer pre-build download. However, it's very easy to compile the code for yourself.

## Building

TODO: maven & friends

## License

doubleganger is licensed under [GPL Version 3](http://www.gnu.org/licenses/gpl.html). Commercial licenses are available upon [request](swd@consistec.de).

## Thanks

The initial development of doubleganger has been funded by Saarland's Ministry of Economic Affairs and Science within the program **Technologieprogramm Saar**.
