CREATE DATABASE `${mysql_server_dbname}` DEFAULT CHARACTER SET latin1;
CREATE DATABASE `${mysql_client_dbname}` DEFAULT CHARACTER SET latin1;
GRANT ALL PRIVILEGES ON ${mysql_server_dbname}.* TO '${mysql_sync_user}' WITH GRANT OPTION;
GRANT ALL PRIVILEGES ON ${mysql_client_dbname}.* TO '${mysql_sync_user}' WITH GRANT OPTION;
FLUSH PRIVILEGES;