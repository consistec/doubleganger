Doubleganger is a framework to synchronize data between databases, regardless of the underlying technology (MySQL, PostgreSQL, SQLite...). It takes care of all synchronization details like conflict handling, sync strategy, performance, ...

The following picture illustrates the architecture of the framework:

[[Architektur.jpg]]

The core of the doubleganger framework is the SyncAgent. It implements the SyncStrategy and serves as an entry point for developers who want to know how the synchronization is handled. The ClientSyncProvider and ServerSyncProvider provide access to the underlying database.

The SyncAgent can access directly to the ServerSyncProvider or connect through an optional transport-layer. The transport-layer consists in a server side service (for example a servlet) and a client side implementation of the ServerSyncProvider interface (Proxy).

We provide also reference implementations for the client and the server of the transport layer using the HTTP protocol. 


