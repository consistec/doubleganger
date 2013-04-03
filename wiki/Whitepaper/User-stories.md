Listed below there are some user stories we defined for the syncframework:

 * the user/developer can easy extend the client side of framework to adapt any database
 * the user/developer can easy extend the server side of framework to adapt any database
 * the user/developer can configure synchronization options in a central place
 * the user/developer can configure each table to synchronize with a sync direction and conflict strategy
 * the user/developer can configure sync direction and conflict strategy in a global way
 * implemented 'client-proxies' can easy adapt to the framework e.g. just with configuration
 * implemented communication protocols can easy adapt to the framework
 * the user/developer can configure the retry of synchronization if an exception is thrown
 * detect and resolve conflicts dependent on configured conflict strategy
 * inform the user of conflicts and provide a possibility to resolve the conflicts by the user
 * the synchronization must let the data in a consistent state
 * the synchronization should have a good/accaptable performance

Example 1.)
-----------

A typical use case for the synchronization can be described as follow:

Person A updates some data in the server database. Person B updates some other data in his client databse.

The doubleganger framework now applies the data from person A to the client database and the data from person B to the server database. After synchronization the server and client databases are synchron and contains the datas of both persons.

 * initial db table state of person A and person B

| id | name       | description
|----|------------|------------
| 1  | beverages  | soft drinks
| 2  | condiments | Sweet and savory sauces

 * after updating data row of person A

| id | name       | description
|----|------------|------------
| 1  | beverages  | soft drinks**, coffees**
| 2  | condiments | Sweet and savory sauces

 * after updating data row of person B

| id | name       | description
|----|------------|------------
| 1  | beverages  | soft drinks
| 2  | condiments | Sweet and savory sauces**, relishes**

 * after synchronization the content of server and client database are synchron

| id | name       | description
|----|------------|------------
| 1  | beverages  | soft drinks**, coffees**
| 2  | condiments | Sweet and savory sauces**, relishes**

The above illustrated scenario is one possibility of data synchronization without any troubleshooting. But lets look to another more complicated example:

Example 2.)
-----------

Person A updates some data in the server database. Person B updates the same data in the client databse.

 * initial db table state of person A and person B

| id | name       | description
|----|------------|------------
| 1  | beverages  | soft drinks
| 2  | condiments | Sweet and savory sauces

 * after updating data row of person A

| id | name       | description
|----|------------|------------
| 1  | beverages  | soft drinks**, coffees**
| 2  | condiments | Sweet and savory sauces

 * after updating data row of person B

| id | name       | description
|----|------------|------------
| 1  | beverages  | soft drinks**, limonade**
| 2  | condiments | Sweet and savory sauces


Both, person A and person B modified the same row with id 1. That leads to a conflict situation which can be resolved with following possibilities (strategies):

 * SERVER_WINS - server modification will be applied on client and server side
 * CLIENT_WINS - client modification will be applied on client and server side and
 * USER_SELECTED - we let the user decide which change he wants to apply


CLIENT_WINS: after synchronization the content of server and client database are synchron 

| id | name       | description
|----|------------|------------
| 1  | beverages  | soft drinks**, coffees**
| 2  | condiments | Sweet and savory sauces

SERVER_WINS: after synchronization the content of server and client database are synchron

| id | name       | description
|----|------------|------------
| 1  | beverages  | soft drinks**, limonade**
| 2  | condiments | Sweet and savory sauces

There are a lot of other scenarios which leads to conflict situations for example a data row of the server database is updated and on the other hand the same data row on client database is deleted.

In the syncframework we defined a conflict strategy representing the above mentioned conflict resolving posibilities. Additionally the conflict strategy depends on the sync direction. If the internal state of conflict strategy and sync direction do not match the framework will throw an exception and the synchronization will be stopped. As illustrated in the followed table we can now decide on the basis of this definitions what to do in such conflict cases.

sync direction || conflict strategy|| conflict behaviour | apply non conflict changes | comment
----------------|-------------------|--------------------|----------------------------|---------
client | server | client | server   |                    |                            |         
**c -> s** | **c -> s** | **sw**     | **sw**       | **no change on server, conflict on client not possible** | **client data applied on server, server doesn't send changes to client** | not used, framework throws an exception
c -> s | c -> s | cw     | cw       | client data applied on server, conflict on client not possible | client data applied on server, server doesn't send changes to client | 
c -> s | c -> s | ud     | ud       |                    |                            |         
s -> c | s -> c | sw     | sw       | conflict on server not possible, server data applied on client | server data applied on client, client doesn't send changes to server | 
**s -> c** | **s -> c** | **cw**     | **cw**       | **conflict on server not possible, no change on client** | **server data applied on client, client doesn't send changes to server** | not used, framework throws an exception
s -> c | s -> c | ud     | ud       |                    |                            |         
bi     | bi     | sw     | sw       | no change on server, server data applied on client | client data applied on server and server data applied on client | 
bi     | bi     | cw     | cw       | no change on client, client data applied on server | client data applied on server and server data applied on client | 
bi     | bi     | ud     | ud       |                    |                            |         

Legend:

abbreviation | meaning
-------------|-----------------
c -> s       | client to server
s -> c       | server to client
bi           | bidirectional
sw           | server wins
cw           | client wins
ud           | user decision


