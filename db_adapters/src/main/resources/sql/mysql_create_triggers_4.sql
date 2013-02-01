CREATE TRIGGER `%table%_after_delete` AFTER DELETE ON `%table%`
    FOR EACH ROW
        IF (SELECT CURRENT_USER() <> '%syncuser%@localhost') THEN
            -- if it has been synced already, we just update the flag
            UPDATE %table%%md_suffix% SET %flag_md% = -1 WHERE %pk_md% = OLD.%pk_data%;
        END IF
