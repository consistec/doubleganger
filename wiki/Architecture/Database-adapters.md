The DatabaseAdapter part is implemented like a service provider.

Classes and their functions
===========================

Class                   | Function
------------------------|---------
IDatabaseAdapter        | Abstract service interface. This class contains general methods and initialization routines for normal database connections.
DatabaseAdapterCallback | This callback is used to transfer data to client / server sync providers and but get the database adapter the possibility to close the resultsets database connections etc.
DatabaseAdapterFactory  | The factory abstracts the database adapter creation from the calling class. The Factory decides which adapter should be created.
GenericDatabaseAdapter  | This is the generic implementation of the IDatabaseAdapter.
Adapter implementations | Other implementations like PostgresDatabaseAdapter, MysqlDatabaseAdapter, ... should extend the GenericDatabaseAdapter and override only these methods which differ from the generic implementation or can extend the ADatabaseAdapter and implement all abstract methods.

DatabaseAdapter implementations are loaded with reflection from the classpath.

Triggers
========

MySQL: http://dev.mysql.com/doc/refman/5.0/en/triggers.html

PostgreSQL: http://www.postgresql.org/docs/8.2/static/sql-createtrigger.html

SQLite: http://www.sqlite.org/lang_createtrigger.html

The triggers do not track the modifications made by the sync user (`syncuser`) during synchronization: only external modifications are marked as such. That is why we created an extern user for the test (`extern`).

Some Good Reasons to Use Triggers
---------------------------------
There are several very good reasons to use triggers, including:

 * to audit the changes of data in a database table
 * to derive additional data that is not available within a table or within the database. For example, when an update occurs to the quantity column of a product table, you can calculate the corresponding value of the total_price column.
 * to enforce referential integrity.  For example, when you delete a customer you can use a trigger to delete corresponding rows in the orders table.
 * to guarantee that when a specific operation is performed, related actions are performed.
 * for centralized, global operations that should be fired for the triggering statement, regardless of which user or database application issues the statement.

Some Good Reasons Not to Use Triggers
-------------------------------------

There are some equally valid reasons to not use triggers, including:

 * they may add workload to the database and cause the system to run slower because they are executed for every user every time the event occurs on which the trigger is created.
 * SQL Triggers execute invisibly from client-application, which connects to the database server so it is difficult to figure out what happened in the underlying database layer.
 * **Triggers are activated by SQL statements ONLY. They are not activated by changes in tables made by APIs that do not transmit SQL statements to the MySQL Server.**

You should also bear in mind that, if the logic for your trigger requires much more than 60 lines of SQL code, it’s usually better to include most of the code in a stored procedure and call the procedure from the trigger.

Quoted from the [Database Journal](http://www.databasejournal.com/features/mysql/the-wonderful-and-not-so-wonderful-things-about-mysql-triggers.html).

Example in PostgreSQL
---------------------
```bash
CREATE OR REPLACE FUNCTION categories_update_flag() RETURNS trigger AS $BODY$
    BEGIN
        IF (CURRENT_USER = 'syncuser') THEN 
            RETURN NULL; 
        END IF;

        IF (TG_OP = 'INSERT') THEN
            BEGIN
                INSERT INTO categories_md (pk, f) VALUES (NEW.categoryid, 2);
            EXCEPTION
                -- the key already exists, so let's update
                -- see http://postgresql.1045698.n5.nabble.com/Howto-quot-insert-or-update-quot-td3276313.html
                WHEN unique_violation THEN
                    UPDATE categories_md SET f = 1 WHERE pk = NEW.categoryid;
            END;
            RETURN NEW;
        ELSIF (TG_OP = 'UPDATE') THEN
            UPDATE categories_md SET f = 1 WHERE pk = NEW.categoryid AND f = 0;
            RETURN NEW;
        ELSIF (TG_OP = 'DELETE') THEN
            -- if it's been deleted and never synced, the revision will be NULL
            -- no one needs to know about it, it sort of never existed - we delete it silently
            DELETE FROM categories_md WHERE pk = OLD.categoryid AND rev IS NULL;
            IF NOT FOUND THEN
                UPDATE categories_md SET f = -1 WHERE pk = OLD.categoryid;
            END IF;
            RETURN OLD;
        END IF;
        RETURN NULL;
    END;$BODY$
    LANGUAGE plpgsql;

CREATE TRIGGER trigger_categories AFTER INSERT OR UPDATE OR DELETE
    ON "categories"
    FOR EACH ROW
    EXECUTE PROCEDURE categories_update_flag();
```
