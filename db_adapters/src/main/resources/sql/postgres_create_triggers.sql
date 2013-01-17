CREATE OR REPLACE FUNCTION %table%_update_flag() RETURNS trigger AS $BODY$
    BEGIN

        IF (CURRENT_USER = '%syncuser%')
        THEN
            RETURN NULL;
        END IF;

        IF (TG_OP = 'INSERT') THEN

            BEGIN
                INSERT INTO %table%%_md% (pk, f) VALUES (NEW.%pk%, 2);
            EXCEPTION
                -- the key already exists, so let's update
                -- see http://postgresql.1045698.n5.nabble.com/Howto-quot-insert-or-update-quot-td3276313.html
                WHEN unique_violation THEN
                    UPDATE %table%%_md% SET f = 1 WHERE pk = NEW.%pk%;
            END;
            RETURN NEW;

        ELSIF (TG_OP = 'UPDATE') THEN

            UPDATE %table%%_md% SET f = 1 WHERE pk = NEW.%pk% AND f = 0;
            RETURN NEW;

        ELSIF (TG_OP = 'DELETE') THEN

            UPDATE %table%%_md% SET f = -1 WHERE pk = OLD.%pk%;
            RETURN OLD;

        END IF;

        RETURN NULL;

    END;$BODY$
    LANGUAGE plpgsql;

CREATE TRIGGER trigger_%table% AFTER INSERT OR UPDATE OR DELETE
    ON %table%
    FOR EACH ROW
    EXECUTE PROCEDURE %table%_update_flag()