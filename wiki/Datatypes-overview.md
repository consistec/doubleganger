PostgreSQL
==========

This overview shows which datatype could be synced from a PostgreSQL database to another PostgreSQL database using the ConsoleSyncClient.

Supported types
---------------

| Datatype                   | Converted to / Remark       |
|----------------------------|-----------------------------|
| bigint                     |                             |
| bigserial                  | bigint                      |
| "char"                     | character(1)                |
| character                  |                             |
| character varying          | length required             |
| date                       |                             |
| integer                    |                             |
| numeric                    | length/precision is ignored |
| oid                        | bigint                      |
| real                       | 	                           |
| serial                     | integer                     |
| timestamp without timezone |                             |
| timestamp with timezone    | timestamp without timezone  |
| time without timezone      |                             |
| time with timezone         | time without timezone       |

Unsupported types
-----------------

| Datatype         | Converted to / Remark |  | Datatype      | Converted to / Remark | 
|------------------|-----------------------|--|---------------|-----------------------|
| abstime          |                       |  | polygon       |                       | 
| aclitem          |                       |  | refcursor     |                       |
| bit              |                       |  | regclass      |                       | 
| bit varying      |                       |  | regconfig     |                       | 
| boolean          |                       |  | regdictionary |                       | 
| box              |                       |  | regoper       |                       | 
| bytea            |                       |  | regoperator   |                       | 
| cid              |                       |  | regproc       |                       | 
| cidr             |                       |  | regprocedure  |                       | 
| circle           |                       |  | regtype       |                       | 
| cstring[]        |                       |  | reltime       |                       | 
| double precision | DOUBLE                |  | smallint      |                       | 
| gtsvector        |                       |  | smgr          |                       | 
| inet             |                       |  | text          | VARCHAR(2147483647)   | 
| int2vector       |                       |  | tid           |                       | 
| interval         |                       |  | tinterval     |                       | 
| line             |                       |  | tsquery       |                       | 
| lseg             |                       |  | tsvector      |                       | 
| macaddr          |                       |  | txid_snapshot |                       | 
| money            | DOUBLE                |  | uuid	      |                       | 
| name             | VARCHAR(2147483647)   |  | xid           |                       | 
| oidvector        |                       |  | xml           |                       | 
| path             |                       |  | \<anytype\>[] |                       | 
| pg_node_tree     |                       |  |               |                       | 
| point            |                       |  |               |                       | 


