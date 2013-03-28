Dependencies
============

 * maven3
 * jdk7
 * postgresql database (to run tests)
 * mysql database (to run tests)
 * Android SDK (set `ANDROID_HOME` or pass `-Dandroid.sdk.path=XXX` to the maven calls) 
 * git

Database setup
==============

The Unit tests are working with normal databases. This databases must be setup by the user before the first run. This step-by-step guide shows you how to configure your DB:

MySQL
-----
 * create database "server" 
 * create database "client" 
 * add user and assign privileges to access and write both "server" and "client" databases
```bash
      username:mysql 
      password:mysql 
```
 * add user and assign privileges to access and write both "server" and "client" databases
```bash
      username:extern 
      password:extern 
```

PostgreSQL
----------
 * create database "server" 
 * create database "client" 
 * add user and assign privileges to access and write both "server" and "client" databases
```bash
       username:syncuser 
       password:syncuser 
```
 * add user and assign privileges to access and write both "server" and "client" databases
```bash
       username:extern 
       password:extern 
```

**You can change these values and reflect the modifications in the file `db_adapters/pom.xml`:**
```xml
<properties>
    ...
    <mysql_server_dbname>server</mysql_server_dbname>
    <mysql_client_dbname>client</mysql_client_dbname>
    <mysql_sync_user>mysql</mysql_sync_user>
    <mysql_sync_pwd>mysql</mysql_sync_pwd>
    <mysql_extern_user>extern</mysql_extern_user>
    <mysql_extern_pwd>extern</mysql_extern_pwd>
    ...
</properties>
```

Step-by-step
============

Install required prerequisites
------------------------------

```bash
# aptitude install maven postgresql mysql-server git
```

Setup databases to run tests
----------------------------
```bash
root@debian-vm:/# su - postgres
postgres@debian-vm:~$ psql -d template1 -U postgres
template1=# CREATE USER syncuser WITH PASSWORD 'syncuser';
template1=# CREATE DATABASE client;
template1=# CREATE DATABASE server;
template1=# GRANT ALL PRIVILEGES ON DATABASE client to syncuser;
template1=# GRANT ALL PRIVILEGES ON DATABASE server to syncuser;
template1=# \q
postgres@debian-vm:~$ exit

root@debian-vm:/home/consistec# mysql -u root -p
mysql> create database client;
mysql> create database server;
mysql> GRANT ALL PRIVILEGES ON *.* TO 'mysql'@'localhost' IDENTIFIED BY 'mysql' WITH GRANT OPTION;
mysql> exit;
```

Setup android sdk
-----------------
```bash
root@debian-vm:/# cd /opt/
root@debian-vm:/# wget http://dl.google.com/android/android-sdk_r20.0.1-linux.tgz
root@debian-vm:/# tar -xf android-sdk_r20.0.1-linux.tgz
root@debian-vm:/# export ANDROID_HOME=/opt/android-sdk-linux/
```

Setup AVD to run Android tests on
---------------------------------

```bash
consistec@debian-vm:~$ /opt/android-sdk-linux/tools/android create avd -n TestDevice -t android-10 -c 128M
```

Checkout the source code
------------------------
```bash
git clone gitosis@bigmama1.ads.consistec.de:syncframework.git
cd syncframework/
```

Build the framework
-------------------
```bash
mvn install
```

OPTIONAL: without Database setup
```bash
mvn install -DskipTests
```
OPTIONAL: with Android tests
```bash
mvn install -PandroidTest
```

