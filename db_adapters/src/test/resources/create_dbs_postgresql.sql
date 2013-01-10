CREATE DATABASE ${postgres_server_dbname}
  WITH OWNER = ${postgres_sync_user}
       ENCODING = 'UTF8'
       TABLESPACE = pg_default
       LC_COLLATE = 'de_DE.UTF-8'
       LC_CTYPE = 'de_DE.UTF-8'
       CONNECTION LIMIT = -1;

CREATE DATABASE ${postgres_client_dbname}
    WITH OWNER = ${postgres_sync_user}
         ENCODING = 'UTF8'
         TABLESPACE = pg_default
         LC_COLLATE = 'de_DE.UTF-8'
         LC_CTYPE = 'de_DE.UTF-8'
         CONNECTION LIMIT = -1;