--CREATE LANGUAGE 'plpgsql';

DROP TABLE IF EXISTS items;
DROP TABLE IF EXISTS items_md;
DROP TABLE IF EXISTS categories;
DROP TABLE IF EXISTS categories_md;

CREATE TABLE categories
    (
        categoryid integer NOT NULL,
        categoryname character varying(30000),
        description character varying(30000),
        CONSTRAINT categories_pkey PRIMARY KEY (categoryid)
    )
    WITH (
        OIDS=FALSE
    );
ALTER TABLE categories
    OWNER TO syncuser;

CREATE TABLE items
    (
        itemid integer NOT NULL,
        itemname character varying(30000),
        description character varying(30000),
        CONSTRAINT items_pkey PRIMARY KEY (itemid)
    )
    WITH (
        OIDS=FALSE
    );
ALTER TABLE items
    OWNER TO syncuser;

CREATE TABLE categories_md
    (
        pk integer NOT NULL,
        f integer NOT NULL,
        rev integer NOT NULL DEFAULT 1,
        mdv character varying(30000) DEFAULT NULL,
        CONSTRAINT categories_md_pkey PRIMARY KEY (pk)
    )
    WITH (
        OIDS=FALSE
    );
ALTER TABLE categories_md
    OWNER TO syncuser;

CREATE TABLE items_md
    (
        pk integer NOT NULL,
        f integer NOT NULL,
        rev integer NOT NULL DEFAULT 1,
        mdv character varying(30000) DEFAULT NULL,
        CONSTRAINT items_md_pkey PRIMARY KEY (pk)
    )
    WITH (
        OIDS=FALSE
    );
ALTER TABLE items_md
    OWNER TO syncuser;


CREATE OR REPLACE FUNCTION items_update_flag() RETURNS trigger AS $BODY$
    BEGIN

        IF (TG_OP = 'INSERT') THEN

            BEGIN
                INSERT INTO items_md (pk, f) VALUES (NEW.itemid, 2);
            EXCEPTION
                -- the key already exists, so let's update
                -- see http://postgresql.1045698.n5.nabble.com/Howto-quot-insert-or-update-quot-td3276313.html
                WHEN unique_violation THEN
                    UPDATE items_md SET f = 1 WHERE pk = NEW.itemid;
            END;
            RETURN NEW;

        ELSIF (TG_OP = 'UPDATE') THEN

            UPDATE items_md SET f = 1 WHERE pk = NEW.itemid AND f = 0;
            RETURN NEW;

        ELSIF (TG_OP = 'DELETE') THEN

            UPDATE items_md SET f = -1 WHERE pk = OLD.itemid;
            RETURN OLD;

        END IF;

        RETURN NULL;

    END;$BODY$
    LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION categories_update_flag() RETURNS trigger AS $BODY$
    BEGIN

        IF (TG_OP = 'INSERT') THEN

            BEGIN
                INSERT INTO categories_md (pk, f) VALUES (NEW.categoryid, 2);
            EXCEPTION
                -- the key already exists, so let's update
                WHEN unique_violation THEN
                    UPDATE categories_md SET f = 1 WHERE pk = NEW.categoryid;
            END;
            RETURN NEW;

        ELSIF (TG_OP = 'UPDATE') THEN

            UPDATE categories_md SET f = 1 WHERE pk = NEW.categoryid AND f = 0;
            RETURN NEW;

        ELSIF (TG_OP = 'DELETE') THEN

            UPDATE categories_md SET f = -1 WHERE pk = OLD.categoryid;
            RETURN OLD;

        END IF;

        RETURN NULL;

    END;$BODY$
    LANGUAGE plpgsql;


CREATE TRIGGER trigger_items AFTER INSERT OR UPDATE OR DELETE
    ON items
    FOR EACH ROW
    EXECUTE PROCEDURE items_update_flag();

CREATE TRIGGER trigger_categories AFTER INSERT OR UPDATE OR DELETE
    ON categories
    FOR EACH ROW
    EXECUTE PROCEDURE categories_update_flag();



INSERT INTO categories (categoryid, categoryname, description) VALUES
    (1, 'Beverages', 'Soft drinks, coffees, teas, beers, and ales');

INSERT INTO categories (categoryid, categoryname, description) VALUES
    (2, 'Condiments', 'Sweet and savory sauces, relishes, spreads, and seasonings');

INSERT INTO categories (categoryid, categoryname, description) VALUES
    (3, 'Confections', 'Desserts, candies, and sweet breads');

UPDATE categories SET categoryname = 'Drinks' WHERE categoryname = 'Beverages';

UPDATE categories SET categoryname = 'Sweets' WHERE categoryname = 'Confections';

DELETE FROM categories WHERE categoryid = 2;
DELETE FROM categories WHERE categoryid = 3;

INSERT INTO categories (categoryid, categoryname, description) VALUES
    (2, 'Condiments', 'Sweet and savory sauces, relishes, spreads, and seasonings');