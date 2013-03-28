The SyncProvider interfaces abstracts the synchronization specific actions which the [[SyncAgent]] triggers on both client and server side to handle one full sync.

There are two different interfaces for SyncProvider:

 * `IServerSyncProvider` only contains 3 methods:
    * applyChanges
    * getChanges
    * getSchema

 * `IClientSyncProvider` contains more methods, but its core methods are:
    * applyChanges
    * getChanges
    * getLastRevision
    * updateClientRevision
    * hasSchema
    * applySchema


