CREATE TRIGGER `%table%_after_insert` AFTER INSERT ON `%table%`
    FOR EACH ROW
        IF (SELECT CURRENT_USER() <> '%syncuser%@localhost') THEN
            INSERT INTO %table%%md_suffix% (%pk_md%, %flag_md%) VALUES (NEW.%pk_data%, 2)
            ON DUPLICATE KEY UPDATE %flag_md% = 1;
        END IF