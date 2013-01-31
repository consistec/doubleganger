CREATE TRIGGER `%table%_after_update` AFTER UPDATE ON `%table%`
    FOR EACH ROW
        IF (SELECT CURRENT_USER() <> '%syncuser%@localhost') THEN
            UPDATE %table%%md_suffix% SET %flag_md% = 1 WHERE %pk_md% = NEW.%pk_data%;
        END IF