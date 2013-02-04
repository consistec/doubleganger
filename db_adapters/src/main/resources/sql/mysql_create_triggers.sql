DROP TRIGGER IF EXISTS `%table%_after_insert`;;
DROP TRIGGER IF EXISTS `%table%_after_update`;;
DROP TRIGGER IF EXISTS `%table%_after_delete`;;
CREATE TRIGGER `%table%_after_insert` AFTER INSERT ON `%table%`
    FOR EACH ROW
        IF (SELECT USER() NOT LIKE '%syncuser%@%') THEN
            INSERT INTO %table%%md_suffix% (%pk_md%, %flag_md%) VALUES (NEW.%pk_data%, 2)
            ON DUPLICATE KEY UPDATE %flag_md% = 1;
        END IF;;
CREATE TRIGGER `%table%_after_update` AFTER UPDATE ON `%table%`
    FOR EACH ROW
        IF (SELECT USER() NOT LIKE '%syncuser%@%') THEN
            UPDATE %table%%md_suffix% SET %flag_md% = 1 WHERE %pk_md% = NEW.%pk_data%;
        END IF;;
CREATE TRIGGER `%table%_after_delete` AFTER DELETE ON `%table%`
    FOR EACH ROW
        IF (SELECT USER() NOT LIKE '%syncuser%@%') THEN BEGIN
            -- if it's been deleted and never synced, the revision will be NULL
            -- no one needs to know about it, it sort of never existed - we delete it silently
            DELETE FROM %table%%md_suffix% WHERE %pk_md% = OLD.%pk_data% AND rev IS NULL;
            -- if it has been synced already, we just update the flag
            UPDATE %table%%md_suffix% SET %flag_md% = -1 WHERE %pk_md% = OLD.%pk_data%;
        END;
        END IF