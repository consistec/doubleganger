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
