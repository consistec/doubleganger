---
-- #%L
-- Project - doppelganger
-- File - create_dbs_postgresql.sql
-- %%
-- Copyright (C) 2011 - 2013 consistec GmbH
-- %%
-- This program is free software: you can redistribute it and/or modify
-- it under the terms of the GNU General Public License as
-- published by the Free Software Foundation, either version 3 of the 
-- License, or (at your option) any later version.
-- 
-- This program is distributed in the hope that it will be useful,
-- but WITHOUT ANY WARRANTY; without even the implied warranty of
-- MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
-- GNU General Public License for more details.
-- 
-- You should have received a copy of the GNU General Public 
-- License along with this program.  If not, see
-- <http://www.gnu.org/licenses/gpl-3.0.html>.
-- #L%
---
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