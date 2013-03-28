Overview
========

Filtered syncs should restrict the amount of data transmission when syncing large tables. The basic idea is to specify a filter criterion and to sync only those rows that match the filter. Filters should be separate for each table.

Problems
========

Filtered syncs (based on the current synchronization protocol) would lead to several problems when the filter criterion is being changed. Thus, we need to adapt the sync protocol; but before we go into details, let's start with a description of the possible problems.

Problem 1: Existence of out-of-data rows on the client
------------------------------------------------------

### Example

Consider the following situation

1. First, we sync **without** filter.
      * client revision of all entries is 5
2. Some entries on the server, which don't match the filter, are being changed
      * revision of these entries (server-side) is 6
3. Then, we sync again **with** filter
      * client revision is 6 although the client entries which don't match the filter still have the content from revision 5
4. Finally, we sync again **without** filter
      * client requests all changes since revision 6 and so the changes from step 2 are not applied on client-side

### Possible solution

When doing a filtered sync, we must delete all entries on the client that don't match the filter criterion

    this solution has the drawback that client-side changes might be deleted which have not yet been synced with the server, but this would be documented behavior (i.e., the user has to ensure that all client changes have been synchronized before changing the filter).
    this solution guarantees the following **invariant**: after a sync, **all** entries on the client are "up2date"

Problem 2: The new filter matches more rows on the server than the old filter
-----------------------------------------------------------------------------

### Example

Consider the following situation

1. First, we sync **with** filter A (at revision 5)
2. Second, we sync **without** filter or **with** filter **B** where **B** matches some entries which are not included in **A**
      * The important point is that this sync should contain some entries (let's call them **X**) which have not been synced in step 1 but were already present in the server data base at revision 5.
      * The second step would not send the entries **X** to the client because the server has no way to know that the client does not yet have these entries.

### Possible solution

This problem could be solved by implementing the following idea of **slow syncs**:

 * The first sync after changing a filter for a table needs to be a slow sync (but only for that table).
 * During a slow sync, the client does not only send its current revision number **i** but additionally the primary keys of all client-side entries for the affected tables.
      * So, the server can check whether there exist additional entries **X** which match the filter but are not present on the client.
 * In addition to the entries that have been changed since revision **i** the server will also send the entries from **X**.
 * Afterwards, the sync will continue as usual (client tries to merge the changes, resolves the conflicts and sends its changes to the server)
