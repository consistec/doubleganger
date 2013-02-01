---
-- #%L
-- doppelganger
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
BEGIN;
    DROP TABLE IF EXISTS categories;
    DROP TABLE IF EXISTS items;

    CREATE TABLE categories
    (
        categoryid integer NOT NULL,
        categoryname character varying(30000),
        description character varying(30000),
        CONSTRAINT categories_pkey PRIMARY KEY (categoryid )
    )
    WITH (
        OIDS=FALSE
    );
    ALTER TABLE categories
        OWNER TO ${postgres_sync_user};

    CREATE TABLE items
    (
        itemid integer NOT NULL,
        itemname character varying(30000),
        description character varying(30000),
        CONSTRAINT items_pkey PRIMARY KEY (itemid )
    )
    WITH (
        OIDS=FALSE
    );
    ALTER TABLE items
        OWNER TO ${postgres_sync_user};
COMMIT;
