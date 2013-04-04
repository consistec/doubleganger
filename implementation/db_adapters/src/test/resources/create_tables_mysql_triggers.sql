---
-- #%L
-- Project - doubleganger
-- File - create_tables_mysql_triggers.sql
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
DELIMITER $$

    CREATE TABLE categories
    (
        categoryid integer NOT NULL PRIMARY KEY,
        categoryname VARCHAR(30000),
        description VARCHAR(30000),
    );
    ALTER TABLE categories
        OWNER TO ${postgres_sync_user};

    CREATE TABLE items
    (
        itemid integer NOT NULL PRIMARY KEY,
        itemname VARCHAR(30000),
        description VARCHAR(30000),
    );
    ALTER TABLE items
        OWNER TO ${postgres_sync_user};

    CREATE
        TRIGGER `categories_after_insert` AFTER INSERT
        ON `categories`
        FOR EACH ROW
            INSERT INTO categories_md (pk, f) VALUES (NEW.categoryid, 2);

    CREATE
        TRIGGER `categories_after_update` AFTER UPDATE
        ON `categories`
        FOR EACH ROW
            UPDATE categories_md SET f = 1 WHERE pk = NEW.categoryid;

    CREATE
        TRIGGER `categories_after_delete` AFTER DELETE
        ON `categories`
        FOR EACH ROW
            UPDATE categories_md SET f = -1 WHERE pk = NEW.categoryid;

    CREATE
        TRIGGER `items_after_insert` AFTER INSERT
        ON `items`
        FOR EACH ROW
            INSERT INTO items_md (pk, f) VALUES (NEW.itemid, 2);

    CREATE
        TRIGGER `items_after_update` AFTER UPDATE
        ON `items`
        FOR EACH ROW
            UPDATE items_md SET f = 1 WHERE pk = NEW.itemid;

    CREATE
        TRIGGER `items_after_delete` AFTER DELETE
        ON `items`
        FOR EACH ROW
            UPDATE items_md SET f = -1 WHERE pk = NEW.itemid;

$$

DELIMITER ;