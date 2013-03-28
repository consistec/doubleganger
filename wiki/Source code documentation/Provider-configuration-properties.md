Keys and their values
=====================
| Key               | Value                                           |
|-------------------|-------------------------------------------------|
| `dbDriverName`    | The class name for the specific driver          |
| `dbConnectionUrl` | the jdbc connection url                         |
| `dbUsername`      | database username                               | 
| `dbPassword`      | database password for username                  | 
| `syncTables`      | comma separated list with tables to synchronize | 

Sample config
=============
```bash
dbDriverName=org.postgresql.Driver
dbConnectionUrl=jdbc:postgresql://localhost/server
dbUsername=syncuser
dbPassword=syncuser
syncTables=categories
```
