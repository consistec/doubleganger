General information
===================

In the repository is a maven project called "ConsoleSyncClient". This project builds an elementary client for synchronization. The structure of the property files can be found here.

```bash
usage: java -jar ConsoleSyncClient.jar <options>
This tool immediately starts a synchronization with the given properties.
 -c,--client-settings <arg>   Properties file with settings for connection
 -o,--output <arg>            Write program output to file
 -s,--server-settings <arg>   Properties file with settings for connection
```

Build
=====

```bash
mvn install
```

Run
===
```bash
markus@pc15e:~$ java -jar ConsoleSyncClient-0.0.1-SNAPSHOT.jar -c client.properties -s server.properties
```
