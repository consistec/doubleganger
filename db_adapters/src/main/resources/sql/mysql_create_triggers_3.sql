CREATE TRIGGER `%table%_before_delete` BEFORE DELETE ON `%table%`
    FOR EACH ROW
        IF (SELECT CURRENT_USER() <> '%syncuser%@localhost') THEN
            -- if it's been deleted and never synced, the revision will be NULL
            -- no one needs to know about it, it sort of never existed - we delete it silently
            DELETE FROM %table%%md_suffix% WHERE %pk_md% = OLD.%pk_data% AND rev IS NULL;
        END IF