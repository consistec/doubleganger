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