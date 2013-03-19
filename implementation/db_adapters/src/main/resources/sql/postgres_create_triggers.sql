CREATE OR REPLACE FUNCTION %table%_update_flag() RETURNS trigger AS $BODY$
    BEGIN

        IF (CURRENT_USER = '%syncuser%')
        THEN
            RETURN NULL;
        END IF;

        IF (TG_OP = 'INSERT') THEN

            BEGIN
                INSERT INTO %table%%md_suffix% (%pk_md%, %flag_md%) VALUES (NEW.%pk_data%, 2);
            EXCEPTION
                -- the key already exists, so let's update
                -- see http://postgresql.1045698.n5.nabble.com/Howto-quot-insert-or-update-quot-td3276313.html
                WHEN unique_violation THEN
                    UPDATE %table%%md_suffix% SET %flag_md% = 1 WHERE %pk_md% = NEW.%pk_data%;
            END;
            RETURN NEW;

        ELSIF (TG_OP = 'UPDATE') THEN

            UPDATE %table%%md_suffix% SET %flag_md% = 1 WHERE %pk_md% = NEW.%pk_data%;
            RETURN NEW;

        ELSIF (TG_OP = 'DELETE') THEN
            -- if it's been deleted and never synced, the revision will be NULL
            -- no one needs to know about it, it sort of never existed - we delete it silently
            DELETE FROM %table%%md_suffix% WHERE %pk_md% = OLD.%pk_data% AND rev IS NULL;

            IF NOT FOUND THEN
                -- if it has been synced already, we just update the flag
                UPDATE %table%%md_suffix% SET %flag_md% = -1 WHERE %pk_md% = OLD.%pk_data%;
            END IF;

            RETURN OLD;

        END IF;

        RETURN NULL;

    END;$BODY$
    LANGUAGE plpgsql;

CREATE TRIGGER trigger_%table% AFTER INSERT OR UPDATE OR DELETE
    ON %table%
    FOR EACH ROW
    EXECUTE PROCEDURE %table%_update_flag()