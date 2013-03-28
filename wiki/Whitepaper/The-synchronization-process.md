The process of synchronization consists of 5 logical separated phases:

- phase 1 validation and initialization phase (validation of settings, db schema creation)

- phase 2 'determine latest client revision and getting server changes'

- phase 3 'getting client changes, resolve conflicts and apply server changes'

- phase 4 'apply client changes on server, update phase (updating revisions on client)'

- phase 5 'updating client revisions'

Phase 1 - Validation and initialization phase
---------------------------------------------

As there are settings that can be different on client side then on server side it is necessary to validate these on server side. At time the table sync strategies and database tables to sync can be different. After the validation process the db schema is checked and if necessary some essential special data tables will be created. On validation/inizialization error the synchronization will be stopped and the client gets an synchronization exception.

Phase 2 – getting server changes
--------------------------------

After validation the client determines its latest revision. On base of this revision he requests all available server changes.

Phase 3 – getting client changes, resolve conflicts and apply server changes
----------------------------------------------------------------------------

After phase 2, the client retrieves its own changes. There are two possibilities retrieving changes: - changes are marked with sql-trigger and marked as changed in db - changes can be determines on the basis of hash values The results are two change sets with these the conflict resolution can start. During the resolving the conflicts the client change set will be cleaned from conflicts so the output will be a set of changes to apply on client. Now the client applies the 'cleaned' change set on his database.

Phase 4 – applying client changes
---------------------------------

The 4th transaction phase applies the client changes on the server. After updating the server database the server sends the next (newest) revision back to the client.

Phase 5 - updating client revisions
-----------------------------------

The last phase updates the revisions on client changes with the next server revision.

Transaction phases
------------------

There are two transaction phases to ensure the consistensy on client and server databases. To avoid a bottle neck on server's side, the transaction phases should be as short as possible. Due to this reason the server not starts the transaction until all necessary client information are available. If an error occurs during one of the transaction phases the transaction will be rolled back and all changes will be revoked to hold the databases in conistent state.

The following picture illustrates the synchronization process:

[[synchronization.jpg]]
