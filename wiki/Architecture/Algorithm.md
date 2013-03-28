This doubleganger framework is originally based on [SAMD](Choi2010_A_Database_Synchronization_Algorithm_for_Mobile_Devices.pdf) (Synchronization Algorithm Based on Message Digest). This means several of its components refer to this technique. 

| Component         | Function |
|-------------------|----------|
| HashProcessor     | Processes a given list of changes and tries to apply them to the database. The client implementation is much more complex than the server implementation. Because conflicts can occur on the client and must be resolved by the hash processor. Server side code throws an exception if a conflict occurs. |
| ChangesEnumerator | Enumerates changes since last synchronization. The server and client implementation differ:  |
|                   | * Client: select every data row with matching hash row where flag is set to 1 |
|                   | * Server: select every data row with matching hash row where revision number is higher than the given revision number |
| TableSynchronizer | This component synchronizes the data tables with the hash tables. The tables must be synchronized before other components like hash processor and changes enumerator can start their work. Also this implementation differs on client and server. |

The following diagrams point at the differences:

 * [[SysML_SAMD_Abgleich_Hashes.pdf]]
 * [[SysML_SAMD_Enumeration.pdf]]
 * [[SysML_SAMD_Verarbeitung_Hasheintrag_Server.pdf]]
 * [[SysML_SAMD_Verarbeitung_Hasheintrag_Client.pdf]]

Table structure
===============

To work with SAMD it is necessary to have the following table layout:

[[SAMD_Table_Structure.pdf]]

Data transfer
=============

On data communication the hash is not getting transfered. So the server can decide which algorithm to use. Following meta information are used during data transfer:

 * Change
       * MDEntry
           * Primary key
           * exists (boolean)
           * revision number
           * table name
       * RowData
           * the data for each column in the row (key value pair)

Synchronization flow
====================
[[SAMD_Ablauf.pdf]]

Hash calculation
================

The SAMD algorithm is database agnostic, since it only relies on ISO SQL commands. However, generating the hash for any given entry consumes both CPU power and time. As a result, the synchronization speed decreases linearly with the size of the database, because a new hash is generated for every changed entry.

The default hash algorithm is MD5.

As an alternative, the doubleganger framework can use SQL triggers that are specific to the database technology. They modify the original database to monitor its changes, hence replacing the message digest calculation.

Without triggers
----------------

When a synchronization is performed, the framework generates a hash for every row in the monitored tables, both on the client and on the server. It then compares it with the hash stored in the metadata table. If both don't match, a new change is added to the change list that will be sent to the other part and the new hash is stored into the metadata table.

With triggers
-------------

The doubleganger framework currently supports the use of triggers for MySQL and PostgreSQL databases. You can activate the triggers separately on the server and on the client (see [[Framework configuration]] for more details).

The triggers monitor changes to the databases and mirror them into the corresponding metadata tables. When a synchronization starts, the framework doesn't need to generate and compare hashes anymore, since every changed row is flagged as such by the triggers. While the hash overhead disappears, speeding up the synchronization process, the overall use of the monitored databases is slowed down, as every modification triggers a matching event in the metadata tables.

**Which solution should you implement?**

 1. if the data synchronization seldom happens and the one time overhead of calculating the message digest for every row is irrelevant, you should use Message Digest and avoid triggers.
 2. if the synchronization happens often and the constant overhead of triggers being activated at every change in your database is irrelevant, you should use triggers.


