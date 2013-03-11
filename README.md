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

TODO

## Features

* Support for different databases (SQLite, MySQL, PostgreSQL)
* Filter
* Trigger support for high performance
* Network layer is transparent to the core: currently, JSON serialization over http(s) is supported
* Implemented in Java, but due to its flexible architecture, could be easily implemented in other languages (e.g., .Net) as well
* Little dependencies: works well in minimal environments (e.g., Android phones)
* Conflict detection per database row
* Sync-direction (bi-directional, client to server, or server to client) can be configured per database table
* Conflict resolution strategy can be configured per table (ask "user", server wins, client wins)

## How does it work?

TODO: very brief description with link to tutorial and other stuff

## Community

TODO

## Download

TODO

## Usage

TODO: cf. tutorial

## Building

TODO: maven & friends

## License

doubleganger is licensed under [GPL Version 3](http://www.gnu.org/licenses/gpl.html). Commercial licenses are available upon [request](swd@consistec.de).
